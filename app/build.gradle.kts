plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.parcialproyectosurtidor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.parcialproyectosurtidor"
        minSdk = 21
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.mapbox.maps:android:11.11.0")

    // Añade esta dependencia para Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.1.0")



    // Puedes comentar estas dependencias de Mapbox Location si usas Google Location
    // implementation("com.mapbox.android:location:2.0.0")
    // implementation("com.mapbox.android:location-component:2.0.0")
    // implementation("com.mapbox.mapboxsdk:mapbox-android-core:5.0.2")
    // implementation("com.mapbox.mapboxsdk:mapbox-android-plugin-locationcomponent-v10:2.0.0")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Dependencias existentes... Para la barra
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation("com.google.android.material:material:1.9.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}