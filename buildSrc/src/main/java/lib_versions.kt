object Versions {
    private const val libVersion = "0.9.0"
    val mavenLib: String
        get() = if (System.getenv("PUBLISH_SNAPSHOT") == "true") {
            val buildNumber = System.getenv("SNAPSHOT_BUILD_NUMBER")
            if (buildNumber != null && buildNumber.isNotBlank()) {
                "$libVersion-$buildNumber-SNAPSHOT"
            } else {
                "$libVersion-SNAPSHOT"
            }
        } else {
            libVersion
        }
    const val mavenGroupId = "de.drick.compose"

    const val compileSdk = 36

    const val uiAutomator = "2.4.0-alpha06"
}
