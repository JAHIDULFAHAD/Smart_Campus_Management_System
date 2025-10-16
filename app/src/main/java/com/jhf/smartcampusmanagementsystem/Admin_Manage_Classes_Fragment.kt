package com.jhf.smartcampusmanagementsystem

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*
import com.jhf.smartcampusmanagementsystem.Adapter.AdminClassesAdapter
import com.jhf.smartcampusmanagementsystem.Data.ClassItem
import com.jhf.smartcampusmanagementsystem.Data.ScheduleItem
import com.jhf.smartcampusmanagementsystem.Data.SubjectItem
import com.jhf.smartcampusmanagementsystem.Data.TeacherItem
import com.jhf.smartcampusmanagementsystem.R

class Admin_Manage_Classes_Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminClassesAdapter
    private val classesList = mutableListOf<ClassItem>()
    private val dbRef = FirebaseDatabase.getInstance().getReference("classes")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin__manage__classes_, container, false)

        recyclerView = view.findViewById(R.id.rvClasses)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AdminClassesAdapter(
            classesList,
            onEditClass = { editClass(it) },
            onDeleteClass = { deleteClass(it) },
            onAddSubject = { addOrEditSubject(it) },
            onEditSubject = { classItem, subject -> addOrEditSubject(classItem, subject) },
            onDeleteSubject = { classItem, subject ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Subject")
                    .setMessage("Are you sure you want to delete this subject?")
                    .setPositiveButton("Yes") { _, _ ->
                        val updatedSubjects = classItem.subjects.toMutableList()
                        updatedSubjects.remove(subject)
                        classItem.subjects = updatedSubjects
                        dbRef.child(classItem.id).setValue(classItem)
                        Toast.makeText(requireContext(), "Subject deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Deletion cancelled", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        )

        recyclerView.adapter = adapter

        view.findViewById<MaterialButton>(R.id.btnAddNewClass).setOnClickListener { addClass() }

        loadClassesFromFirebase()
        return view
    }

    private fun loadClassesFromFirebase() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                classesList.clear()
                for (snap in snapshot.children) {
                    val classItem = snap.getValue(ClassItem::class.java)
                    classItem?.let { classesList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ✅ ADD CLASS
    private fun addClass() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.admin_dialog_add_class_simple, null)
        val etClassName = dialogView.findViewById<EditText>(R.id.etClassName)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Class")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val className = etClassName.text.toString().trim()
                if (className.isEmpty()) {
                    etClassName.error = "Please enter class name"
                    return@setOnClickListener
                }

                val id = dbRef.push().key ?: System.currentTimeMillis().toString()
                val newClass = ClassItem(id = id, className = className)
                dbRef.child(id).setValue(newClass)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // ✅ EDIT CLASS
    private fun editClass(classItem: ClassItem) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.admin_dialog_add_class_simple, null)
        val etClassName = dialogView.findViewById<EditText>(R.id.etClassName)
        etClassName.setText(classItem.className)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Class")
            .setView(dialogView)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            updateButton.setOnClickListener {
                val className = etClassName.text.toString().trim()
                if (className.isEmpty()) {
                    etClassName.error = "Please enter class name"
                    return@setOnClickListener
                }

                classItem.className = className
                dbRef.child(classItem.id).setValue(classItem)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // ✅ DELETE CLASS with confirmation
    private fun deleteClass(classItem: ClassItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Class")
            .setMessage("Are you sure you want to delete this class?")
            .setPositiveButton("Yes") { _, _ ->
                dbRef.child(classItem.id).removeValue()
                Toast.makeText(requireContext(), "Class deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(requireContext(), "Deletion cancelled", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    // ✅ ADD / EDIT SUBJECT (Teacher saved by ID)
    private fun addOrEditSubject(classItem: ClassItem, subjectToEdit: SubjectItem? = null) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.admin_dialog_add_class, null)

        val etSubjectName = dialogView.findViewById<EditText>(R.id.etSubjectName)
        val spinnerTeacher = dialogView.findViewById<Spinner>(R.id.spinnerTeacher)
        val etRoom = dialogView.findViewById<EditText>(R.id.etRoom)
        val btnAddSchedule = dialogView.findViewById<Button>(R.id.btnAddSchedule)
        val llSchedules = dialogView.findViewById<LinearLayout>(R.id.llSchedules)

        val tempSchedules = mutableListOf<ScheduleItem>()
        subjectToEdit?.let { tempSchedules.addAll(it.schedule) }

        // Load teachers from Firebase
        val teacherNames = mutableListOf<String>()
        val teacherList = mutableListOf<TeacherItem>()
        FirebaseDatabase.getInstance().getReference("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    teacherNames.clear()
                    teacherList.clear()
                    for (snap in snapshot.children) {
                        val user = snap.getValue(TeacherItem::class.java)
                        if (user != null && user.role == "teacher") {
                            teacherList.add(user)
                            teacherNames.add(user.name)
                        }
                    }
                    if (teacherNames.isNotEmpty()) {
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, teacherNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerTeacher.adapter = adapter

                        // If editing, set spinner selection
                        subjectToEdit?.let { sub ->
                            val index = teacherList.indexOfFirst { it.user_id == sub.teacherId }
                            if (index >= 0) spinnerTeacher.setSelection(index)
                        }
                    } else {
                        Toast.makeText(requireContext(), "No teachers found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Firebase error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

        fun refreshSchedules() {
            llSchedules.removeAllViews()
            tempSchedules.forEachIndexed { index, schedule ->
                val tv = TextView(requireContext())
                tv.text = "${schedule.day} ${schedule.time} ✕"
                tv.setPadding(8, 8, 8, 8)
                tv.setOnClickListener {
                    tempSchedules.removeAt(index)
                    refreshSchedules()
                }
                llSchedules.addView(tv)
            }
        }
        refreshSchedules()

        btnAddSchedule.setOnClickListener {
            val scheduleView = LayoutInflater.from(requireContext())
                .inflate(R.layout.admin_dialog_add_schedule, null)
            val etDay = scheduleView.findViewById<EditText>(R.id.etDay)
            val etTime = scheduleView.findViewById<EditText>(R.id.etTime)

            AlertDialog.Builder(requireContext())
                .setTitle("Add Schedule")
                .setView(scheduleView)
                .setPositiveButton("Add") { _, _ ->
                    val day = etDay.text.toString().trim()
                    val time = etTime.text.toString().trim()
                    if (day.isNotEmpty() && time.isNotEmpty()) {
                        tempSchedules.add(ScheduleItem(day, time))
                        refreshSchedules()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Fill existing values if editing
        if (subjectToEdit != null) {
            etSubjectName.setText(subjectToEdit.subjectName)
            etRoom.setText(subjectToEdit.roomNumber)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (subjectToEdit == null) "Add Subject" else "Edit Subject")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val subjectName = etSubjectName.text.toString().trim()
                val room = etRoom.text.toString().trim()
                val selectedTeacherIndex = spinnerTeacher.selectedItemPosition

                if (subjectName.isEmpty()) {
                    etSubjectName.error = "Enter subject name"
                    return@setOnClickListener
                }
                if (room.isEmpty()) {
                    etRoom.error = "Enter room number"
                    return@setOnClickListener
                }
                if (tempSchedules.isEmpty()) {
                    Toast.makeText(requireContext(), "Please add at least one schedule", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (selectedTeacherIndex < 0 || teacherList.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select a teacher", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val selectedTeacher = teacherList[selectedTeacherIndex]

                val subject = subjectToEdit ?: SubjectItem()
                if (subject.id.isEmpty()) subject.id = dbRef.push().key ?: System.currentTimeMillis().toString()

                subject.subjectName = subjectName
                subject.roomNumber = room
                subject.teacherName = selectedTeacher.name
                subject.teacherId = selectedTeacher.user_id
                subject.schedule = tempSchedules.toMutableList()

                if (subjectToEdit == null) classItem.subjects.add(subject)
                dbRef.child(classItem.id).setValue(classItem)

                dialog.dismiss()
            }
        }

        dialog.show()
    }
}
