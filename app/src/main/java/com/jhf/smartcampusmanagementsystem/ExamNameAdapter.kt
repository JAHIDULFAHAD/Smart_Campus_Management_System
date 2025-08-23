package com.jhf.smartcampusmanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExamNameAdapter(private val sections: List<ExamNameModel>) :
    RecyclerView.Adapter<ExamNameAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvSectionName)
        val rvComponents: RecyclerView = itemView.findViewById(R.id.rvComponents)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_examname, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]
        holder.tvTitle.text = section.title

        holder.rvComponents.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvComponents.adapter = ExamComponentAdapter(section.components)
    }

    override fun getItemCount() = sections.size
}
