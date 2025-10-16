package com.jhf.smartcampusmanagementsystem.adapters

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jhf.smartcampusmanagementsystem.Data.ComponentItem
import com.jhf.smartcampusmanagementsystem.R
import com.jhf.smartcampusmanagementsystem.StudentMarkUpload

class TeacherStudentMarksAdapter(
    private val students: MutableList<StudentMarkUpload>
) : RecyclerView.Adapter<TeacherStudentMarksAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_marks, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = students.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val student = students[position]
        holder.tvName.text = student.name

        // Inner adapter for components
        val innerAdapter = StudentComponentAdapter(student.components)
        holder.rvComponents.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvComponents.adapter = innerAdapter

        // Add component per student
        holder.btnAddPerStudent.setOnClickListener {
            val ctx = holder.itemView.context
            val etName = EditText(ctx).apply { hint = "Component Name" }
            val etMaxMarks = EditText(ctx).apply {
                hint = "Max Marks"
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }

            val container = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20, 10, 20, 10)
                addView(etName)
                addView(etMaxMarks)
            }

            android.app.AlertDialog.Builder(ctx)
                .setTitle("Add Component for ${student.name}")
                .setView(container)
                .setPositiveButton("Add") { _, _ ->
                    val name = etName.text.toString().trim()
                    val maxMarks = etMaxMarks.text.toString().toFloatOrNull() ?: 100f
                    if (name.isEmpty()) return@setPositiveButton

                    // Add component to student data
                    student.components.add(ComponentItem(name = name, maxMarks = maxMarks))
                    innerAdapter.notifyItemInserted(student.components.size - 1)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvStudentName)
        val rvComponents: RecyclerView = view.findViewById(R.id.rvComponents)
        val btnAddPerStudent: Button = view.findViewById(R.id.btnAddComponentPerStudent)
    }
}
