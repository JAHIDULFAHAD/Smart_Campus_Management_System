plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    kotlin("kapt")
}

android {
    namespace = "com.jhf.smartcampusmanagementsystem"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jhf.smartcampusmanagementsystem"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        // ✅ Use Java 17 to match Kotlin JVM target
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17" // ✅ Make Kotlin JVM target same as Java
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // ✅ Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // ✅ Firebase SDKs
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // ✅ Other libraries
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // ✅ Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // ✅ CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // ✅ Android Navigation (update according to your libs version)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}
