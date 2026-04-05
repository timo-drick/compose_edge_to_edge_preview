plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

configurations.all {
    resolutionStrategy {
        force(libs.androidx.espresso.core)
    }
}

android {
    namespace = "de.drick.compose.devicerecording"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.drick.compose.devicerecording"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":edge_to_edge_preview_lib"))
    implementation(project(":edge_to_edge_test_lib"))

    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.uiToolingPreview)

    implementation(libs.androidx.coreKtx)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.activityCompose)

    debugImplementation(libs.compose.uiTooling)
    debugImplementation(libs.androidx.composeUiTestManifest)

    //Testing
    //testImplementation("junit:junit:${Versions.junit}")

    androidTestImplementation(libs.androidx.uiautomator)
    androidTestImplementation(libs.androidx.composeUiTest)
    androidTestImplementation(libs.androidx.espresso.core)

    //androidTestImplementation("androidx.test.services:storage:1.6.0") // Used to store bitmaps in TestStorage
    //androidTestImplementation("androidx.test.services:test-services:1.6.0")
}