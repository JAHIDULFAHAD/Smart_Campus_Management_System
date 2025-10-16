package com.jhf.smartcampusmanagementsystem.Data

data class SubjectModel(
    val name: String = "",
    val teacher: String = "",
    val exams: List<ExamNameModel> = emptyList()
)
