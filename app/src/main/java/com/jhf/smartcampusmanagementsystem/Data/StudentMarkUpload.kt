package com.jhf.smartcampusmanagementsystem

import com.jhf.smartcampusmanagementsystem.Data.ComponentItem

data class StudentMarkUpload(
    val id: String,
    val name: String,
    val components: MutableList<ComponentItem> = mutableListOf()
) {
    val marks: MutableMap<String, Float> = mutableMapOf()
}
