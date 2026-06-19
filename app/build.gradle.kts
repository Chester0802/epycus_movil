plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "es.epycus.app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "es.epycus.app"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "API_BASE_URL", "\"https://app.epycus.es/\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"621141066064-vtm8tf4bv7bl3oubq3eesaha0205e6gr.apps.googleusercontent.com\"")
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
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.nav.fragment)
    implementation(libs.nav.ui)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.swiperefreshlayout)
    implementation(libs.security.crypto)
    implementation(libs.play.services.auth)
    implementation(libs.glide)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}