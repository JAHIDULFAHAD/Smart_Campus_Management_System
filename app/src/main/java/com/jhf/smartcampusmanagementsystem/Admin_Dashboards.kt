package com.jhf.smartcampusmanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class Admin_Dashboards : Fragment() {

    private lateinit var db: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin__dashboards, container, false)

        // Quick Stats TextViews
        val tvTotalStudents = view.findViewById<TextView>(R.id.tvTotalStudents)
        val tvTotalTeachers = view.findViewById<TextView>(R.id.tvTotalTeachers)
        val tvTotalClasses = view.findViewById<TextView>(R.id.tvTotalClasses)
        val tvNotices = view.findViewById<TextView>(R.id.tvNotices)

        db = FirebaseDatabase.getInstance().reference

        // Fetch Students Count
        db.child("users").orderByChild("role").equalTo("student")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount
                    tvTotalStudents.text = "Students: $count"
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        // Fetch Teachers Count
        db.child("users").orderByChild("role").equalTo("teacher")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount
                    tvTotalTeachers.text = "Teachers: $count"
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        // Fetch Classes Count
        db.child("classes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                tvTotalClasses.text = "Classes: $count"
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Total Notices
        db.child("notices").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalNotices = 0L
                for (child in snapshot.children) {
                    totalNotices += child.childrenCount  // Count notices under student and teacher
                }
                tvNotices.text = "Notices: $totalNotices"
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        // Button Navigation
        view.findViewById<Button>(R.id.btnAddStudent).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, Admin_Add_Student_Fragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btnAddTeacher).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, Admin_Add_Teacher_Fragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btnManageClasses).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, Admin_Manage_Classes_Fragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btnPostNotice).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, Admin_Notice_Fragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
