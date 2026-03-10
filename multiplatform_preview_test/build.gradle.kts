plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {

    jvm()

    android {
        namespace = "de.drick.compose.multiplatform_preview_test"
        compileSdk = Versions.compileSdk
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.compose.materialIconsExtended)
                // Add KMP dependencies here

                api("androidx.compose.ui:ui-test:${Versions.composeMultiplatform}")

                implementation(project(":edge_to_edge_preview_lib"))
                implementation(project(":edge_to_edge_preview_check_lib"))
            }
        }
        androidMain.dependencies {
            implementation(libs.compose.uiTooling)
        }
    }

}