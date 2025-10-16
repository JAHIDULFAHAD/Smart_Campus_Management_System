package com.jhf.smartcampusmanagementsystem.Data

data class UserModel(
    val user_id: String? = "",
    val name: String? = "",
    val role: String? = "",
    val email: String? = "",
    val mobile: String? = "",
    val className: String? = "",       // for students
    val father_name: String? = "",     // for students
    val mother_name: String? = "",     // for students
    val dob: String? = "",
    val address: String? = "",
    val department: String? = "",      // for teachers
    val qualification: String? = "",   // for teachers
    val profileImage: String? = ""     // optional
)

