plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.androidsss"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidsss"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.firebase:firebase-analytics")

    // Firebase dependencies
    implementation(libs.firebase.auth)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.firebase.database) // Firebase Realtime Database
    implementation(libs.firebase.storage)

    implementation("com.google.firebase:firebase-auth:21.1.0") // Firebase Auth
    implementation("com.google.firebase:firebase-database:20.0.3") // Firebase Realtime Database
    implementation("com.google.firebase:firebase-storage:20.1.0") // Firebase Storage (if applicable)



    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Apply the Google services plugin
apply(plugin = "com.google.gms.google-services")
