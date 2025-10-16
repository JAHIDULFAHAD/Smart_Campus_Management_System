package com.jhf.smartcampusmanagementsystem  // তোমার প্যাকেজ নাম

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
