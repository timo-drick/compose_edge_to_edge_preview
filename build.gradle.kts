import nl.littlerobots.vcu.plugin.resolver.VersionSelectors
import nl.littlerobots.vcu.plugin.versionCatalogUpdate

plugins {
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.versionCatalogUpdate) // Check for dependency updates
    alias(libs.plugins.autonomousappsDependencyAnalysis)

    kotlin("plugin.serialization") version Versions.kotlin
    id("app.cash.paparazzi") version Versions.paparazzi apply false
    id("com.android.lint") version "9.1.0" apply false
}

versionCatalogUpdate {
    sortByKey.set(true)
    versionSelector(VersionSelectors.PREFER_STABLE)
    keep {
        // Because of a bug of the version catalog it does not detect that the following variables are used
        versions.add("compileSdk")
        versions.add("targetSdk")
        versions.add("minSdk")
    }
}