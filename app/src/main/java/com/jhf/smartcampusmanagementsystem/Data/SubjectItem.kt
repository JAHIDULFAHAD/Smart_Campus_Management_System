package com.jhf.smartcampusmanagementsystem.Data

data class SubjectItem(
    var id: String = "",
    var subjectName: String = "",
    var teacherId: String = "",      // ✅ Teacher ID for unique identification
    var teacherName: String = "",    // UI display only
    var roomNumber: String = "",
    var schedule: MutableList<ScheduleItem> = mutableListOf()
)
