package com.jhf.smartcampusmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btn: Button
    private lateinit var password: EditText
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btn = findViewById(R.id.btnlogin)
        btn.setOnClickListener {
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Home_Page::class.java)
            startActivity(intent)
        }

        password = findViewById(R.id.StudentPassword)
        password.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = password.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (password.right - drawableEnd.bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    val selectionStart = password.selectionStart
                    val selectionEnd = password.selectionEnd
                    if (isPasswordVisible) {
                        password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_on, 0)
                    } else {
                        password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_off, 0)
                    }

                    password.setSelection(selectionStart, selectionEnd)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}
