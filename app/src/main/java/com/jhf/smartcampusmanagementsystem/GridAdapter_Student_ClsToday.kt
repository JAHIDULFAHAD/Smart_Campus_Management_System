package com.jhf.smartcampusmanagementsystem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class GridAdapter_Student_ClsToday(
    private val itemList: List<GridViewModel>,
    private val context: Context
) : RecyclerView.Adapter<GridAdapter_Student_ClsToday.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val subject = itemView.findViewById<TextView>(R.id.subject)
        val datetime = itemView.findViewById<TextView>(R.id.datetime)
        val result = itemView.findViewById<TextView>(R.id.result)
        val button = itemView.findViewById<Button>(R.id.buttonAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.gridview_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.subject.text = item.subject
        holder.datetime.text = item.datetime
        holder.result.text = item.result
        holder.button.text = item.buttonText

        holder.button.setOnClickListener {
            Toast.makeText(context, "${item.title} clicked", Toast.LENGTH_SHORT).show()
        }
    }
}

