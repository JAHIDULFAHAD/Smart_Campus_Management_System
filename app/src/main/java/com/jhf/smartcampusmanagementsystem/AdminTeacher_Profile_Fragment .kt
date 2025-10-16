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

class AdminTeacher_Profile_Fragment : Fragment(R.layout.fragment_profile) {

    private lateinit var avatarImage: CircleImageView
    private lateinit var buttonUpload: Button
    private lateinit var updateButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var logoutButton: Button

    private lateinit var userIdEdit: EditText
    private lateinit var nameEdit: EditText
    private lateinit var roleDeptEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var mobileEdit: EditText
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

        avatarImage = view.findViewById(R.id.profileImage)
        buttonUpload = view.findViewById(R.id.btnUploadImage)
        updateButton = view.findViewById(R.id.btnUpdateProfile)
        changePasswordButton = view.findViewById(R.id.btnChangePassword)
        logoutButton = view.findViewById(R.id.btnLogout)

        userIdEdit = view.findViewById(R.id.etUserId)
        nameEdit = view.findViewById(R.id.etName)
        roleDeptEdit = view.findViewById(R.id.etClassOrDept)
        emailEdit = view.findViewById(R.id.etEmail)
        mobileEdit = view.findViewById(R.id.etMobile)
        currentPassword = view.findViewById(R.id.etCurrentPassword)
        newPassword = view.findViewById(R.id.etNewPassword)

        loadUserData()
        setupPasswordToggle(currentPassword)
        setupPasswordToggle(newPassword)

        buttonUpload.setOnClickListener { checkGalleryPermission() }
        updateButton.setOnClickListener { updateUserData() }
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
                    val selStart = editText.selectionStart
                    val selEnd = editText.selectionEnd
                    editText.inputType = if (isVisible)
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    else
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        if (isVisible) R.drawable.eye_on else R.drawable.eye_off, 0)
                    editText.setSelection(selStart, selEnd)
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
                setPadding(50,50,50,50)
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

    private fun loadUserData() {
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        userIdEdit.setText(sharedPref.getString("user_id", ""))
        nameEdit.setText(sharedPref.getString("name", ""))
        roleDeptEdit.setText(sharedPref.getString("department",""))
        emailEdit.setText(sharedPref.getString("email",""))
        mobileEdit.setText(sharedPref.getString("mobile",""))

        val profileImageUrl = sharedPref.getString("profileImage", "")
        Glide.with(requireContext())
            .load(if (!profileImageUrl.isNullOrEmpty()) profileImageUrl else R.drawable.blank_profile)
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

    private fun updateUserData() {
        val name = nameEdit.text.toString().trim()
        val email = emailEdit.text.toString().trim()
        val mobile = mobileEdit.text.toString().trim()

        if(name.isEmpty()){ nameEdit.error = "Required"; nameEdit.requestFocus(); return }
        if(email.isEmpty()){ emailEdit.error = "Required"; emailEdit.requestFocus(); return }
        if(mobile.isEmpty()){ mobileEdit.error = "Required"; mobileEdit.requestFocus(); return }

        showLoading()
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null) ?: run { hideLoading(); return }

        val dataMap = mutableMapOf<String, Any>(
            "name" to name,
            "email" to email,
            "mobile" to mobile,
            "department" to roleDeptEdit.text.toString().trim()
        )

        if(imageUri != null) uploadImageCompressed(userId, imageUri!!, dataMap)
        else saveToFirebaseAndPref(userId, dataMap)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
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
                .addOnFailureListener { hideLoading(); Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show() }
                .continueWithTask { imageRef.downloadUrl }
                .addOnCompleteListener { task ->
                    hideLoading()
                    if(task.isSuccessful){
                        dataMap["profileImage"] = task.result.toString()
                        saveToFirebaseAndPref(userId, dataMap)
                    }
                }
        } catch(e: Exception){
            hideLoading()
            e.printStackTrace()
            Toast.makeText(requireContext(), "Image processing failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFirebaseAndPref(userId: String, finalData: Map<String, Any>){
        databaseRef.child(userId).updateChildren(finalData)
            .addOnSuccessListener {
                hideLoading()
                val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
                sharedPref.edit().apply { finalData.forEach{(k,v)-> putString(k,v.toString()) }; apply() }

                // ✅ Update toolbar & bottom sheet in Admin_Home_page
                (activity as? Admin_Home_page)?.updateProfileImage(finalData["profileImage"]?.toString() ?: "")

                Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { hideLoading(); Toast.makeText(requireContext(), "Update Failed", Toast.LENGTH_SHORT).show() }
    }

    private fun changePassword() {
        val currPass = currentPassword.text.toString().trim()
        val newPass = newPassword.text.toString().trim()
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val savedPassword = sharedPref.getString("password","")

        if(currPass.isEmpty()){ currentPassword.error="Required"; currentPassword.requestFocus(); return }
        if(newPass.isEmpty()){ newPassword.error="Required"; newPassword.requestFocus(); return }
        if(currPass != savedPassword){ Toast.makeText(requireContext(),"Current password incorrect",Toast.LENGTH_SHORT).show(); return }

        showLoading()
        val userId = sharedPref.getString("user_id", null) ?: run { hideLoading(); return }

        databaseRef.child(userId).child("password").setValue(newPass)
            .addOnSuccessListener { hideLoading(); sharedPref.edit().putString("password", newPass).apply(); Toast.makeText(requireContext(),"Password changed successfully",Toast.LENGTH_SHORT).show(); currentPassword.text.clear(); newPassword.text.clear() }
            .addOnFailureListener { hideLoading(); Toast.makeText(requireContext(),"Failed to change password",Toast.LENGTH_SHORT).show() }
    }

    private fun logoutUser() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes"){ _, _ ->
                val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
                sharedPref.edit().clear().apply()
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
