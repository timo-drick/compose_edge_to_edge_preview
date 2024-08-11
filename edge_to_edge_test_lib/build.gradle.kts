plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.compose")
    id("maven-publish")
    id("signing")
}

val mavenGroupId = Versions.mavenGroupId
val mavenArtifactId = "edge-to-edge-preview"

val mavenVersion = Versions.mavenLib


android {
    namespace = "de.telekom.edgetoedgetestlib"
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
    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:${Versions.coreKtx}")

    lintChecks("com.slack.lint.compose:compose-lint-checks:${Versions.composeLintChecks}") // https://slackhq.github.io/compose-lints

    implementation("androidx.compose.material3.adaptive:adaptive:${Versions.composeAdaptive}")
    implementation("junit:junit:${Versions.junit}")
    implementation("androidx.compose.ui:ui-test-junit4:${Versions.composeVersion}")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = mavenGroupId
            artifactId = mavenArtifactId
            version = mavenVersion

            pom {
                name.set("Compose edge to edge test library")
                description.set("""
                    Collection of test functions for edge-to-edge designs.
                    Testing if window insets overlap with content.
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

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "local"
            setUrl("$rootDir/repo")
        }
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USER")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

signing {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}
