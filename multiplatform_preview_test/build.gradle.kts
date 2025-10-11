import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application") version Versions.androidPlugin
}

kotlin {

    jvm("desktop")

    androidTarget("android")

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material3)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.uiTooling)
                // Add KMP dependencies here

                implementation(project(":edge_to_edge_preview_lib"))
            }
        }
    }

}

android {
    namespace = "de.drick.compose.multiplatform_preview_test"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = 21
        targetSdk = Versions.compileSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
    }
}