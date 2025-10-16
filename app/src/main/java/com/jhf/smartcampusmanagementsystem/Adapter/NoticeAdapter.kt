package com.jhf.smartcampusmanagementsystem.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jhf.smartcampusmanagementsystem.Data.Notice
import com.jhf.smartcampusmanagementsystem.R
import java.text.SimpleDateFormat
import java.util.*

class NoticeAdapter(private val noticeList: List<Notice>) :
    RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    companion object {
        private val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        private val colorList = listOf("#2563EB", "#4CAF50", "#F59E0B", "#EF4444", "#9C27B0")
    }

    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notice_card, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = noticeList[position]

        holder.tvTitle.text = notice.title
        holder.tvContent.text = notice.description
        holder.tvDate.text = sdf.format(Date(notice.timestamp))

        // Stable color per notice based on position
        val color = colorList[position % colorList.size]
        holder.tvDate.apply {
            setBackgroundColor(Color.parseColor(color))
            setTextColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int = noticeList.size
}
