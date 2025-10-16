package com.jhf.smartcampusmanagementsystem

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.jhf.smartcampusmanagementsystem.Adapter.NoticeAdapter
import com.jhf.smartcampusmanagementsystem.Data.Notice
import java.text.SimpleDateFormat
import java.util.*

class Student_Dashboard : Fragment() {

    private lateinit var todayAdapter: HorizontalAdapter
    private val todayClasses = mutableListOf<RecyclerViewModal>()
    private lateinit var tvNoTodayClass: android.widget.TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var rvAssignments: RecyclerView

    private lateinit var rvSchoolNotice: RecyclerView
    private val noticeList = mutableListOf<Notice>()
    private lateinit var noticeAdapter: NoticeAdapter

    private lateinit var btnViewAllNotices: android.widget.TextView

    companion object {
        val noticeColors = listOf("#2196F3", "#4CAF50", "#9C27B0", "#009688", "#795548")
        private val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student__dashboard, container, false)

        tvNoTodayClass = view.findViewById(R.id.tvNoTodayClass)
        recyclerView = view.findViewById(R.id.recyclerView)
        rvSchoolNotice = view.findViewById(R.id.rvSchoolNotice)
        btnViewAllNotices = view.findViewById(R.id.btnViewAllNotices)
        rvAssignments = view.findViewById(R.id.rvAssignments)

        // Today's classes
        todayAdapter = HorizontalAdapter(todayClasses, requireContext())
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = todayAdapter

        // Load class name from SharedPreferences (not classId)
        val sharedPref = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val savedClassName = sharedPref.getString("class", null)?.trim()
        Log.d("DASHBOARD_DEBUG", "Saved class name: $savedClassName")

        if (!savedClassName.isNullOrEmpty()) {
            loadTodayClasses(savedClassName)
        } else {
            tvNoTodayClass.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }

        // Assignments (dummy data)
        val assignmentList = listOf(
            GridViewModel("Assignment 1","Math","14 Nov, 8 AM","9/10","Hand In"),
            GridViewModel("Assignment 2","Chemistry","14 Nov, 8 AM","10/10","Hand In"),
            GridViewModel("Assignment 3","Physics","15 Nov, 9 AM","8/10","Hand In")
        )
        rvAssignments.layoutManager = GridLayoutManager(requireContext(), 2)
        rvAssignments.adapter = GridAdapter_Student_ClsToday(assignmentList, requireContext())

        // Notices
        noticeAdapter = NoticeAdapter(noticeList)
        rvSchoolNotice.layoutManager = LinearLayoutManager(requireContext())
        rvSchoolNotice.adapter = noticeAdapter
        loadSchoolNotices()

        // View All Notices click
        btnViewAllNotices.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, AllNoticesFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun loadTodayClasses(className: String) {
        val database = FirebaseDatabase.getInstance().getReference("classes")

        // Case-insensitive search for className
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var classSnapshot: DataSnapshot? = null

                for (child in snapshot.children) {
                    val firebaseClassName = child.child("className").getValue(String::class.java) ?: ""
                    if (firebaseClassName.equals(className, ignoreCase = true)) {
                        classSnapshot = child
                        break
                    }
                }

                if (classSnapshot == null) {
                    tvNoTodayClass.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    return
                }

                // Populate today's classes
                todayClasses.clear()
                val calendar = Calendar.getInstance()
                val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) ?: "Sunday"

                val subjectsSnap = classSnapshot.child("subjects")
                for(subjectSnap in subjectsSnap.children){
                    val subjectName = subjectSnap.child("subjectName").getValue(String::class.java) ?: ""
                    val teacherName = subjectSnap.child("teacherName").getValue(String::class.java) ?: ""
                    val roomNumber = subjectSnap.child("roomNumber").getValue(String::class.java) ?: ""

                    val scheduleSnap = subjectSnap.child("schedule")
                    for(scheduleItem in scheduleSnap.children){
                        val day = scheduleItem.child("day").getValue(String::class.java) ?: ""
                        val time = scheduleItem.child("time").getValue(String::class.java) ?: ""

                        val dayName = when(day.lowercase()){
                            "sun","sunday" -> "Sunday"
                            "mon","monday" -> "Monday"
                            "tue","tues","tuesday" -> "Tuesday"
                            "wed","wednesday" -> "Wednesday"
                            "thu","thur","thursday" -> "Thursday"
                            "fri","friday" -> "Friday"
                            "sat","saturday" -> "Saturday"
                            else -> continue
                        }

                        if(dayName == dayOfWeek){
                            val randomColor = Color.parseColor(noticeColors.random())
                            todayClasses.add(RecyclerViewModal(subjectName, randomColor, time, "Room $roomNumber", teacherName))
                        }
                    }
                }

                todayAdapter.notifyDataSetChanged()
                tvNoTodayClass.visibility = if(todayClasses.isEmpty()) View.VISIBLE else View.GONE
                recyclerView.visibility = if(todayClasses.isEmpty()) View.GONE else View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                tvNoTodayClass.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })
    }

    private fun loadSchoolNotices() {
        val dbRef = FirebaseDatabase.getInstance().getReference("notices").child("student")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noticeList.clear()
                if (!snapshot.exists()) return

                for (noticeSnap in snapshot.children) {
                    val notice = noticeSnap.getValue(Notice::class.java)
                    notice?.let { noticeList.add(it) }
                }

                // Sort by timestamp descending
                noticeList.sortByDescending { it.timestamp }

                noticeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load notices", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
