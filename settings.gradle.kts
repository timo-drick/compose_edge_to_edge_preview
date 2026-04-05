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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":edge_to_edge_preview_lib")
include(":edge_to_edge_test_lib")
include(":edge_to_edge_preview_check_lib")
include(":edge_to_edge_preview_recorded_lib")

include(":edge_to_edge_preview")
include(":device_recording")
include(":multiplatform_preview_test")
include(":preview_screenshot_test")
