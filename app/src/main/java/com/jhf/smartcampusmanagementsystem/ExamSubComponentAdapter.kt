package com.jhf.smartcampusmanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExamSubComponentAdapter(private val subComponents: List<ExamComponentModel>) :
    RecyclerView.Adapter<ExamSubComponentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSubName: TextView = itemView.findViewById(R.id.tvSubName)
        val tvSubMark: TextView = itemView.findViewById(R.id.tvSubMark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sub_component, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sub = subComponents[position]
        holder.tvSubName.text = sub.name
        holder.tvSubMark.text = sub.mark?.toString() ?: "-"
    }

    override fun getItemCount() = subComponents.size
}
