package com.jhf.smartcampusmanagementsystem.Data

sealed class ResultItem {
    data class TermHeader(
        val term: String,
        val obtainedMarks: Int,
        val maxMarks: Int,
        val grade: String
    ) : ResultItem()

    data class ComponentItem(val name: String, val marks: Int) : ResultItem()
}
