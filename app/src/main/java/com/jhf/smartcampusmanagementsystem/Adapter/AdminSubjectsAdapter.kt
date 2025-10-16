package com.jhf.smartcampusmanagementsystem.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jhf.smartcampusmanagementsystem.Data.SubjectItem
import com.jhf.smartcampusmanagementsystem.R

class AdminSubjectsAdapter(
    private val subjects: List<SubjectItem>,
    private val onEdit: (SubjectItem) -> Unit,
    private val onDelete: (SubjectItem) -> Unit
) : RecyclerView.Adapter<AdminSubjectsAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSubjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
        val tvTeacher: TextView = itemView.findViewById(R.id.tvTeacher)
        val tvRoom: TextView = itemView.findViewById(R.id.tvRoom)
        val tvSchedule: TextView = itemView.findViewById(R.id.tvSchedule)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditSubject)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteSubject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_add_sub_class, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.tvSubjectName.text = subject.subjectName
        holder.tvTeacher.text = "Teacher: ${subject.teacherName}"
        holder.tvRoom.text = "Room: ${subject.roomNumber}"
        holder.tvSchedule.text = subject.schedule.joinToString(", ") { "${it.day} ${it.time}" }

        holder.btnEdit.setOnClickListener { onEdit(subject) }
        holder.btnDelete.setOnClickListener { onDelete(subject) }
    }

    override fun getItemCount(): Int = subjects.size
}
