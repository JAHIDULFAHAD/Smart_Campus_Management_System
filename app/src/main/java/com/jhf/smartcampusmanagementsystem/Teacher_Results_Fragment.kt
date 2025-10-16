package com.jhf.smartcampusmanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jhf.smartcampusmanagementsystem.Data.ComponentItem
import com.jhf.smartcampusmanagementsystem.Data.SubjectSchedule
import com.jhf.smartcampusmanagementsystem.adapters.TeacherStudentMarksAdapter
import com.jhf.smartcampusmanagementsystem.StudentMarkUpload

class Teacher_Results_Fragment : Fragment() {

    private lateinit var spinnerSchedules: Spinner
    private lateinit var spinnerTerm: Spinner
    private lateinit var btnAddComponentGlobal: Button
    private lateinit var btnLoadStudents: Button
    private lateinit var rvStudents: RecyclerView
    private lateinit var btnSaveMarks: Button

    private val schedulesList = mutableListOf<SubjectSchedule>()
    private val scheduleDisplay = mutableListOf<String>()
    private val students = mutableListOf<StudentMarkUpload>()
    private val classComponents = mutableListOf<String>()

    private lateinit var database: DatabaseReference
    private lateinit var adapter: TeacherStudentMarksAdapter

    private val teacherId = FirebaseAuth.getInstance().currentUser?.uid ?: "25-1760213505697-1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_teacher__results_, container, false)

        spinnerSchedules = v.findViewById(R.id.spinnerSchedules)
        spinnerTerm = v.findViewById(R.id.spinnerTerm)
        btnAddComponentGlobal = v.findViewById(R.id.btnAddComponentGlobal)
        btnLoadStudents = v.findViewById(R.id.btnLoadStudents)
        rvStudents = v.findViewById(R.id.rvStudentsForMarks)
        btnSaveMarks = v.findViewById(R.id.btnSaveMarks)

        database = FirebaseDatabase.getInstance().reference

        // Term spinner
        val terms = listOf("1st Term", "2nd Term", "Final")
        spinnerTerm.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, terms).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Adapter fixed: only pass students
        adapter = TeacherStudentMarksAdapter(students)
        rvStudents.layoutManager = LinearLayoutManager(requireContext())
        rvStudents.adapter = adapter

        loadSchedulesForTeacher()

        btnLoadStudents.setOnClickListener {
            val pos = spinnerSchedules.selectedItemPosition
            if (pos < 0 || pos >= schedulesList.size) {
                Toast.makeText(requireContext(), "Select a schedule first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selected = schedulesList[pos]
            loadStudentsForClass(selected)
        }

        btnAddComponentGlobal.setOnClickListener {
            showAddComponentDialogGlobal()
        }

        btnSaveMarks.setOnClickListener {
            saveMarksToFirebase()
        }

        return v
    }

    private fun loadSchedulesForTeacher() {
        val classesRef = database.child("classes")
        classesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                schedulesList.clear()
                scheduleDisplay.clear()

                for (classSnap in snapshot.children) {
                    val className = classSnap.child("className").getValue(String::class.java) ?: "Unknown"
                    val subjectsSnap = classSnap.child("subjects")
                    for (subjectSnap in subjectsSnap.children) {
                        val teacher = subjectSnap.child("teacherId").getValue(String::class.java) ?: continue
                        if (teacher != teacherId) continue

                        val subjectName = subjectSnap.child("subjectName").getValue(String::class.java) ?: "Unknown"
                        val roomNumber = subjectSnap.child("roomNumber").getValue(String::class.java) ?: ""

                        val display = "$className/$subjectName"
                        scheduleDisplay.add(display)

                        schedulesList.add(
                            SubjectSchedule(
                                className = className,
                                subjectName = subjectName,
                                teacherName = teacher,
                                roomNumber = roomNumber,
                                day = "",
                                time = ""
                            )
                        )
                    }
                }

                if (scheduleDisplay.isEmpty()) scheduleDisplay.add("No schedules assigned")

                spinnerSchedules.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, scheduleDisplay).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load schedules: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadStudentsForClass(schedule: SubjectSchedule) {
        students.clear()
        adapter.notifyDataSetChanged()

        val usersRef = database.child("users")
        val query = usersRef.orderByChild("class").equalTo(schedule.className)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                students.clear()
                for (s in snapshot.children) {
                    val role = s.child("role").getValue(String::class.java) ?: ""
                    if (role != "student") continue

                    val id = s.child("user_id").getValue(String::class.java) ?: continue
                    val name = s.child("name").getValue(String::class.java) ?: "Unknown"
                    val st = StudentMarkUpload(id = id, name = name)

                    // Add global components
                    for (comp in classComponents) st.marks.putIfAbsent(comp, 0f)
                    st.marks.putIfAbsent("Attendance", 0f)
                    st.marks.putIfAbsent("Exam", 0f)

                    students.add(st)
                }

                if (students.isEmpty())
                    Toast.makeText(requireContext(), "No students found for ${schedule.className}", Toast.LENGTH_SHORT).show()

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed loading students: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddComponentDialogGlobal() {
        val ctx = requireContext()
        val et = EditText(ctx)
        et.hint = "Component name (e.g., Quiz 1)"

        android.app.AlertDialog.Builder(ctx)
            .setTitle("Add Component (global for this class)")
            .setView(et)
            .setPositiveButton("Add") { _, _ ->
                val name = et.text.toString().trim()
                if (name.isEmpty() || classComponents.contains(name)) return@setPositiveButton

                classComponents.add(name)

                // Update all students' components
                for (student in students) {
                    if (student.components.none { it.name == name }) {
                        student.components.add(ComponentItem(name = name, maxMarks = 100f))
                    }
                }

                // Refresh RecyclerView
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun saveMarksToFirebase() {
        val pos = spinnerSchedules.selectedItemPosition
        if (pos < 0 || pos >= schedulesList.size) {
            Toast.makeText(requireContext(), "Select schedule first", Toast.LENGTH_SHORT).show()
            return
        }
        val schedule = schedulesList[pos]
        val term = spinnerTerm.selectedItem.toString()

        val baseRef = database.child("results").child("${schedule.className}_${schedule.subjectName}").child(term).child("students")
        val componentsRef = database.child("results").child("${schedule.className}_${schedule.subjectName}").child(term).child("components")
        componentsRef.setValue(classComponents)

        val updates = hashMapOf<String, Any>()
        for (st in students) {
            val mapToUpload = mutableMapOf<String, Any>()
            for ((k, v) in st.marks) mapToUpload[k] = v
            updates[st.id] = mapToUpload
        }

        baseRef.updateChildren(updates as Map<String, Any>).addOnSuccessListener {
            Toast.makeText(requireContext(), "Marks uploaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Upload failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}
