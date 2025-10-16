package com.jhf.smartcampusmanagementsystem.adapters

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.jhf.smartcampusmanagementsystem.Data.ComponentItem
import com.jhf.smartcampusmanagementsystem.R
import com.jhf.smartcampusmanagementsystem.StudentMarkUpload

class StudentComponentAdapter(
    private val studentComponents: MutableList<ComponentItem>
) : RecyclerView.Adapter<StudentComponentAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val etName: EditText = view.findViewById(R.id.etComponentName)
        val etMarks: EditText = view.findViewById(R.id.etMarks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_marks, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = studentComponents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val component = studentComponents[position]

        holder.etName.setText(component.name)
        holder.etMarks.setText(component.obtainedMarks.toString())

        // Remove old watchers
        holder.etName.tag?.let { holder.etName.removeTextChangedListener(it as TextWatcher) }
        holder.etMarks.tag?.let { holder.etMarks.removeTextChangedListener(it as TextWatcher) }

        val nameWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                component.name = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        holder.etName.addTextChangedListener(nameWatcher)
        holder.etName.tag = nameWatcher

        val marksWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                component.obtainedMarks = s.toString().toFloatOrNull() ?: 0f
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        holder.etMarks.addTextChangedListener(marksWatcher)
        holder.etMarks.tag = marksWatcher
    }

    fun addComponent(name: String, maxMarks: Float = 100f) {
        studentComponents.add(ComponentItem(name = name, maxMarks = maxMarks))
        notifyItemInserted(studentComponents.size - 1)
    }
}


