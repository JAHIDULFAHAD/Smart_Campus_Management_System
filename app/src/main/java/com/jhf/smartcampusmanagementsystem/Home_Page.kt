package com.jhf.smartcampusmanagementsystem

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jhf.smartcampusmanagementsystem.databinding.ActivityHomePageBinding

class Home_Page : AppCompatActivity() {
    lateinit var binding: ActivityHomePageBinding
    lateinit var profileImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        replaceFragment(Student_Dashboard())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(Student_Dashboard())
                    true
                }
                R.id.class_schedule -> {
                    replaceFragment(Student_Cls_TimeTable())
                    true
                }
                R.id.result -> {
                    replaceFragment(Student_result())
                    true
                }
                R.id.profile -> {
                    replaceFragment(Student_Profile_Fragment())
                    true
                }
                else -> false
            }
        }

        profileImage = findViewById(R.id.avatarImage)
        profileImage.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_student, null)

            view.findViewById<TextView>(R.id.tvName).text = "Jahidul Haque Fahad"
            view.findViewById<TextView>(R.id.tvPhone).text = "01626383239"

            view.findViewById<Button>(R.id.btnProfile).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()
        }

    }
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment) // fragment_container = FrameLayout id
        fragmentTransaction.commit()
    }
}