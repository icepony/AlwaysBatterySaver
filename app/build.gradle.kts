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
        versionCode = 4
        versionName = "2.2"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            output.outputFileName.set(
                "AlwaysBatterySaver_v${output.versionName.get()}-${variant.buildType}.apk"
            )
        }
    }
}

dependencies {
    compileOnly(libs.xposed)
}