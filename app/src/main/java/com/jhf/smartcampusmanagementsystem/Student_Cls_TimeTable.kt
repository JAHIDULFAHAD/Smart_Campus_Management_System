package com.jhf.smartcampusmanagementsystem

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Student_Cls_TimeTable : Fragment() {

    lateinit var CourseList: List<RecyclerViewModal>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student__cls__time_table, container, false)

        // Sample courses
        CourseList = listOf(
            RecyclerViewModal("Math", Color.parseColor("#FF5722"), "9:00 AM - 10:00 AM", "Room 308","Md. Nahid"),
            RecyclerViewModal("Physics", Color.parseColor("#4CAF50"), "10:00 AM - 11:00 AM", "Room 410","Md. Jahid"),
            RecyclerViewModal("Chemistry", Color.parseColor("#2196F3"), "12:00 PM - 1:00 PM", "Room 309","Md. Zahid"),
            RecyclerViewModal("Biology", Color.parseColor("#9C27B0"), "1:00 PM - 2:00 PM", "Room 308","Md. Zafor")
        )

        // Horizontal RecyclerView setup
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = HorizontalAdapter(CourseList, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        val weeklySchedule: Map<String, List<RecyclerViewModal>> = mapOf(
            "Monday" to listOf(
                RecyclerViewModal("Math", Color.parseColor("#FF5722"), "9:00 - 10:00 Am", "Room 308","Md. Nahid"),
                RecyclerViewModal("Physics", Color.parseColor("#4CAF50"), "10:00AM - 11:00 Am", "Room 410","Md. Jahid")
            ),
            "Tuesday" to listOf(
                RecyclerViewModal("Chemistry", Color.parseColor("#2196F3"), "9:00 - 10:00", "Room 309","Md. Zahid"),
                RecyclerViewModal("Biology", Color.parseColor("#9C27B0"), "10:00 - 11:00", "Room 308","Md. Zafor")
            ),
            "Wednesday" to listOf(
                RecyclerViewModal("Math", Color.parseColor("#FF5722"), "9:00 - 10:00", "Room 308","Md. Nahid"),
                RecyclerViewModal("Biology", Color.parseColor("#9C27B0"), "10:00 - 11:00", "Room 308","Md. Zafor")
            )
        )

        val weeklyContainer = view.findViewById<LinearLayout>(R.id.weeklyContainer)

        // Loop through each day
        for ((day, courses) in weeklySchedule) {
            val dayCard = inflater.inflate(R.layout.day_schedule, weeklyContainer, false)
            val tvDay = dayCard.findViewById<TextView>(R.id.tvDay)
            val scheduleContainer = dayCard.findViewById<LinearLayout>(R.id.scheduleContainer)

            tvDay.text = day

            // Loop through courses to create slots
            for (course in courses) {
                val slotView = inflater.inflate(R.layout.item_clstime_slot, scheduleContainer, false)

                slotView.findViewById<TextView>(R.id.tvTime).text = course.ClassTime
                slotView.findViewById<TextView>(R.id.tvSubject).text = course.SubName
                slotView.findViewById<TextView>(R.id.tvRoom).text = course.ClassRoom
                slotView.findViewById<TextView>(R.id.tvTeacher).text = course.TeacherName

                // Set background color to course color
                scheduleContainer.addView(slotView)
            }

            weeklyContainer.addView(dayCard)
        }

        return view
    }
}
