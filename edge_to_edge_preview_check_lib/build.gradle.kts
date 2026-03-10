import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.autonomousappsDependencyAnalysis)
    alias(libs.plugins.vanniktechMavenPublish)
}

val mavenGroupId = Versions.mavenGroupId
val mavenArtifactId = "edge-to-edge-preview-check"
val mavenVersion = Versions.mavenLib


kotlin {

    applyDefaultHierarchyTemplate()

    jvm()

    android {
        namespace = "de.drick.compose.edgetoedgepreviewchecklib"
        compileSdk = Versions.compileSdk
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs() { browser() }

    js { browser() }

    macosX64()
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTest)
        }

        // Android targets
        androidMain.dependencies {
            api(libs.androidx.coreKtx)
            implementation(libs.compose.uiTooling)
        }

        // NonAndroid targets (JVM, JS, Native)
        val nonAndroidMain by creating {
            dependsOn(commonMain.get())
        }

        jvmMain { dependsOn(nonAndroidMain) }
        webMain { dependsOn(nonAndroidMain) }
        nativeMain { dependsOn(nonAndroidMain) }
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
        name.set("Compose edge to edge preview check")
        description.set("""
            Check Jetpack Compose previews in Android Studio for edge-to-edge designs for overlaping content with WindowInsets .
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
