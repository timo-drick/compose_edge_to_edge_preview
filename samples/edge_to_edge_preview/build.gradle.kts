import org.gradle.internal.classpath.Instrumented.systemProperty

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    //alias(libs.plugins.paparazzi) // does not support agp 9+ yet
}

android {
    namespace = "de.drick.compose.edgetoedgepreview"
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "de.drick.compose.edgetoedgepreview"
        minSdk = 23
        targetSdk = Versions.compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        resValues = true
    }

    lint {
        disable += "SlotReused" // Unfortunately this rule does produce false negatives. Not usable
    }

    testOptions {
        animationsDisabled = false

        unitTests {
            isIncludeAndroidResources = true
        }

        // https://developer.android.com/studio/test/gradle-managed-devices
        managedDevices {
            localDevices {
                create("pixel5api34") {
                    device = "Pixel 5"
                    apiLevel = 34
                    systemImageSource = "aosp"
                }
            }
        }
    }

    systemProperty("robolectric.screenshot.hwrdr.native","true")

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":edge_to_edge_preview_lib"))
    implementation(project(":edge_to_edge_test_lib"))
    implementation(project(":edge_to_edge_preview_check_lib"))

    implementation(libs.androidx.coreKtx)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.activityCompose)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.toolingPreview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.material.iconsExtended)

    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.kotlinx.serialization.json)

    //Unit jvm tests
    testImplementation(project(":edge_to_edge_test_lib"))

    //testImplementation("junit:junit:${Versions.junit}")
    testImplementation(libs.kotlin.test)
    testImplementation(libs.robolectric)
    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.androidx.composeUiTest)
    testImplementation(libs.androidx.runner)

    api(libs.androidx.composeUiTest)

    // Android device tests
    androidTestImplementation(project(":edge_to_edge_test_lib"))

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:${Versions.uiAutomator}")
    //androidTestImplementation("androidx.test.services:storage:1.4.2") // Used to store bitmaps in TestStorage
    //androidTestImplementation("androidx.test.services:test-services:1.4.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest") // Needed for createComposeRule() injects the Activity
}