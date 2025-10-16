package com.jhf.smartcampusmanagementsystem

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class FirebaseHelper(private val context: Context) {

    private val storageRef = FirebaseStorage.getInstance(
        "gs://smartcampusmanagement-cce20.appspot.com"
    ).reference
    private val databaseRef = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    // Convert Uri to Bitmap
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val cr: ContentResolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(cr, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(cr, uri)
        }
    }

    // Upload profile image
    fun uploadProfileImage(uri: Uri, onComplete: (success: Boolean, url: String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onComplete(false, "User not logged in")
            return
        }
        val userId = user.uid

        try {
            val bitmap = getBitmapFromUri(uri)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val imageData = baos.toByteArray()

            val imageRef = storageRef.child("profile_images/$userId.jpg")
            imageRef.putBytes(imageData)
                .continueWithTask { task ->
                    if (!task.isSuccessful) task.exception?.let { throw it }
                    imageRef.downloadUrl
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        databaseRef.child(userId).child("profileImage").setValue(downloadUri.toString())
                            .addOnSuccessListener {
                                onComplete(true, downloadUri.toString())
                            }
                            .addOnFailureListener {
                                onComplete(false, "Database update failed")
                            }
                    } else {
                        onComplete(false, "Image upload failed")
                    }
                }

        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(false, "Image processing failed")
        }
    }
}
