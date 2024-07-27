buildscript {
    pluginManagement {
        repositories {
            gradlePluginPortal()
            maven("https://repo.sleeping.town") {
                content {
                    includeGroup("com.unascribed")
                }
            }
        }
    }
}
includeBuild("../")