plugins {
    id("com.android.application") version Versions.androidPlugin apply false
    id("com.android.library") version Versions.androidPlugin apply false
    kotlin("android") version Versions.kotlin apply false
    kotlin("plugin.compose") version Versions.kotlin apply false
    kotlin("plugin.serialization") version Versions.kotlin
    id("com.android.compose.screenshot") version Versions.composeScreenshot apply false

    id("com.github.ben-manes.versions") version Versions.benManesPlugin
}

fun isNonStable(version: String): Boolean {
    val unStableKeyword = listOf("alpha", "beta", "rc", "cr", "m", "preview", "dev").any { version.contains(it, ignoreCase = true) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = unStableKeyword.not() || regex.matches(version)
    return isStable.not()
}

tasks.named("dependencyUpdates", com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class.java).configure {
    rejectVersionIf {
        //isNonStable(candidate.version)
        (isNonStable(candidate.version) && isNonStable(currentVersion).not())
    }
}
