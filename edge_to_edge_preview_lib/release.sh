#export OSSRH_USER=xxx
#export OSSRH_PASSWORD=xxx
printenv | grep OSSRH
#../gradlew :edge_to_edge_preview_lib:publishReleasePublicationToOSSRHRepository

# Using the com.vanniktech.maven.publish plugin
#./gradlew publishAndReleaseToMavenCentral --no-configuration-cache