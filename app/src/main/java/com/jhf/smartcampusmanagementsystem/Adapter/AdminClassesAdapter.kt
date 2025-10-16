package com.jhf.smartcampusmanagementsystem.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jhf.smartcampusmanagementsystem.Data.ClassItem
import com.jhf.smartcampusmanagementsystem.Data.SubjectItem
import com.jhf.smartcampusmanagementsystem.R

class AdminClassesAdapter(
    private val classes: List<ClassItem>,
    private val onEditClass: (ClassItem) -> Unit,
    private val onDeleteClass: (ClassItem) -> Unit,
    private val onAddSubject: (ClassItem) -> Unit,
    private val onEditSubject: (ClassItem, SubjectItem) -> Unit,
    private val onDeleteSubject: (ClassItem, SubjectItem) -> Unit
) : RecyclerView.Adapter<AdminClassesAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClassName: TextView = itemView.findViewById(R.id.tvClassName)
        val btnAddSubject: Button = itemView.findViewById(R.id.btnAddSubject)
        val btnEditClass: Button = itemView.findViewById(R.id.btnEditClass)
        val btnDeleteClass: Button = itemView.findViewById(R.id.btnDeleteClass)
        val rvSubjects: RecyclerView = itemView.findViewById(R.id.rvSubjects)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classItem = classes[position]
        holder.tvClassName.text = classItem.className

        holder.btnAddSubject.setOnClickListener { onAddSubject(classItem) }
        holder.btnEditClass.setOnClickListener { onEditClass(classItem) }
        holder.btnDeleteClass.setOnClickListener { onDeleteClass(classItem) }

        holder.rvSubjects.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvSubjects.isNestedScrollingEnabled = false
        holder.rvSubjects.adapter = AdminSubjectsAdapter(
            classItem.subjects,
            onEdit = { subject -> onEditSubject(classItem, subject) },
            onDelete = { subject -> onDeleteSubject(classItem, subject) }
        )
    }

    override fun getItemCount(): Int = classes.size
}
