//import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
//import com.vanniktech.maven.publish.SonatypeHost
import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.compose")
    id("maven-publish")
    id("signing")
    //id("com.gradleup.nmcp").version("0.0.4")
    //id("cl.franciscosolis.sonatype-central-upload") version "1.0.0"
    //id("com.vanniktech.maven.publish") version Versions.vanniktechPlugin
}

val mavenGroupId = Versions.mavenGroupId
val mavenArtifactId = "edge-to-edge-preview"

val mavenVersion = Versions.mavenLib

android {
    namespace = "de.drick.compose.edgetoedgepreviewlib"
    group = mavenGroupId
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

    //val composeBom = platform("androidx.compose:compose-bom:${Versions.composeBom}")
    //implementation(composeBom)
    // Currently there are problems when using bom.
    // MavenCentral do not validate the lib in this case

    implementation("androidx.compose.ui:ui:${Versions.composeVersion}")
    implementation("androidx.compose.foundation:foundation:${Versions.composeVersion}")
    implementation("androidx.compose.ui:ui-graphics:${Versions.composeVersion}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Versions.composeVersion}")
    implementation("androidx.compose.material:material-icons-extended:${Versions.composeVersion}")

    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.composeVersion}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Versions.composeVersion}")

    //Testing
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("androidx.compose.ui:ui-test-junit4:${Versions.composeVersion}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.extJunit}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.espresso}")
    //androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.composeVersion}")
}

// https://vanniktech.github.io/gradle-maven-publish-plugin/central/
/*
mavenPublishing {
    configure(
        AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = true,
            publishJavadocJar = true
        )
    )
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()

    coordinates(mavenGroupId, mavenArtifactId, mavenVersion)

    pom {
        name.set("Compose edge to edge preview")
        description.set("""
            Create previews for edge-to-edge designs (also known as WindowInsets) with Jetpack Compose in Android Studio.
        """.trimIndent())
        url.set("https://github.com/timo-drick/compose_libraries")
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
            url.set("https://github.com/timo-drick/compose_libraries")
            connection.set("scm:git:git://github.com/timo-drick/compose_libraries.git")
            developerConnection.set("scm:git:ssh://git@github.com/timo-drick/compose_libraries.git")
        }
    }
}
*/

/*nmcp {
    // nameOfYourPublication must point to an existing publication
    publishAllPublications {
        username = System.getenv("mavenCentralUsername") ?: System.getProperty("mavenCentralUsername")
        password = System.getenv("mavenCentralPassword") ?: System.getProperty("mavenCentralPassword")

        // publish manually from the portal
        publicationType = "USER_MANAGED"

    }
}*/

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = mavenGroupId
            artifactId = mavenArtifactId
            version = mavenVersion

            pom {
                name.set("Compose edge to edge preview")
                description.set("""
                    Create previews for edge-to-edge designs (also known as WindowInsets) with Jetpack Compose in Android Studio.
                """.trimIndent())
                url.set("https://github.com/timo-drick/compose_libraries")
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
