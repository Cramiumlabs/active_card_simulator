pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if(requested.id.id == "dagger.hilt.android.plugin") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("file://${rootProject.projectDir.parent}/activecard") }
        maven { url = uri("file://${rootProject.projectDir.parent}/repo") }
    }
}

rootProject.name = "Example"
include(":app")
include(":sdk")
include(":activecard")

// Specify the correct relative path to sdk module
project(":sdk").projectDir = file("../sdk")
project(":activecard").projectDir = file ("../activecard")
