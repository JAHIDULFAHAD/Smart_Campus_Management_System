package com.jhf.smartcampusmanagementsystem.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jhf.smartcampusmanagementsystem.R
import com.jhf.smartcampusmanagementsystem.TeacherClassModel

class TeacherClassAdapter(
    private val classList: List<TeacherClassModel>
) : RecyclerView.Adapter<TeacherClassAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClassName: TextView = itemView.findViewById(R.id.ClassName)
        val tvSubjectName: TextView = itemView.findViewById(R.id.SubName)
        val tvRoom: TextView = itemView.findViewById(R.id.ClassRoom)
        val tvTime: TextView = itemView.findViewById(R.id.ClassTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_teacher_class, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = classList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = classList[position]
        holder.tvClassName.text = item.className
        holder.tvSubjectName.text = item.subjectName
        holder.tvRoom.text = "Room: ${item.room}"
        holder.tvTime.text = item.time
    }
}
