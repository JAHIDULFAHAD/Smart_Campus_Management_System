package com.jhf.smartcampusmanagementsystem

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Student_Cls_TimeTable : Fragment() {

    private lateinit var weeklyContainer: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoTodayClass: TextView
    private lateinit var todayAdapter: HorizontalAdapter
    private val todayClasses = mutableListOf<RecyclerViewModal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student__cls__time_table, container, false)

        weeklyContainer = view.findViewById(R.id.weeklyContainer)
        recyclerView = view.findViewById(R.id.recyclerView)
        tvNoTodayClass = view.findViewById(R.id.noClassesTextView)

        todayAdapter = HorizontalAdapter(todayClasses, requireContext())
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = todayAdapter

        val sharedPref = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val savedClassName = sharedPref.getString("class", null)?.trim()
        Log.d("TIME_TABLE_DEBUG", "Saved class name: $savedClassName")

        if (!savedClassName.isNullOrEmpty()) {
            loadTimeTableByClassName(savedClassName, inflater)
        } else {
            Toast.makeText(
                requireContext(),
                "Class info not found! Please logout and login again",
                Toast.LENGTH_LONG
            ).show()
        }

        return view
    }

    private fun loadTimeTableByClassName(className: String, inflater: LayoutInflater) {
        val database = FirebaseDatabase.getInstance().getReference("classes")

        // Case-insensitive query: fetch all classes, then match ignoring case
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
                    Toast.makeText(requireContext(), "Class info not found in database", Toast.LENGTH_LONG).show()
                    return
                }

                populateTimeTable(classSnapshot, inflater)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Error: ${error.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateTimeTable(snapshot: DataSnapshot, inflater: LayoutInflater) {
        weeklyContainer.removeAllViews()
        todayClasses.clear()

        val weeklyMap = mutableMapOf<String, MutableList<RecyclerViewModal>>()
        val allDays = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
        allDays.forEach { weeklyMap[it] = mutableListOf() }

        val calendar = java.util.Calendar.getInstance()
        val dayOfWeek = when(calendar.get(java.util.Calendar.DAY_OF_WEEK)){
            java.util.Calendar.SUNDAY -> "Sunday"
            java.util.Calendar.MONDAY -> "Monday"
            java.util.Calendar.TUESDAY -> "Tuesday"
            java.util.Calendar.WEDNESDAY -> "Wednesday"
            java.util.Calendar.THURSDAY -> "Thursday"
            java.util.Calendar.FRIDAY -> "Friday"
            java.util.Calendar.SATURDAY -> "Saturday"
            else -> "Sunday"
        }

        val subjectsSnap = snapshot.child("subjects")
        for(subjectSnap in subjectsSnap.children){
            val subjectName = subjectSnap.child("subjectName").getValue(String::class.java) ?: ""
            val teacherName = subjectSnap.child("teacherName").getValue(String::class.java) ?: ""
            val roomNumber = subjectSnap.child("roomNumber").getValue(String::class.java) ?: ""

            val scheduleSnap = subjectSnap.child("schedule")
            for(scheduleItem in scheduleSnap.children){
                val day = scheduleItem.child("day").getValue(String::class.java) ?: continue
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

                val colorList = listOf("#2196F3","#4CAF50","#9C27B0","#009688","#795548")
                val randomColor = Color.parseColor(colorList.random())

                val modal = RecyclerViewModal(subjectName, randomColor, time, "Room $roomNumber", teacherName)
                weeklyMap[dayName]?.add(modal)

                if(dayName == dayOfWeek) todayClasses.add(modal)
            }
        }

        todayAdapter.notifyDataSetChanged()

        if(todayClasses.isEmpty()){
            tvNoTodayClass.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvNoTodayClass.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        // Populate weekly timetable
        weeklyContainer.removeAllViews()
        for((day, subjects) in weeklyMap){
            val dayCard = inflater.inflate(R.layout.day_schedule, weeklyContainer,false)
            val tvDay = dayCard.findViewById<TextView>(R.id.tvDay)
            val scheduleContainer = dayCard.findViewById<LinearLayout>(R.id.scheduleContainer)
            tvDay.text = day

            if(subjects.isEmpty()){
                val noClassView = TextView(requireContext())
                noClassView.text = "No Class"
                noClassView.setPadding(16,16,16,16)
                scheduleContainer.addView(noClassView)
            } else {
                for(subject in subjects){
                    val slotView = inflater.inflate(R.layout.item_clstime_slot, scheduleContainer,false)
                    val tvTime = slotView.findViewById<TextView>(R.id.tvTime)
                    val tvSubject = slotView.findViewById<TextView>(R.id.tvSubject)
                    val tvRoom = slotView.findViewById<TextView>(R.id.tvRoom)
                    val tvTeacher = slotView.findViewById<TextView>(R.id.tvTeacher)

                    tvTime.text = subject.ClassTime
                    tvSubject.text = subject.SubName
                    tvRoom.text = subject.ClassRoom
                    tvTeacher.text = subject.TeacherName
                    tvSubject.setTextColor(subject.SubColor)

                    scheduleContainer.addView(slotView)
                }
            }
            weeklyContainer.addView(dayCard)
        }
    }
}
