plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ariofrio.justhelp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ariofrio.justhelp"
        minSdk = 28
        targetSdk = 34
        versionCode = 8
        versionName = "1.1"

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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.car.ui.lib)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.github.bumptech.glide:glide:4.12.0")// o la última versión
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0") // o la última versión
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")  // Verifica que esta línea esté presente.


    implementation ("org.osmdroid:osmdroid-android:6.1.14")
}