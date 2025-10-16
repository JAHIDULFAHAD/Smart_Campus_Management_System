package com.jhf.smartcampusmanagementsystem

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase

class Admin_Notice_Fragment : Fragment() {

    private lateinit var spinnerNoticeType: Spinner
    private lateinit var etNoticeTitle: EditText
    private lateinit var etNoticeDescription: EditText
    private lateinit var btnUploadNotice: Button
    private var loadingDialog: AlertDialog? = null

    private val databaseRef = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin__notice_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerNoticeType = view.findViewById(R.id.spinnerNoticeType)
        etNoticeTitle = view.findViewById(R.id.etNoticeTitle)
        etNoticeDescription = view.findViewById(R.id.etNoticeDescription)
        btnUploadNotice = view.findViewById(R.id.btnUploadNotice)

        setupSpinner()
        btnUploadNotice.setOnClickListener { uploadNotice() }
    }

    private fun setupSpinner() {
        val types = listOf("Student", "Teacher")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNoticeType.adapter = adapter
    }

    private fun showLoading() {
        if (loadingDialog == null) {
            val progressBar = ProgressBar(requireContext())
            loadingDialog = AlertDialog.Builder(requireContext())
                .setTitle("Uploading...")
                .setView(progressBar)
                .setCancelable(false)
                .create()
        }
        loadingDialog?.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
    }

    private fun uploadNotice() {
        val type = spinnerNoticeType.selectedItem.toString().trim()
        val title = etNoticeTitle.text.toString().trim()
        val description = etNoticeDescription.text.toString().trim()

        if (title.isEmpty()) {
            etNoticeTitle.error = "Title required"
            etNoticeTitle.requestFocus()
            return
        }
        if (description.isEmpty()) {
            etNoticeDescription.error = "Description required"
            etNoticeDescription.requestFocus()
            return
        }

        showLoading()

        // Firebase path: notices/student or notices/teacher
        val noticeRef = databaseRef.child("notices").child(type.lowercase()).push()
        val noticeData = mapOf(
            "title" to title,
            "description" to description,
            "timestamp" to System.currentTimeMillis()
        )

        noticeRef.setValue(noticeData)
            .addOnSuccessListener {
                hideLoading()
                Toast.makeText(requireContext(), "Notice uploaded successfully", Toast.LENGTH_SHORT).show()
                etNoticeTitle.text.clear()
                etNoticeDescription.text.clear()
            }
            .addOnFailureListener {
                hideLoading()
                Toast.makeText(requireContext(), "Failed to upload notice", Toast.LENGTH_SHORT).show()
            }
    }
}
