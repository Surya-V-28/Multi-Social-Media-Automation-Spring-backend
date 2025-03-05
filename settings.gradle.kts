plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "learningwebflux"

include(
    "authentication",
    "platformconnection",
    "post",
    "notification",
    "common",
    "medialibrary",
    "postanalytics"
)
