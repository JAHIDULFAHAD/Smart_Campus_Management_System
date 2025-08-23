package com.jhf.smartcampusmanagementsystem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class HorizontalAdapter(
    private val courseList: List<RecyclerViewModal>,
    private val context: Context
) : RecyclerView.Adapter<HorizontalAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseColor: View = itemView.findViewById(R.id.Color)
        val courseTV: TextView = itemView.findViewById(R.id.SubName)
        val classTime: TextView = itemView.findViewById(R.id.ClassTime)
        val classRoom: TextView = itemView.findViewById(R.id.ClassRoom)
        val Teacher: TextView = itemView.findViewById(R.id.TName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val course = courseList[position]
        holder.courseColor.setBackgroundColor(course.SubColor)
        holder.courseTV.text = course.SubName
        holder.classTime.text = course.ClassTime
        holder.classRoom.text = course.ClassRoom
        holder.Teacher.text = course.TeacherName

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "${course.SubName} selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = courseList.size
}
