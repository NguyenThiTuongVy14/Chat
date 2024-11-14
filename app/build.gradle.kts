plugins {



    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.chat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chat"
        minSdk = 27
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures{
        viewBinding=true;
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.intuit.ssp:ssp-android:1.1.1")
    implementation ("com.makeramen:roundedimageview:2.3.0")



    implementation ("com.google.firebase:firebase-messaging:24.0.3")

    implementation ("com.google.firebase:firebase-firestore:25.1.1")
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

//Xác thực otp
    implementation ("com.google.firebase:firebase-auth:21.0.1")



    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.google.firebase:firebase-database:20.2.0")
    implementation ("com.google.firebase:firebase-auth:22.1.1")  
    implementation ("com.google.firebase:firebase-core:21.1.1")
}