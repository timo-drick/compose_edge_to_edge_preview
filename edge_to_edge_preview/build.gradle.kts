import org.gradle.internal.classpath.Instrumented.systemProperty

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.compose.screenshot")
    id("app.cash.paparazzi")
}

android {
    namespace = "de.drick.compose.edgetoedgepreview"
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "de.drick.compose.edgetoedgepreview"
        minSdk = 21
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

    lint {
        disable += "SlotReused" // Unfortunately this rule does produce false negatives. Not usable
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

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
    implementation(project(":edge_to_edge_preview_check_lib"))

    implementation("androidx.core:core-ktx:${Versions.coreKtx}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
    implementation("androidx.activity:activity-compose:${Versions.activityCompose}")
    val composeBom = platform("androidx.compose:compose-bom:${Versions.composeBom}")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3.adaptive:adaptive:${Versions.composeAdaptive}")
    implementation("androidx.compose.material:material-icons-extended")

    lintChecks("com.slack.lint.compose:compose-lint-checks:${Versions.composeLintChecks}")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}")

    testImplementation(project(":edge_to_edge_test_lib"))
    androidTestImplementation(project(":edge_to_edge_test_lib"))

    //Testing
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.robolectric:robolectric:${Versions.robolectric}")
    testImplementation(composeBom)
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("androidx.test:runner:${Versions.testRunner}")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:${Versions.uiAutomator}")
    //androidTestImplementation("androidx.test.services:storage:1.4.2") // Used to store bitmaps in TestStorage
    //androidTestImplementation("androidx.test.services:test-services:1.4.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest") // Needed for createComposeRule() injects the Activity
}