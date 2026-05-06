plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    //alias(libs.plugins.androidLint)
    alias(libs.plugins.previewScreenshotTest)
}


// Target declarations - add or remove as needed below. These define
// which platforms this KMP module supports.
// See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
android {
    namespace = "de.drick.compose.preview_screenshot_test"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }
    defaultConfig {
        minSdk = 26
    }
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    implementation(project(":edge_to_edge_preview_lib"))
    implementation(project(":samples:multiplatform_preview_test"))

    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.toolingPreview)
    implementation(libs.androidx.material3)

    screenshotTestImplementation(libs.compose.screenshotValidation)
    screenshotTestImplementation(libs.androidx.ui.tooling)
}
