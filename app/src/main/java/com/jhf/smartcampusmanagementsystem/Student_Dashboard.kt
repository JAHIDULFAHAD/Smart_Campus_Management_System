package com.jhf.smartcampusmanagementsystem

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.GridView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

class Student_Dashboard : Fragment() {

    lateinit var CourseList: List<RecyclerViewModal>
    lateinit var AssingmentDetails: List<GridViewModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student__dashboard, container, false)

        // Course list initialize
        CourseList = listOf(
            RecyclerViewModal("Math", Color.parseColor("#FF5722"), "9 to 10 Am", "Room 308","Md. Nahid"),
            RecyclerViewModal("Physics", Color.parseColor("#4CAF50"), "10 to 11 Am", "Room 410","Md. Jahid"),
            RecyclerViewModal("Chemistry", Color.parseColor("#2196F3"), "12 to 1 pm", "Room 309","Md. Zahid"),
            RecyclerViewModal("Biology", Color.parseColor("#9C27B0"), "1 to 2 pm", "Room 308","Md. Zafor")
        )

        // RecyclerView setup
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = HorizontalAdapter(CourseList, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        // Assingment Details initialize
        AssingmentDetails= listOf(
            GridViewModel("Assignment 1","Math","14 Nov,8 Am","9/10","Hand In"),
            GridViewModel("Assignment 3","Chemistry","14 Nov,8 Am","9/10","Hand In"),
        )
        // GridView setup
        val gridView = view.findViewById<GridView>(R.id.gridView)
        val gridAdapter = GridAdapter_Student_ClsToday(AssingmentDetails, requireContext())
        gridView.adapter = gridAdapter

        return view
    }
}
