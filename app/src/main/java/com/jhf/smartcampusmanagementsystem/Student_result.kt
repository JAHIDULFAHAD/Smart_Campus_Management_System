package com.jhf.smartcampusmanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.jhf.smartcampusmanagementsystem.Data.ResultItem


class Student_result : Fragment() {

    private lateinit var spinnerSubjects: Spinner
    private lateinit var tvSubjectName: TextView
    private lateinit var tvTeacherName: TextView
    private lateinit var tvTerm: TextView
    private lateinit var tvTotalMarks: TextView
    private lateinit var recyclerViewSec: RecyclerView

    private lateinit var adapter: ResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_result, container, false)

        spinnerSubjects = view.findViewById(R.id.spinnerSubjects)
        tvSubjectName = view.findViewById(R.id.tvSubjectName)
        tvTeacherName = view.findViewById(R.id.tvTeacherName)
        tvTerm = view.findViewById(R.id.tvTerm)
        tvTotalMarks = view.findViewById(R.id.tvTotalMarks)
        recyclerViewSec = view.findViewById(R.id.recyclerViewSec)

        recyclerViewSec.layoutManager = LinearLayoutManager(requireContext())

        loadSubjects()

        return view
    }

    // Grading function
    private fun calculateGrade(score: Double): String {
        return when {
            score >= 80 -> "A+"
            score >= 70 -> "A"
            score >= 60 -> "B"
            score >= 50 -> "C"
            score >= 40 -> "D"
            else -> "F"
        }
    }

    private fun loadSubjects() {
        val sharedPref = requireActivity().getSharedPreferences("user_pref", 0)
        val className = sharedPref.getString("class", "") ?: return

        val classRef = FirebaseDatabase.getInstance().getReference("classes")
        classRef.get().addOnSuccessListener { snapshot ->
            val subjectNames = mutableListOf<String>()
            val subjectsMap = mutableMapOf<String, Map<String, Any>>()

            for (cs in snapshot.children) {
                val dbClassName = cs.child("className").getValue(String::class.java)
                if (dbClassName != null && dbClassName.equals(className, ignoreCase = true)) {
                    val subjectsSnap = cs.child("subjects")
                    for (sub in subjectsSnap.children) {
                        val subName = sub.child("subjectName").getValue(String::class.java) ?: continue
                        subjectNames.add(subName)
                        subjectsMap[subName] = mapOf(
                            "teacherName" to (sub.child("teacherName").getValue(String::class.java) ?: ""),
                            "subjectId" to (sub.child("id").getValue(String::class.java) ?: "")
                        )
                    }
                }
            }

            if (subjectNames.isEmpty()) {
                Toast.makeText(requireContext(), "No subject found for your class", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectNames)
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSubjects.adapter = adapterSpinner

            spinnerSubjects.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedSub = subjectNames[position]
                    val info = subjectsMap[selectedSub] ?: return
                    tvSubjectName.text = selectedSub
                    tvTeacherName.text = info["teacherName"].toString()
                    loadResults(selectedSub)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun loadResults(subjectName: String) {
        val sharedPref = requireActivity().getSharedPreferences("user_pref", 0)
        val studentId = sharedPref.getString("studentId", "")?.trim() ?: return
        val className = sharedPref.getString("class", "")?.trim() ?: return

        val resultRef = FirebaseDatabase.getInstance().getReference("results")
        val nodeName = "${className}_$subjectName".trim()

        resultRef.child(nodeName).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                Toast.makeText(requireContext(), "No results found for this subject", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val resultItems = mutableListOf<ResultItem>()
            var finalMarks = 0.0
            val totalTerms = snapshot.children.count()

            for (termSnap in snapshot.children) {
                val term = termSnap.key?.trim() ?: continue
                val studentSnap = termSnap.child("students").child(studentId)
                if (!studentSnap.exists()) continue

                var termTotal = 0
                val tempComponents = mutableListOf<ResultItem.ComponentItem>()

                for (comp in studentSnap.children) {
                    val name = comp.key ?: continue
                    val marks = comp.getValue(Int::class.java) ?: 0
                    termTotal += marks
                    tempComponents.add(ResultItem.ComponentItem(name, marks))
                }

                val termMax = 100 // max marks per term
                val termGrade = calculateGrade((termTotal.toDouble() / termMax) * 100)
                resultItems.add(ResultItem.TermHeader(term, termTotal, termMax, termGrade))
                resultItems.addAll(tempComponents)

                // Add term weighted contribution to final marks (evenly weighted)
                finalMarks += (termTotal.toDouble() / termMax) * (100.0 / totalTerms)
            }

            val finalGrade = calculateGrade(finalMarks)
            tvTerm.text = "Final Marks"
            tvTotalMarks.text = String.format("%.2f (%s)", finalMarks, finalGrade)

            adapter = ResultAdapter(resultItems)
            recyclerViewSec.adapter = adapter

        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to load result: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    class ResultAdapter(private val list: List<ResultItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private const val TYPE_HEADER = 0
            private const val TYPE_COMPONENT = 1
        }

        class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvTermHeader: TextView = itemView.findViewById(R.id.tvTermHeader)
        }

        class ComponentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvComponentName: TextView = itemView.findViewById(R.id.tvComponentName)
            val tvMarks: TextView = itemView.findViewById(R.id.tvMarks)
        }

        override fun getItemViewType(position: Int): Int {
            return when (list[position]) {
                is ResultItem.TermHeader -> TYPE_HEADER
                is ResultItem.ComponentItem -> TYPE_COMPONENT
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == TYPE_HEADER) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_term_header, parent, false)
                HeaderViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_exam_component, parent, false)
                ComponentViewHolder(view)
            }
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (val item = list[position]) {
                is ResultItem.TermHeader -> {
                    val h = holder as HeaderViewHolder
                    h.tvTermHeader.text = "${item.term} → ${item.obtainedMarks} / ${item.maxMarks} (${item.grade})"
                }
                is ResultItem.ComponentItem -> {
                    val h = holder as ComponentViewHolder
                    h.tvComponentName.text = item.name
                    h.tvMarks.text = item.marks.toString()
                }
            }
        }
    }
}
