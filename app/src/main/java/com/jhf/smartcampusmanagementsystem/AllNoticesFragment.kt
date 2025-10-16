package com.jhf.smartcampusmanagementsystem

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.jhf.smartcampusmanagementsystem.Adapter.NoticeAdapter
import com.jhf.smartcampusmanagementsystem.Data.Notice
import java.text.SimpleDateFormat
import java.util.*

class AllNoticesFragment : Fragment() {

    private lateinit var rvAllNotices: RecyclerView
    private lateinit var noticeAdapter: NoticeAdapter
    private val noticeList = mutableListOf<Notice>()

    private val dbNotices = FirebaseDatabase.getInstance().getReference("notices").child("student")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_notices, container, false)

        rvAllNotices = view.findViewById(R.id.rvAllNotices)
        noticeAdapter = NoticeAdapter(noticeList)
        rvAllNotices.layoutManager = LinearLayoutManager(requireContext())
        rvAllNotices.adapter = noticeAdapter

        loadAllNotices()
        return view
    }

    private fun loadAllNotices() {
        dbNotices.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noticeList.clear()

                if (!snapshot.exists()) {
                    Toast.makeText(requireContext(), "No notices found!", Toast.LENGTH_SHORT).show()
                    noticeAdapter.notifyDataSetChanged()
                    return
                }

                for (noticeSnap in snapshot.children) {
                    val notice = noticeSnap.getValue(Notice::class.java)
                    notice?.let { noticeList.add(it) }
                }

                // Sort by timestamp descending
                noticeList.sortByDescending { it.timestamp }

                noticeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load notices", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
