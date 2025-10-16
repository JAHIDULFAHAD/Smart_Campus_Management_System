package com.jhf.smartcampusmanagementsystem.Data

data class Notice(
    val title: String? = "",
    val description: String? = "",
    var timestamp: Long = 0L,
    val type: String? = "student"
)
