package com.jhf.smartcampusmanagementsystem

class ExamComponentModel (
    val name: String,
    val total: Int,
    val pass: Int,
    val contributes: Int,
    val mark: Float?,
    val subComponents: List<ExamComponentModel>? = null
)