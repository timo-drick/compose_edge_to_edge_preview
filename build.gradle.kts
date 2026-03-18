import nl.littlerobots.vcu.plugin.resolver.VersionSelectors
import nl.littlerobots.vcu.plugin.versionCatalogUpdate

plugins {
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.versionCatalogUpdate) // Check for dependency updates
    alias(libs.plugins.autonomousappsDependencyAnalysis)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.paparazzi) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.previewScreenshotTest) apply false
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