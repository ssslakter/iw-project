plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.iw.aeroskin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iw.aeroskin"
        minSdk = 24 // CameraX works best with API 24+
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    // --- ADD THIS BLOCK FOR VIEW BINDING ---
    buildFeatures {
        viewBinding = true
    }
    // ------------------------------------
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2") // Use the latest version

    // For collecting flows from the UI
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.2") // Use the latest version
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2" )// Use the latest version
    implementation("androidx.activity:activity-ktx:1.9.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- ADD THE FOLLOWING DEPENDENCIES ---

    // CameraX core libraries (using a variable for the version is good practice)
    val cameraxVersion = "1.3.1" // Using a slightly newer stable version
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // NanoHTTPD Lightweight Web Server
    implementation("org.nanohttpd:nanohttpd:2.3.1")

    // ------------------------------------
}