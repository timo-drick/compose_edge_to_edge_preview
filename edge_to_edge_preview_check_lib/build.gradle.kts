plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.compose")
    id("com.autonomousapps.dependency-analysis")
    id("com.vanniktech.maven.publish") version Versions.vanniktechPlugin
}

val mavenGroupId = Versions.mavenGroupId
val mavenArtifactId = "edge-to-edge-preview-check"

val mavenVersion = Versions.mavenLib

android {
    namespace = "de.drick.compose.edgetoedgepreviewchecklib"
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core:${Versions.coreKtx}")

    lintChecks("com.slack.lint.compose:compose-lint-checks:${Versions.composeLintChecks}") // https://slackhq.github.io/compose-lints

    api("androidx.compose.runtime:runtime:${Versions.composeVersion}")

    implementation("androidx.compose.ui:ui:${Versions.composeVersion}")
    implementation("androidx.compose.foundation:foundation:${Versions.composeVersion}")
    implementation("androidx.compose.material3.adaptive:adaptive:${Versions.composeAdaptive}")
    implementation("androidx.compose.ui:ui-test:${Versions.composeVersion}")
}