package com.jhf.smartcampusmanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExamComponentAdapter(private val components: List<ExamComponentModel>) :
    RecyclerView.Adapter<ExamComponentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.ComponentName)
        val tvMark: TextView = itemView.findViewById(R.id.ComponentMark)
        val rvSub: RecyclerView = itemView.findViewById(R.id.rvSubComponents)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_component, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val component = components[position]

        holder.tvName.text = "${component.name} (Total:${component.total}; Pass:${component.pass}; Contributes:${component.contributes}%)"
        holder.tvMark.text = component.mark?.toString() ?: "-"

        // Setup sub-components if available
        if (!component.subComponents.isNullOrEmpty()) {
            holder.rvSub.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.rvSub.adapter = ExamSubComponentAdapter(component.subComponents)
            holder.rvSub.visibility = View.VISIBLE
        } else {
            holder.rvSub.visibility = View.GONE
        }
    }

    override fun getItemCount() = components.size
}
