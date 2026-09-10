pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "channel-android-v2"

include(":app")
include(":core:contracts")
include(":core:network")
include(":core:designsystem")
include(":core:upload")
include(":core:media")
include(":core:audio")
include(":feature:auth")
include(":feature:onboarding")
include(":feature:profile")
