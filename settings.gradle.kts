pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven ("https://jitpack.io")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven ( "https://jitpack.io")
    }
}

rootProject.name = "wirup"
include(":app")
 