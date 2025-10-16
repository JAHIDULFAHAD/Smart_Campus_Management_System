package com.jhf.smartcampusmanagementsystem

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.jhf.smartcampusmanagementsystem.Adapter.NoticeAdapter
import com.jhf.smartcampusmanagementsystem.Adapter.TeacherClassAdapter
import com.jhf.smartcampusmanagementsystem.Data.ClassItem
import com.jhf.smartcampusmanagementsystem.Data.Notice
import java.text.SimpleDateFormat
import java.util.*


class Teacher_Dashboard_Fragment : Fragment() {

    private var teacherId: String? = null

    private lateinit var recyclerClasses: RecyclerView
    private lateinit var classesAdapter: TeacherClassAdapter
    private val todayClassesList = mutableListOf<TeacherClassModel>()

    private lateinit var recyclerNotices: RecyclerView
    private lateinit var noticesAdapter: NoticeAdapter
    private val noticeList = mutableListOf<Notice>()

    private val dbClasses = FirebaseDatabase.getInstance().getReference("classes")
    private val dbNotices = FirebaseDatabase.getInstance().getReference("notices").child("teacher")

    companion object {
        private val noticeColors = listOf("#2563EB", "#4CAF50", "#F59E0B", "#EF4444", "#9C27B0")
        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        private val dayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teacherId = arguments?.getString("teacherId")
        Log.d("TeacherDashboard", "TeacherId: $teacherId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_teacher__dashboard_, container, false)

        recyclerClasses = view.findViewById(R.id.recyclerTodayClasses)
        recyclerNotices = view.findViewById(R.id.recyclerTeacherNotices)

        // Setup Classes RecyclerView
        recyclerClasses.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        classesAdapter = TeacherClassAdapter(todayClassesList)
        recyclerClasses.adapter = classesAdapter
        recyclerClasses.isNestedScrollingEnabled = false

        // Setup Notices RecyclerView
        recyclerNotices.layoutManager = LinearLayoutManager(requireContext())
        noticesAdapter = NoticeAdapter(noticeList)
        recyclerNotices.adapter = noticesAdapter
        recyclerNotices.isNestedScrollingEnabled = false

        teacherId?.let {
            loadTodayClasses(it)
            loadTeacherNotices()
        }

        return view
    }

    private fun getCurrentDay(): String = dayFormat.format(Date())

    private fun loadTodayClasses(teacherId: String) {
        val currentDay = getCurrentDay()
        dbClasses.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                todayClassesList.clear()

                for (classSnap in snapshot.children) {
                    val classItem = classSnap.getValue(ClassItem::class.java) ?: continue
                    for (subject in classItem.subjects ?: emptyList()) {
                        val scheduleToday = subject.schedule?.find { it.day.equals(currentDay, true) }
                        if (subject.teacherId == teacherId && scheduleToday != null) {
                            todayClassesList.add(
                                TeacherClassModel(
                                    className = classItem.className ?: "N/A",
                                    subjectName = subject.subjectName ?: "N/A",
                                    room = subject.roomNumber ?: "N/A",
                                    time = scheduleToday.time ?: "No Class Today"
                                )
                            )
                        }
                    }
                }

                classesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TeacherDashboard", "Failed to load classes: ${error.message}")
            }
        })
    }

    private fun loadTeacherNotices() {
        dbNotices.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noticeList.clear()

                if (!snapshot.exists()) {
                    Log.d("TeacherDashboard", "No teacher notices found")
                    noticesAdapter.notifyDataSetChanged()
                    return
                }

                for (noticeSnap in snapshot.children) {
                    val notice = noticeSnap.getValue(Notice::class.java)
                    notice?.let {
                        noticeList.add(it)
                        Log.d("TeacherDashboard", "Notice loaded: ${it.title} | ${it.description} | ${it.timestamp}")
                    }
                }

                // Sort by timestamp descending
                noticeList.sortByDescending { it.timestamp }

                // Assign stable colors
                noticeList.forEachIndexed { index, notice ->
                    val color = noticeColors[index % noticeColors.size]
                    // Store color in notice or handle in adapter if needed
                }

                noticesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TeacherDashboard", "Failed to load teacher notices: ${error.message}")
            }
        })
    }
}
