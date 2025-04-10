plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "io.github.icepony.alwaysbatterysaver"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.icepony.alwaysbatterysaver"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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

dependencies {
    compileOnly(libs.xposed)
}