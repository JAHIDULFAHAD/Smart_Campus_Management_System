package com.jhf.smartcampusmanagementsystem

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.jhf.smartcampusmanagementsystem.Data.Teacher
import java.text.SimpleDateFormat
import java.util.*

class Admin_Add_Teacher_Fragment : Fragment() {

    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etDOB: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var spinnerQualification: Spinner
    private lateinit var spinnerDepartment: Spinner
    private lateinit var btnSubmit: MaterialButton

    private val qualificationList = arrayOf("Select Qualification", "BSc", "MSc", "BEd", "MEd", "PhD")
    private val departmentList = arrayOf("Select Department", "Math", "Science", "English", "Bangla", "Computer")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin__add__teacher_, container, false)

        // Initialize views
        etFirstName = view.findViewById(R.id.etTeacherFirstName)
        etLastName = view.findViewById(R.id.etTeacherLastName)
        etPhone = view.findViewById(R.id.etTeacherPhone)
        etEmail = view.findViewById(R.id.etTeacherEmail)
        etDOB = view.findViewById(R.id.etTeacherDOB)
        etAddress = view.findViewById(R.id.etTeacherAddress)
        spinnerQualification = view.findViewById(R.id.spinnerQualification)
        spinnerDepartment = view.findViewById(R.id.spinnerDepartment)
        btnSubmit = view.findViewById(R.id.btnSubmitTeacher)

        // Setup Spinners
        spinnerQualification.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, qualificationList)
        spinnerDepartment.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, departmentList)

        // Setup DatePicker for DOB
        etDOB.setOnClickListener {
            showDatePicker()
        }

        btnSubmit.setOnClickListener {
            if (validateFields()) {
                addTeacherToFirebase()
            }
        }

        return view
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val cal = Calendar.getInstance()
            cal.set(selectedYear, selectedMonth, selectedDay)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            etDOB.setText(format.format(cal.time))
        }, year, month, day)

        datePicker.show()
    }

    private fun validateFields(): Boolean {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val qualification = spinnerQualification.selectedItem.toString()
        val department = spinnerDepartment.selectedItem.toString()
        val dob = etDOB.text.toString().trim()

        if (firstName.isEmpty()) {
            etFirstName.error = "First name is required"
            etFirstName.requestFocus()
            return false
        }
        if (lastName.isEmpty()) {
            etLastName.error = "Last name is required"
            etLastName.requestFocus()
            return false
        }
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email format"
            etEmail.requestFocus()
            return false
        }
        if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
            etPhone.requestFocus()
            return false
        }
        if (phone.length != 11) {
            etPhone.error = "Phone number must be 11 digits"
            etPhone.requestFocus()
            return false
        }
        if (dob.isEmpty()) {
            etDOB.error = "Date of Birth is required"
            etDOB.requestFocus()
            return false
        }
        if (qualification == "Select Qualification") {
            Toast.makeText(requireContext(), "Please select Qualification", Toast.LENGTH_SHORT).show()
            return false
        }
        if (department == "Select Department") {
            Toast.makeText(requireContext(), "Please select Department", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun addTeacherToFirebase() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val qualification = spinnerQualification.selectedItem.toString()
        val department = spinnerDepartment.selectedItem.toString()
        val dob = etDOB.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val fullName = "$firstName $lastName"

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("users")

        // Generate unique user ID
        val fixedPrefix = "25"
        val fixedSuffix = "1"
        val autoNumber = System.currentTimeMillis() // timestamp as unique number
        val userId = "$fixedPrefix-$autoNumber-$fixedSuffix"

        val teacher = Teacher(
            user_id = userId,
            name = fullName,
            email = email,
            mobile = phone,
            dob = dob,
            qualification = qualification,
            department = department,
            address = address,
            password = userId, // default password
            role = "teacher"
        )

        // ✅ Add using custom ID, no push()
        ref.child(userId).setValue(teacher)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Teacher added: $userId", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        etFirstName.text?.clear()
        etLastName.text?.clear()
        etPhone.text?.clear()
        etEmail.text?.clear()
        etDOB.text?.clear()
        etAddress.text?.clear()
        spinnerQualification.setSelection(0)
        spinnerDepartment.setSelection(0)
    }
}
