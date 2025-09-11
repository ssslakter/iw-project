// settings.gradle.kts

// This block tells Gradle WHERE to find PLUGINS
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// This block tells Gradle WHERE to find project DEPENDENCIES (libraries)
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AeroSkin"
include(":app")