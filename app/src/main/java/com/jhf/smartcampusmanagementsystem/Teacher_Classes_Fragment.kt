package com.jhf.smartcampusmanagementsystem

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.jhf.smartcampusmanagementsystem.Data.SubjectSchedule

class Teacher_Classes_Fragment : Fragment() {

    private lateinit var todayRecycler: RecyclerView
    private lateinit var weeklyRecycler: RecyclerView
    private lateinit var tvNoToday: TextView
    private lateinit var tvNoWeekly: TextView

    private val todayClasses = mutableListOf<SubjectSchedule>()
    private val weeklyClasses = mutableListOf<SubjectSchedule>()

    private lateinit var teacherId: String
    private lateinit var todayAdapter: TodayClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teacherId = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            .getString("user_id", "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_teacher__classes_, container, false)
        todayRecycler = view.findViewById(R.id.rvTodayClasses)
        weeklyRecycler = view.findViewById(R.id.rvWeeklyClasses)
        tvNoToday = view.findViewById(R.id.tvNoToday)
        tvNoWeekly = view.findViewById(R.id.tvNoWeekly)

        todayRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        weeklyRecycler.layoutManager = LinearLayoutManager(requireContext())

        todayAdapter = TodayClassAdapter(todayClasses)
        todayRecycler.adapter = todayAdapter

        loadClassesFromFirebase()

        return view
    }

    private fun loadClassesFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("classes")
        dbRef.get().addOnSuccessListener { snapshot ->
            todayClasses.clear()
            weeklyClasses.clear()

            val todayDay = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault()).format(java.util.Date())

            for (classSnap in snapshot.children) {
                val className = classSnap.child("className").getValue(String::class.java) ?: continue
                val subjectsSnap = classSnap.child("subjects")
                for (subjectSnap in subjectsSnap.children) {
                    val teacherIdFromDb = subjectSnap.child("teacherId").getValue(String::class.java) ?: continue
                    if (teacherIdFromDb != teacherId) continue

                    val subjectName = subjectSnap.child("subjectName").getValue(String::class.java) ?: ""
                    val teacherName = subjectSnap.child("teacherName").getValue(String::class.java) ?: ""
                    val roomNumber = subjectSnap.child("roomNumber").getValue(String::class.java) ?: ""

                    val scheduleSnap = subjectSnap.child("schedule")
                    for (sched in scheduleSnap.children) {
                        val day = sched.child("day").getValue(String::class.java) ?: ""
                        val time = sched.child("time").getValue(String::class.java) ?: ""
                        val item = SubjectSchedule(className, subjectName, teacherName, roomNumber, day, time)

                        weeklyClasses.add(item)
                        if (day.equals(todayDay, true)) todayClasses.add(item)
                    }
                }
            }

            updateUI()
        }
    }

    private fun updateUI() {
        if (todayClasses.isEmpty()) {
            tvNoToday.visibility = View.VISIBLE
            todayRecycler.visibility = View.GONE
        } else {
            tvNoToday.visibility = View.GONE
            todayRecycler.visibility = View.VISIBLE
            todayAdapter.notifyDataSetChanged()  // TodayClassAdapter handles todayClasses
        }

        if (weeklyClasses.isEmpty()) {
            tvNoWeekly.visibility = View.VISIBLE
            weeklyRecycler.visibility = View.GONE
        } else {
            tvNoWeekly.visibility = View.GONE
            weeklyRecycler.visibility = View.VISIBLE
            weeklyRecycler.adapter = ClassesAdapter(weeklyClasses)
        }
    }

    // Weekly RecyclerView Adapter
    class ClassesAdapter(private val classes: List<SubjectSchedule>) :
        RecyclerView.Adapter<ClassesAdapter.ClassViewHolder>() {

        class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvClassName: TextView = itemView.findViewById(R.id.tvClassName)
            val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
            val tvRoom: TextView = itemView.findViewById(R.id.tvRoom)
            val tvDayTime: TextView = itemView.findViewById(R.id.tvDayTime)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_class, parent, false)
            return ClassViewHolder(view)
        }

        override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
            val item = classes[position]
            holder.tvClassName.text = item.className
            holder.tvSubject.text = item.subjectName
            holder.tvRoom.text = item.roomNumber
            holder.tvDayTime.text = "${item.day}, ${item.time}"
        }

        override fun getItemCount(): Int = classes.size
    }
}
