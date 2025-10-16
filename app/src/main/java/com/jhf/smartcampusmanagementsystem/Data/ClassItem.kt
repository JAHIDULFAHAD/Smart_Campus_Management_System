package com.jhf.smartcampusmanagementsystem.Data

data class ClassItem(
    var id: String = "",
    var className: String = "",
    var subjects: MutableList<SubjectItem> = mutableListOf()
)
