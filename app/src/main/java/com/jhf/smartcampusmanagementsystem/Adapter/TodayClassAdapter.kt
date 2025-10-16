package com.jhf.smartcampusmanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jhf.smartcampusmanagementsystem.Data.SubjectSchedule

class TodayClassAdapter(private val classList: List<SubjectSchedule>) :
    RecyclerView.Adapter<TodayClassAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById(R.id.SubName)
        val teacherName: TextView = itemView.findViewById(R.id.TName)
        val time: TextView = itemView.findViewById(R.id.ClassTime)
        val roomNumber: TextView = itemView.findViewById(R.id.ClassRoom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = classList[position]
        holder.subjectName.text = current.subjectName
        holder.teacherName.text = current.teacherName
        holder.time.text = current.time
        holder.roomNumber.text = "Room: ${current.roomNumber}"
    }

    override fun getItemCount(): Int = classList.size
}
