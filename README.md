# ITSC Smart Campus Management System

A comprehensive Android application built with Kotlin and Firebase designed to streamline campus operations for administrators, teachers, and students. This system provides a centralized platform for managing classes, schedules, notices, and academic results.

## 🚀 Features

### 🔑 Authentication
- Secure login system for Admins, Teachers, and Students using Firebase Authentication.
- Role-based access control to ensure data privacy and relevant functionality.

### 🛡️ Admin Module
- **User Management:** Add and manage student and teacher profiles.
- **Academic Management:** Create and organize classes, subjects, and schedules.
- **Communication:** Post official notices for the entire campus.
- **Analytics:** Dashboard for an overview of campus statistics.

### 👨‍🏫 Teacher Module
- **Class Schedules:** View assigned classes and daily timetables.
- **Result Management:** Upload and manage student marks and academic performance.
- **Notices:** Stay updated with administrative announcements.
- **Profile:** Manage personal information and profile images.

### 🎓 Student Module
- **Personal Timetable:** Access real-time class schedules.
- **Academic Results:** View grades and performance components (exams, assignments).
- **Notices:** Receive instant updates on campus news.
- **Digital Profile:** Personalized dashboard with profile customization.

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** Android XML with Material Design 3
- **Backend:** [Firebase](https://firebase.google.com/)
    - **Authentication:** User identity management.
    - **Realtime Database:** Live data synchronization for schedules and results.
    - **Cloud Storage:** Secure storage for profile images.
    - **Cloud Functions:** Server-side logic for background tasks.
- **Libraries:**
    - **Navigation Component:** For seamless fragment transitions.
    - **ViewBinding:** Type-safe UI interaction.
    - **Glide:** Efficient image loading and caching.
    - **CircleImageView:** Enhanced profile UI.

## 📦 Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/smart-campus-management-system.git
    ```
2.  **Open in Android Studio:**
    - File > Open > Select the project folder.
3.  **Firebase Configuration:**
    - Create a new project in the [Firebase Console](https://console.firebase.google.com/).
    - Register the app with package name `com.jhf.smartcampusmanagementsystem`.
    - Download `google-services.json` and place it in the `app/` directory.
    - Enable **Email/Password** authentication.
    - Enable **Realtime Database** and **Cloud Storage**.
4.  **Sync Project:**
    - Click "Sync Project with Gradle Files" in Android Studio.
5.  **Run:**
    - Connect an Android device or emulator and click **Run**.

## 📐 Architecture

The project follows a modular structure focused on separation of concerns:
- **Adapters:** Handling complex list views (RecyclerViews, GridViews).
- **Data:** Data models (POJOs) representing Campus entities.
- **Firebase:** Helper classes for backend interactions.
- **UI:** Fragments and Activities categorized by user roles (Admin, Teacher, Student).

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
*Developed as part of the Mobile App Development course.*
