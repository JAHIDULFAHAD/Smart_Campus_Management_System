package com.jhf.smartcampusmanagementsystem

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class Student_Profile_Fragment : Fragment(R.layout.fragment_student_profile) {

    private lateinit var avatarImage: CircleImageView
    private lateinit var buttonUpload: Button
    private lateinit var updateButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var logoutButton: Button

    private lateinit var studentId: EditText
    private lateinit var studentClass: EditText
    private lateinit var studentName: EditText
    private lateinit var fatherName: EditText
    private lateinit var motherName: EditText
    private lateinit var studentEmail: EditText
    private lateinit var studentNumber: EditText
    private lateinit var studentDOB: EditText
    private lateinit var studentAddress: EditText
    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText

    private var imageUri: Uri? = null
    private var loadingDialog: AlertDialog? = null

    private val storageRef = FirebaseStorage.getInstance().reference
    private val databaseRef = FirebaseDatabase.getInstance().getReference("users")

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            imageUri = result.data?.data
            avatarImage.setImageURI(imageUri)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openGalleryIntent()
            else Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        avatarImage = view.findViewById(R.id.avatarImage)
        buttonUpload = view.findViewById(R.id.buttonUploadImage)
        updateButton = view.findViewById(R.id.buttonUpdateProfile)
        changePasswordButton = view.findViewById(R.id.buttonChangePassword)
        logoutButton = view.findViewById(R.id.buttonLogout)

        studentId = view.findViewById(R.id.StudentId)
        studentClass = view.findViewById(R.id.StudentClass)
        studentName = view.findViewById(R.id.StudentName)
        fatherName = view.findViewById(R.id.FatherName)
        motherName = view.findViewById(R.id.MotherName)
        studentEmail = view.findViewById(R.id.StudentEmail)
        studentNumber = view.findViewById(R.id.StudentNumber)
        studentDOB = view.findViewById(R.id.StudentDOB)
        studentAddress = view.findViewById(R.id.StudentAddress)
        currentPassword = view.findViewById(R.id.StudentCurrentPassword)
        newPassword = view.findViewById(R.id.StudentNewPassword)

        loadStudentData()

        setupPasswordToggle(currentPassword)
        setupPasswordToggle(newPassword)

        buttonUpload.setOnClickListener { checkGalleryPermission() }
        updateButton.setOnClickListener { updateStudentData() }
        changePasswordButton.setOnClickListener { changePassword() }
        logoutButton.setOnClickListener { logoutUser() }
    }

    private fun setupPasswordToggle(editText: EditText) {
        var isVisible = false
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = editText.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (editText.right - drawableEnd.bounds.width())) {
                    isVisible = !isVisible
                    val selectionStart = editText.selectionStart
                    val selectionEnd = editText.selectionEnd
                    if (isVisible) {
                        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_on, 0)
                    } else {
                        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_off, 0)
                    }
                    editText.setSelection(selectionStart, selectionEnd)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun showLoading() {
        if (loadingDialog == null) {
            val layout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 50, 50, 50)
                gravity = Gravity.CENTER
                setBackgroundColor(Color.parseColor("#80000000"))
            }

            val progressBar = ProgressBar(requireContext()).apply { isIndeterminate = true }
            val loadingText = TextView(requireContext()).apply {
                text = "Loading..."
                setTextColor(Color.WHITE)
                textSize = 18f
                setPadding(0, 20, 0, 0)
            }

            layout.addView(progressBar)
            layout.addView(loadingText)

            loadingDialog = AlertDialog.Builder(requireContext())
                .setView(layout)
                .setCancelable(false)
                .create()
        }
        loadingDialog?.show()
    }

    private fun hideLoading() { loadingDialog?.dismiss() }

    private fun loadStudentData() {
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        studentId.setText(sharedPref.getString("user_id", ""))
        studentClass.setText(sharedPref.getString("class", ""))
        studentName.setText(sharedPref.getString("name", ""))
        fatherName.setText(sharedPref.getString("father_name", ""))
        motherName.setText(sharedPref.getString("mother_name", ""))
        studentEmail.setText(sharedPref.getString("email", ""))
        studentNumber.setText(sharedPref.getString("mobile", ""))
        studentDOB.setText(sharedPref.getString("dob", ""))
        studentAddress.setText(sharedPref.getString("address", ""))

        val profileImage = sharedPref.getString("profileImage", "")
        Glide.with(requireContext())
            .load(if (!profileImage.isNullOrEmpty()) profileImage else R.drawable.blank_profile)
            .into(avatarImage)
    }

    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else openGalleryIntent()
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            else openGalleryIntent()
        }
    }

    private fun openGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun updateStudentData() {
        val name = studentName.text.toString().trim()
        val email = studentEmail.text.toString().trim()
        val mobile = studentNumber.text.toString().trim()
        val dob = studentDOB.text.toString().trim()
        val address = studentAddress.text.toString().trim()

        if (name.isEmpty()) { studentName.error = "Required"; studentName.requestFocus(); return }
        if (email.isEmpty()) { studentEmail.error = "Required"; studentEmail.requestFocus(); return }
        if (mobile.isEmpty()) { studentNumber.error = "Required"; studentNumber.requestFocus(); return }
        if (dob.isEmpty()) { studentDOB.error = "Required"; studentDOB.requestFocus(); return }
        if (address.isEmpty()) { studentAddress.error = "Required"; studentAddress.requestFocus(); return }

        showLoading()
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)
        if (userId.isNullOrEmpty()) {
            hideLoading()
            Toast.makeText(requireContext(), "User ID not found. Login required.", Toast.LENGTH_SHORT).show()
            return
        }

        val dataMap = mutableMapOf<String, Any>(
            "name" to name,
            "email" to email,
            "mobile" to mobile,
            "dob" to dob,
            "address" to address,
            "father_name" to fatherName.text.toString().trim(),
            "mother_name" to motherName.text.toString().trim(),
            "class" to studentClass.text.toString().trim()
        )

        if (imageUri != null) uploadImageCompressed(userId, imageUri!!, dataMap)
        else saveToFirebaseAndPref(userId, dataMap)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
    }

    private fun uploadImageCompressed(userId: String, uri: Uri, dataMap: MutableMap<String, Any>) {
        try {
            val bitmap = getBitmapFromUri(uri)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val imageData = baos.toByteArray()

            val fileName = "${userId}_${System.currentTimeMillis()}.jpg"
            val imageRef = storageRef.child("profile_images/$fileName")

            showLoading()
            imageRef.putBytes(imageData)
                .addOnFailureListener { e ->
                    hideLoading()
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Upload Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
                .continueWithTask { imageRef.downloadUrl }
                .addOnCompleteListener { task ->
                    hideLoading()
                    if (task.isSuccessful) {
                        dataMap["profileImage"] = task.result.toString()
                        saveToFirebaseAndPref(userId, dataMap)
                    } else {
                        task.exception?.printStackTrace()
                        Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            hideLoading()
            e.printStackTrace()
            Toast.makeText(requireContext(), "Image Processing Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFirebaseAndPref(userId: String, finalData: Map<String, Any>) {
        databaseRef.child(userId).updateChildren(finalData)
            .addOnSuccessListener {
                hideLoading()
                val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
                sharedPref.edit().apply { finalData.forEach { (k,v) -> putString(k,v.toString()) }; apply() }
                Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show()

                // Toolbar + Bottom sheet update
                val activity = requireActivity() as? Home_Page
                finalData["profileImage"]?.let { activity?.updateProfileImage(it.toString()) }
            }
            .addOnFailureListener { hideLoading(); Toast.makeText(requireContext(), "Update Failed", Toast.LENGTH_SHORT).show() }
    }

    private fun changePassword() {
        val currPass = currentPassword.text.toString().trim()
        val newPass = newPassword.text.toString().trim()
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val savedPassword = sharedPref.getString("password", "")

        if (currPass.isEmpty()) { currentPassword.error = "Required"; currentPassword.requestFocus(); return }
        if (newPass.isEmpty()) { newPassword.error = "Required"; newPassword.requestFocus(); return }
        if (currPass != savedPassword) { Toast.makeText(requireContext(), "Current password incorrect", Toast.LENGTH_SHORT).show(); return }

        showLoading()
        val userId = sharedPref.getString("user_id", null) ?: run {
            hideLoading()
            Toast.makeText(requireContext(), "Login required", Toast.LENGTH_SHORT).show()
            return
        }

        databaseRef.child(userId).child("password").setValue(newPass)
            .addOnSuccessListener {
                hideLoading()
                sharedPref.edit().putString("password", newPass).apply()
                Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                currentPassword.text.clear()
                newPassword.text.clear()
            }
            .addOnFailureListener { hideLoading(); Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show() }
    }

    private fun logoutUser() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
                sharedPref.edit().clear().apply()
                Toast.makeText(requireContext(), "You are logged out", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
