// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath("com.github.iwhys:sdk-editor-plugin:1.1.7")
    }
}

allprojects {
    repositories {
        google()
        maven("https://jitpack.io")
        jcenter()
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}