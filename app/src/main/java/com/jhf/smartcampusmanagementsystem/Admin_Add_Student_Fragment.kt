package com.jhf.smartcampusmanagementsystem

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class Admin_Add_Student_Fragment : Fragment() {

    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etFatherName: TextInputEditText
    private lateinit var etMotherName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etMobile: TextInputEditText
    private lateinit var etDOB: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var spinnerClass: Spinner
    private lateinit var btnSubmit: MaterialButton

    private val ROLL_PREFIX = "25-"
    private val START_ROLL = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin__add__student_, container, false)

        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etFatherName = view.findViewById(R.id.etFatherName)
        etMotherName = view.findViewById(R.id.etMotherName)
        etEmail = view.findViewById(R.id.etEmail)
        etMobile = view.findViewById(R.id.etMobile)
        etDOB = view.findViewById(R.id.etDOB)
        etAddress = view.findViewById(R.id.etAddress)
        spinnerClass = view.findViewById(R.id.spinnerClass)
        btnSubmit = view.findViewById(R.id.btnSubmitStudent)

        setupClassSpinner()
        setupDOBPicker()
        addRealtimeValidation()

        btnSubmit.setOnClickListener {
            if (validateFields()) {
                saveStudentData()
            }
        }

        return view
    }

    private fun setupClassSpinner() {
        val classes = arrayOf(
            "Select Class", "Class 1", "Class 2", "Class 3", "Class 4",
            "Class 5", "Class 6", "Class 7", "Class 8", "Class 9", "Class 10"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClass.adapter = adapter
    }

    private fun setupDOBPicker() {
        etDOB.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireContext(), { _: DatePicker, y: Int, m: Int, d: Int ->
                val monthFormatted = String.format("%02d", m + 1)
                val dayFormatted = String.format("%02d", d)
                etDOB.setText("$dayFormatted/$monthFormatted/$y")
            }, year, month, day)
            dpd.show()
        }
    }

    private fun addRealtimeValidation() {
        etFirstName.addTextChangedListener(createWatcher { text -> etFirstName.error = if (text.isEmpty()) "First name required" else null })
        etLastName.addTextChangedListener(createWatcher { text -> etLastName.error = if (text.isEmpty()) "Last name required" else null })
        etFatherName.addTextChangedListener(createWatcher { text -> etFatherName.error = if (text.isEmpty()) "Father name required" else null })
        etMotherName.addTextChangedListener(createWatcher { text -> etMotherName.error = if (text.isEmpty()) "Mother name required" else null })
        etEmail.addTextChangedListener(createWatcher { text ->
            etEmail.error = if (text.isEmpty()) "Email required"
            else if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) "Invalid email"
            else null
        })
        etMobile.addTextChangedListener(createWatcher { text ->
            etMobile.error = if (text.isEmpty()) "Mobile required"
            else if (text.length != 11) "Must be 11 digits"
            else null
        })
        etDOB.addTextChangedListener(createWatcher { text -> etDOB.error = if (text.isEmpty()) "DOB required" else null })
        etAddress.addTextChangedListener(createWatcher { text -> etAddress.error = if (text.isEmpty()) "Address required" else null })
    }

    private fun createWatcher(validation: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validation(s.toString().trim()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun validateFields(): Boolean {
        var valid = true
        if (etFirstName.text.toString().trim().isEmpty()) valid = false
        if (etLastName.text.toString().trim().isEmpty()) valid = false
        if (etFatherName.text.toString().trim().isEmpty()) valid = false
        if (etMotherName.text.toString().trim().isEmpty()) valid = false
        val email = etEmail.text.toString().trim()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) valid = false
        val mobile = etMobile.text.toString().trim()
        if (mobile.isEmpty() || mobile.length != 11) valid = false
        if (etDOB.text.toString().trim().isEmpty()) valid = false
        if (etAddress.text.toString().trim().isEmpty()) valid = false
        if (spinnerClass.selectedItem.toString() == "Select Class") valid = false
        return valid
    }

    private fun saveStudentData() {
        val database = FirebaseDatabase.getInstance().getReference("users")

        database.get().addOnSuccessListener { snapshot ->
            val totalStudents = snapshot.childrenCount.toInt()
            val nextRollNumber = START_ROLL + totalStudents + 1
            val roll = "$ROLL_PREFIX$nextRollNumber"

            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val father = etFatherName.text.toString().trim()
            val mother = etMotherName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val dob = etDOB.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val selectedClass = spinnerClass.selectedItem.toString()

            val studentData = mapOf(
                "user_id" to roll,
                "name" to "$firstName $lastName",
                "father_name" to father,
                "mother_name" to mother,
                "email" to email,
                "mobile" to mobile,
                "dob" to dob,
                "address" to address,
                "class" to selectedClass,
                "password" to roll,
                "role" to "student",
                "profileImage" to ""
            )

            database.child(roll).setValue(studentData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "✅ Student Added!\nRoll: $roll", Toast.LENGTH_LONG).show()
                    clearFields()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "❌ Failed to Add Student", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "❌ Failed to generate roll", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        etFirstName.text?.clear()
        etLastName.text?.clear()
        etFatherName.text?.clear()
        etMotherName.text?.clear()
        etEmail.text?.clear()
        etMobile.text?.clear()
        etDOB.text?.clear()
        etAddress.text?.clear()
        spinnerClass.setSelection(0)
    }
}
