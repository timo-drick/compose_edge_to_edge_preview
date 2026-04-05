import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SourcesJar
import nl.littlerobots.vcu.plugin.resolver.VersionSelectors
import nl.littlerobots.vcu.plugin.versionCatalogUpdate
import org.gradle.kotlin.dsl.configure

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
    alias(libs.plugins.vanniktechMavenPublish) apply false
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

subprojects {
    pluginManager.withPlugin("com.vanniktech.maven.publish") {
        // https://vanniktech.github.io/gradle-maven-publish-plugin/central/
        extensions.configure<MavenPublishBaseExtension> {
            configure(
                KotlinMultiplatform(
                    sourcesJar = SourcesJar.Sources(),
                    androidVariantsToPublish = listOf("release")
                )
            )
            publishToMavenCentral(automaticRelease = true)
            signAllPublications()

            pom {
                name.set("Compose edge to edge preview")
                description.set("""
            Create previews for edge-to-edge designs (also known as WindowInsets) with Jetpack Compose in Android Studio.
        """.trimIndent())
                url.set("https://github.com/timo-drick/compose_edge_to_edge_preview")
                licenses {
                    license {
                        name = "The Unlicense"
                        url = "https://unlicense.org/"
                    }
                }
                developers {
                    developer {
                        id.set("timo-drick")
                        name.set("Timo Drick")
                        url.set("https://github.com/timo-drick")
                    }
                }
                scm {
                    url.set("https://github.com/timo-drick/compose_edge_to_edge_preview")
                    connection.set("scm:git:git://github.com/timo-drick/compose_edge_to_edge_preview.git")
                    developerConnection.set("scm:git:ssh://git@github.com/timo-drick/compose_edge_to_edge_preview.git")
                }
            }
        }
    }
}