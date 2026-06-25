plugins {
    alias(libs.plugins.android.application)
}

// ============================================================
// Play Store Publishing Configuration
// ============================================================
// Version strategy:
//   versionCode: integer, increment by 1 for every Play Store release
//     - Internal/test releases: 1-99
//     - Production v1.x: 100+
//     - Production v2.x: 200+, etc.
//   versionName: Semantic versioning "major.minor.patch"
//     - Major: breaking changes or big feature releases
//     - Minor: new features, backward compatible
//     - Patch: bug fixes, small improvements
//
// Current: versionCode=2, versionName="1.1" (first public release candidate)
//
// Signing: the release signing config reads from these sources (in priority):
//   1. keystore.properties file at project root (see keystore.properties.example)
//   2. Environment variables: EPYCUS_STORE_FILE, EPYCUS_STORE_PASSWORD,
//      EPYCUS_KEY_ALIAS, EPYCUS_KEY_PASSWORD
// ============================================================

val secretsFile = rootProject.file("secrets.properties")
val googleClientId = if (secretsFile.exists()) {
    secretsFile.readLines()
        .firstOrNull { it.startsWith("googleClientId=") }
        ?.substringAfter("=")
        ?.trim()
        ?: "621141066064-vtm8tf4bv7bl3oubq3eesaha0205e6gr.apps.googleusercontent.com"
} else {
    "621141066064-vtm8tf4bv7bl3oubq3eesaha0205e6gr.apps.googleusercontent.com"
}

fun getKeystoreProperty(key: String): String? {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) {
        return f.readLines()
            .firstOrNull { it.startsWith("$key=") }
            ?.substringAfter("=")
            ?.trim()
    }
    return null
}

android {
    signingConfigs {
        create("release") {
            val storeFileProp = getKeystoreProperty("storeFile")
            val storeFileEnv = System.getenv("EPYCUS_STORE_FILE")
            if (storeFileProp != null) {
                storeFile = file(storeFileProp)
                storePassword = getKeystoreProperty("storePassword")
                keyAlias = getKeystoreProperty("keyAlias")
                keyPassword = getKeystoreProperty("keyPassword")
            } else if (storeFileEnv != null) {
                storeFile = file(storeFileEnv)
                storePassword = System.getenv("EPYCUS_STORE_PASSWORD") ?: ""
                keyAlias = System.getenv("EPYCUS_KEY_ALIAS") ?: ""
                keyPassword = System.getenv("EPYCUS_KEY_PASSWORD") ?: ""
            }
        }
    }

    namespace = "es.epycus.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "es.epycus.app"
        minSdk = 28
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"

        buildConfigField("String", "API_BASE_URL", "\"https://app.epycus.es/\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${googleClientId}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"https://app.epycus.es/\"")
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
    implementation(libs.core.splashscreen)
    implementation(libs.viewpager2)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}