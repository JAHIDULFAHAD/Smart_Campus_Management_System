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
        val view = inflater.inflate(R.layout.fragment_student__cls__time_table, container, false)

        // Horizontal RecyclerView Example
        CourseList = listOf(
            RecyclerViewModal("Math", Color.parseColor("#2196F3"), "9 to 10 AM", "Room 308", "Md. Nahid"),
            RecyclerViewModal("Physics", Color.parseColor("#4CAF50"), "10 to 11 AM", "Room 410", "Md. Jahid"),
            RecyclerViewModal("Chemistry", Color.parseColor("#9C27B0"), "12 to 1 PM", "Room 309", "Md. Zahid"),
            RecyclerViewModal("Biology", Color.parseColor("#009688"), "1 to 2 PM", "Room 308", "Md. Zafor"),
            RecyclerViewModal("English", Color.parseColor("#795548"), "2 to 3 PM", "Room 204", "Md. Karim"),
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = HorizontalAdapter(CourseList, requireContext())
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter


        val weeklySchedule: Map<String, List<RecyclerViewModal>> = mapOf(
            "Sunday" to listOf(
                RecyclerViewModal("Math", Color.parseColor("#2196F3"), "9:00 - 9:50 AM", "Room 308","Md. Nahid"),
                RecyclerViewModal("Physics", Color.parseColor("#4CAF50"), "10:00 - 10:50 AM", "Room 410","Md. Jahid"),
                RecyclerViewModal("Chemistry", Color.parseColor("#9C27B0"), "11:00 - 11:50 AM", "Room 309","Md. Zahid")
            ),
            "Monday" to listOf(
                RecyclerViewModal("Math", Color.parseColor("#2196F3"), "9:00 - 9:50 AM", "Room 308","Md. Nahid"),
                RecyclerViewModal("Physics", Color.parseColor("#4CAF50"), "10:00 - 10:50 AM", "Room 410","Md. Jahid"),
                RecyclerViewModal("Chemistry", Color.parseColor("#9C27B0"), "11:00 - 11:50 AM", "Room 309","Md. Zahid"),
                RecyclerViewModal("Biology", Color.parseColor("#009688"), "12:00 - 12:50 PM", "Room 308","Md. Zafor"),
                RecyclerViewModal("English", Color.parseColor("#795548"), "1:00 - 1:50 PM", "Room 205","Mrs. Akter")
            ),
            "Tuesday" to listOf(
                RecyclerViewModal("Math", Color.parseColor("#2196F3"), "9:00 - 9:50 AM", "Room 308","Md. Nahid"),
                RecyclerViewModal("Physics", Color.parseColor("#4CAF50"), "10:00 - 10:50 AM", "Room 410","Md. Jahid"),
                RecyclerViewModal("Chemistry", Color.parseColor("#9C27B0"), "11:00 - 11:50 AM", "Room 309","Md. Zahid"),
                RecyclerViewModal("Biology", Color.parseColor("#009688"), "12:00 - 12:50 PM", "Room 308","Md. Zafor"),
                RecyclerViewModal("English", Color.parseColor("#795548"), "1:00 - 1:50 PM", "Room 205","Mrs. Akter")
            ),
            "Wednesday" to listOf(
                RecyclerViewModal("Math", Color.parseColor("#2196F3"), "9:00 - 9:50 AM", "Room 308","Md. Nahid"),
                RecyclerViewModal("Physics", Color.parseColor("#4CAF50"), "10:00 - 10:50 AM", "Room 410","Md. Jahid"),
                RecyclerViewModal("Chemistry", Color.parseColor("#9C27B0"), "11:00 - 11:50 AM", "Room 309","Md. Zahid"),
                RecyclerViewModal("Biology", Color.parseColor("#009688"), "12:00 - 12:50 PM", "Room 308","Md. Zafor")
            ),
            "Thursday" to listOf(
                RecyclerViewModal("Math", Color.parseColor("#2196F3"), "9:00 - 9:50 AM", "Room 308","Md. Nahid"),
                RecyclerViewModal("Physics", Color.parseColor("#4CAF50"), "10:00 - 10:50 AM", "Room 410","Md. Jahid"),
                RecyclerViewModal("Chemistry", Color.parseColor("#9C27B0"), "11:00 - 11:50 AM", "Room 309","Md. Zahid"),
                RecyclerViewModal("Biology", Color.parseColor("#009688"), "12:00 - 12:50 PM", "Room 308","Md. Zafor")
            ),
            "Friday" to listOf(
                RecyclerViewModal("No Class", Color.parseColor("#B0BEC5"), "", "", "")
            ),
            "Saturday" to listOf(
                RecyclerViewModal("No Class", Color.parseColor("#B0BEC5"), "", "", "")
            )
        )

        val weeklyContainer = view.findViewById<LinearLayout>(R.id.weeklyContainer)

        // Loop through each day
        for ((day, courses) in weeklySchedule) {
            val dayCard = inflater.inflate(R.layout.day_schedule, weeklyContainer, false)
            val tvDay = dayCard.findViewById<TextView>(R.id.tvDay)
            val scheduleContainer = dayCard.findViewById<LinearLayout>(R.id.scheduleContainer)
            tvDay.text = day

            for (course in courses) {
                val slotView = inflater.inflate(R.layout.item_clstime_slot, scheduleContainer, false)

                val tvTime = slotView.findViewById<TextView>(R.id.tvTime)
                val tvSubject = slotView.findViewById<TextView>(R.id.tvSubject)
                val tvRoom = slotView.findViewById<TextView>(R.id.tvRoom)
                val tvTeacher = slotView.findViewById<TextView>(R.id.tvTeacher)

                tvTime.text = course.ClassTime
                tvSubject.text = course.SubName
                tvRoom.text = course.ClassRoom
                tvTeacher.text = course.TeacherName

                tvSubject.setTextColor(course.SubColor)


                scheduleContainer.addView(slotView)
            }

            weeklyContainer.addView(dayCard)
        }

        return view
    }
}
