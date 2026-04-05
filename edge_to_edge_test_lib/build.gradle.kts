import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.vanniktechMavenPublish)
}

val mavenGroupId = Versions.mavenGroupId
val mavenArtifactId = "edge-to-edge-test"

val mavenVersion = Versions.mavenLib

kotlin {
    android {
        namespace = "de.drick.compose.edgetoedgetestlib"
        compileSdk = Versions.compileSdk
        minSdk = 23
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)

        }
        androidMain.dependencies {
            implementation(project(":edge_to_edge_preview_check_lib"))

            implementation(libs.androidx.coreKtx)
            implementation(libs.androidx.uiautomator)
            implementation(libs.androidx.composeUiTest)

            //val composeBom = platform("androidx.compose:compose-bom:${Versions.composeBom}")
            //implementation(composeBom)
            //implementation("androidx.compose.material3.adaptive:adaptive")
        }
    }
}

// https://vanniktech.github.io/gradle-maven-publish-plugin/central/
mavenPublishing {
    configure(
        KotlinMultiplatform(
            sourcesJar = SourcesJar.Sources(),
            androidVariantsToPublish = listOf("release")
        )
    )
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(mavenGroupId, mavenArtifactId, mavenVersion)

    pom {
        name.set("Compose edge to edge test library")
        description.set(
            """
                    Collection of test functions for edge-to-edge designs.
                    Testing if window insets overlap with content.
                """.trimIndent()
        )
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
