package com.jhf.smartcampusmanagementsystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var etUserId: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private var isPasswordVisible = false
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔹 Auto-login check
        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", null)
        if (!role.isNullOrEmpty()) {
            startHomePage(role)
            return
        }

        etUserId = findViewById(R.id.studentId)
        etPassword = findViewById(R.id.studentPassword)
        btnLogin = findViewById(R.id.btnlogin)

        // 🔹 Password show/hide toggle
        etPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = etPassword.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (etPassword.right - drawableEnd.bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    val start = etPassword.selectionStart
                    val end = etPassword.selectionEnd
                    etPassword.inputType = if (isPasswordVisible)
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    else
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    etPassword.setSelection(start, end)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // 🔹 Login click listener
        btnLogin.setOnClickListener {
            val userId = etUserId.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (userId.isEmpty()) {
                etUserId.error = "User ID required"
                etUserId.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = "Password required"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            showLoading()
            val dbRef = FirebaseDatabase.getInstance().getReference("users")
            dbRef.child(userId).get().addOnSuccessListener { snapshot ->
                hideLoading()
                if (!snapshot.exists()) {
                    Toast.makeText(this, "❌ User not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val dbPassword = snapshot.child("password").getValue(String::class.java) ?: ""
                val role = snapshot.child("role").getValue(String::class.java) ?: "student"

                if (password != dbPassword) {
                    Toast.makeText(this, "❌ Incorrect Password", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // 🔹 Save all snapshot data AND important keys
                val editor = getSharedPreferences("user_pref", Context.MODE_PRIVATE).edit()
                snapshot.children.forEach { editor.putString(it.key, it.value.toString()) }

                val studentId = snapshot.child("id").getValue(String::class.java) ?: userId
                val className = snapshot.child("class").getValue(String::class.java) ?: ""
                editor.putString("studentId", studentId)
                editor.putString("role", role)
                editor.putString("class", className)
                editor.apply()

                // ✅ Start home page
                startHomePage(role)

            }.addOnFailureListener {
                hideLoading()
                Toast.makeText(this, "❌ Failed to login", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startHomePage(role: String) {
        val intent = when (role.lowercase()) {
            "admin" -> Intent(this, Admin_Home_page::class.java)
            "teacher" -> Intent(this, Teacher_Home_Page::class.java)
            else -> Intent(this, Home_Page::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        if (loadingDialog == null) {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(50, 50, 50, 50)
                setBackgroundColor(0x80000000.toInt())
                addView(ProgressBar(this@MainActivity))
                addView(TextView(this@MainActivity).apply {
                    text = "Loading..."
                    setTextColor(0xFFFFFFFF.toInt())
                    textSize = 18f
                })
            }
            loadingDialog = AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(false)
                .create()
        }
        loadingDialog?.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
    }
}
