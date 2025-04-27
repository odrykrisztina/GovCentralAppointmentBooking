plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.govcentralappointmentbooking"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.govcentralappointmentbooking"
        minSdk = 23
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Rename APK after build
afterEvaluate {
    tasks.named("packageDebug") {
        doLast {
            val outputDir = layout.buildDirectory.get().dir("outputs/apk/debug").asFile
            outputDir.listFiles()?.forEach { file ->
                if (file.name.endsWith(".apk")) {
                    val newName = "szkh-idopont-debug-v1.0.apk"
                    println("Renaming APK: ${file.name} -> $newName")
                    file.renameTo(file.resolveSibling(newName))
                }
            }
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //noinspection UseTomlInstead
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
}
