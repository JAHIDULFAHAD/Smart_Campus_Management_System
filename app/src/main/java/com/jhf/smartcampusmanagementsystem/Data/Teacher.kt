package com.jhf.smartcampusmanagementsystem.Data

data class Teacher(
    var user_id: String? = "",
    var name: String? = "",
    var email: String? = "",
    var mobile: String? = "",
    var dob: String? = "",
    var qualification: String? = "",
    var department: String? = "",
    var address: String? = "",
    var password: String? = "",
    var role: String? = "teacher"
)
