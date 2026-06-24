plugins {
    alias(libs.plugins.android.application)
}

// Para publicar en Play Store, crea un archivo keystore.properties en la raíz del proyecto:
// storeFile=ruta/al/keystore.jks
// storePassword=tu_store_password
// keyAlias=tu_key_alias
// keyPassword=tu_key_password
// Luego, en el build la firma se configura automáticamente.
// Alternativa: definir las variables de entorno EPYCUS_STORE_FILE, EPYCUS_STORE_PASSWORD,
// EPYCUS_KEY_ALIAS, EPYCUS_KEY_PASSWORD.

android {
    signingConfigs {
        create("release") {
            val storeFileEnv = System.getenv("EPYCUS_STORE_FILE")
            if (storeFileEnv != null) {
                storeFile = file(storeFileEnv)
                storePassword = System.getenv("EPYCUS_STORE_PASSWORD") ?: ""
                keyAlias = System.getenv("EPYCUS_KEY_ALIAS") ?: ""
                keyPassword = System.getenv("EPYCUS_KEY_PASSWORD") ?: ""
            }
        }
    }

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
        debug {
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:5053/\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_BASE_URL", "\"https://app.epycus.es/\"")
            signingConfig = signingConfigs.getByName("release")
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