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
        // Mapbox Maven repository


        gradlePluginPortal()
    }
}
dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "ParcialProyectoSurtidor"
include(":app")
 