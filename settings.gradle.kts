rootProject.name = "Compose edge to edge library"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":edge_to_edge_preview_lib")
include(":edge_to_edge_test_lib")
include(":edge_to_edge_preview_check_lib")

include(":samples:edge_to_edge_preview")
include(":device_recording")
include(":samples:multiplatform_preview_test")
include(":samples:preview_screenshot_test")
