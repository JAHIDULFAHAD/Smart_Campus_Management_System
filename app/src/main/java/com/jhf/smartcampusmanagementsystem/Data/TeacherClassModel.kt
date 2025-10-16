package com.jhf.smartcampusmanagementsystem

data class TeacherClassModel(
    val className: String = "",
    val subjectName: String = "",
    val teacherName: String = "",
    val room: String = "",
    val time: String = "",
    val color: Int = 0 // random color for display
)
