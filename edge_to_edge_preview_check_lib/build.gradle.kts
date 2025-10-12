@file:OptIn(ExperimentalComposeLibrary::class)

import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("com.autonomousapps.dependency-analysis")
    id("com.vanniktech.maven.publish") version Versions.vanniktechPlugin
}

val mavenGroupId = Versions.mavenGroupId
val mavenArtifactId = "edge-to-edge-preview-check"
val mavenVersion = Versions.mavenLib


kotlin {

    applyDefaultHierarchyTemplate()

    jvm()

    androidTarget("android")

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

    @OptIn(ExperimentalWasmDsl::class) wasmJs() { browser() }

    js { browser() }

    macosX64()
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.uiTest)
        }

        // Android targets
        androidMain.dependencies {
            implementation(compose.uiTooling)
            api("androidx.core:core:${Versions.coreKtx}")
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
    kotlin {
        jvmToolchain(17)
    }
}

// https://vanniktech.github.io/gradle-maven-publish-plugin/central/

mavenPublishing {
    configure(
        KotlinMultiplatform( //TODO
            sourcesJar = true,
            androidVariantsToPublish = listOf("release")
        )
    )
    publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
    //signAllPublications()

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
