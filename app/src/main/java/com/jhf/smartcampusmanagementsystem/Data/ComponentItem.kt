package com.jhf.smartcampusmanagementsystem.Data

data class ComponentItem(
    var name: String = "",
    val maxMarks: Float = 100f,
    val minMarks: Float = 0f,
    val passMarks: Float = 0f,
    var obtainedMarks: Float = 0f,
    val subComponents: MutableList<ComponentItem> = mutableListOf()
)
