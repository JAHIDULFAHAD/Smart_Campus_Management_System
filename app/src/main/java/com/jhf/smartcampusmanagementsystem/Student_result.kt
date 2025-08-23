package com.jhf.smartcampusmanagementsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView

class Student_result : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var rvSections: RecyclerView
    private lateinit var tvSubjectName: TextView
    private lateinit var tvTeacherName: TextView

    private lateinit var subjects: List<SubjectModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_result, container, false)

        spinner = view.findViewById(R.id.spinnerSubjects)
        rvSections = view.findViewById(R.id.recyclerViewSec)
        tvSubjectName = view.findViewById(R.id.tvSubjectName)
        tvTeacherName = view.findViewById(R.id.tvTeacherName)

        // ================= Example Data =================
        subjects = listOf(
            SubjectModel(
                name = "Mathematics",
                teacher = "Mr. Rahman",
                sections = listOf(
                    ExamNameModel("Midterm", listOf(
                        ExamComponentModel("Attendance", 10, 5, 10, 8f),
                        ExamComponentModel("Quiz", 20, 10, 20, 15f, subComponents = listOf(
                            ExamComponentModel("Quiz 1", 10, 5, 10, 8f),
                            ExamComponentModel("Quiz 2", 10, 5, 10, 7f)
                        )),
                        ExamComponentModel("Performance", 10, 5, 10, 9f),
                        ExamComponentModel("Mid Exam Result", 60, 30, 60, 52f)
                    )),
                    ExamNameModel("Final", listOf(
                        ExamComponentModel("Attendance", 10, 5, 10, 9f),
                        ExamComponentModel("Quiz", 20, 10, 20, 18f),
                        ExamComponentModel("Performance", 10, 5, 10, 8f),
                        ExamComponentModel("Final Exam Result", 60, 30, 60, 55f)
                    ))
                )
            ),
            SubjectModel(
                name = "Physics",
                teacher = "Dr. Alam",
                sections = listOf(
                    ExamNameModel("Midterm", listOf(
                        ExamComponentModel("Attendance", 10, 5, 10, 10f),
                        ExamComponentModel("Quiz", 20, 10, 20, 18f),
                        ExamComponentModel("Performance", 10, 5, 10, 9f),
                        ExamComponentModel("Mid Exam Result", 60, 30, 60, 48f)
                    )),
                    ExamNameModel("Final", listOf(
                        ExamComponentModel("Attendance", 10, 5, 10, 8f),
                        ExamComponentModel("Quiz", 20, 10, 20, 17f),
                        ExamComponentModel("Performance", 10, 5, 10, 8f),
                        ExamComponentModel("Final Exam Result", 60, 30, 60, 53f)
                    ))
                )
            )
        )
        // =================================================

        // Spinner Adapter
        val names = subjects.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val subject = subjects[position]
                tvSubjectName.text = subject.name
                tvTeacherName.text = subject.teacher

                rvSections.layoutManager = LinearLayoutManager(requireContext())
                rvSections.adapter = ExamNameAdapter(subject.sections)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }
}
