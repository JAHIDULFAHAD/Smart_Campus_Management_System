package com.jhf.smartcampusmanagementsystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jhf.smartcampusmanagementsystem.databinding.ActivityAdminHomePageBinding

class Admin_Home_page : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomePageBinding
    private lateinit var profileImage: ImageView
    private lateinit var tvToolbarName: TextView
    private lateinit var toolbarLogout: ImageView

    // Bottom sheet image reference
    private var bottomSheetImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Shared Preferences
        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "Admin Name") ?: "Admin Name"
        val mobile = sharedPref.getString("mobile", "0000") ?: "0000"
        val imageUrl = sharedPref.getString("profileImage", "")

        // Toolbar views
        tvToolbarName = findViewById(R.id.name)
        profileImage = findViewById(R.id.avatarImage)
        toolbarLogout = findViewById(R.id.buttonLogout)
        tvToolbarName.text = name

        // Load profile image
        Glide.with(this)
            .load(if (!imageUrl.isNullOrEmpty()) imageUrl else R.drawable.blank_profile)
            .into(profileImage)

        // Logout
        toolbarLogout.setOnClickListener { showLogoutConfirmation() }

        // Default fragment
        replaceFragment(Admin_Dashboards())

        // Bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> { replaceFragment(Admin_Dashboards()); true }
                R.id.users -> { replaceFragment(Admin_Users_Fragment()); true }
                R.id.classes -> { replaceFragment(Admin_Manage_Classes_Fragment()); true }
                R.id.notices -> { replaceFragment(Admin_Notice_Fragment()); true }
                R.id.profile -> { replaceFragment(AdminTeacher_Profile_Fragment()); true }
                else -> false
            }
        }

        // Profile image → Bottom sheet
        profileImage.setOnClickListener { showBottomSheet(name, mobile, imageUrl) }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    private fun showBottomSheet(name: String, phone: String, imageUrl: String?) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_student, null)

        val tvBottomSheetName: TextView = view.findViewById(R.id.tvName)
        val tvBottomSheetPhone: TextView = view.findViewById(R.id.tvPhone)
        bottomSheetImage = view.findViewById(R.id.avatarImage)
        val btnLogout: Button = view.findViewById(R.id.btnLogout)
        val btnProfile: Button = view.findViewById(R.id.btnProfile)

        tvBottomSheetName.text = name
        tvBottomSheetPhone.text = phone

        Glide.with(this)
            .load(if (!imageUrl.isNullOrEmpty()) imageUrl else R.drawable.blank_profile)
            .into(bottomSheetImage!!)

        btnLogout.setOnClickListener {
            bottomSheetDialog.dismiss()
            showLogoutConfirmation()
        }

        btnProfile.setOnClickListener {
            bottomSheetDialog.dismiss()
            replaceFragment(AdminTeacher_Profile_Fragment())
            binding.bottomNavigationView.selectedItemId = R.id.profile
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun showLogoutConfirmation() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            performLogout()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun performLogout() {
        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /** Update profile image dynamically for toolbar & bottom sheet */
    fun updateProfileImage(newImageUrl: String) {
        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        sharedPref.edit().putString("profileImage", newImageUrl).apply()

        Glide.with(this)
            .load(if (newImageUrl.isNotEmpty()) newImageUrl else R.drawable.blank_profile)
            .into(profileImage)

        bottomSheetImage?.let { img ->
            Glide.with(this)
                .load(if (newImageUrl.isNotEmpty()) newImageUrl else R.drawable.blank_profile)
                .into(img)
        }
    }
}
