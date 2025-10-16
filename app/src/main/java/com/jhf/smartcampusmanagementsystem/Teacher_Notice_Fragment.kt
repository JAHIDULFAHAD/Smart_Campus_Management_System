package com.jhf.smartcampusmanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.jhf.smartcampusmanagementsystem.Data.Notice
import java.text.SimpleDateFormat
import java.util.*

class Teacher_Notice_Fragment : Fragment() {

    private lateinit var rvNotices: RecyclerView
    private lateinit var tvNoNotice: TextView
    private val noticeList = mutableListOf<Notice>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_teacher__notice_, container, false)
        rvNotices = view.findViewById(R.id.rvAllNotices)

        // Add a separate TextView in XML for "No Notice available" if needed
        tvNoNotice = TextView(requireContext()).apply {
            text = "No Notice available"
            textSize = 16f
            visibility = View.GONE
        }
        (view as ViewGroup).addView(tvNoNotice)

        rvNotices.layoutManager = LinearLayoutManager(requireContext())
        loadTeacherNotices()

        return view
    }

    private fun loadTeacherNotices() {
        val dbRef = FirebaseDatabase.getInstance().getReference("notices/teacher")
        dbRef.get().addOnSuccessListener { snapshot ->
            noticeList.clear()

            for (snap in snapshot.children) {
                val title = snap.child("title").getValue(String::class.java) ?: ""
                val description = snap.child("description").getValue(String::class.java) ?: ""
                val timestamp = snap.child("timestamp").getValue(Long::class.java) ?: 0L

                noticeList.add(Notice(title, description, timestamp, "teacher"))
            }

            updateUI()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load notices", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        if (noticeList.isEmpty()) {
            tvNoNotice.visibility = View.VISIBLE
            rvNotices.visibility = View.GONE
        } else {
            tvNoNotice.visibility = View.GONE
            rvNotices.visibility = View.VISIBLE
            rvNotices.adapter = NoticeAdapter(noticeList)
        }
    }

    // Adapter
    class NoticeAdapter(private val notices: List<Notice>) :
        RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

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
            val notice = notices[position]
            holder.tvTitle.text = notice.title
            holder.tvContent.text = notice.description
            holder.tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(notice.timestamp))
        }

        override fun getItemCount(): Int = notices.size
    }
}
