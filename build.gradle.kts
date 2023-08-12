// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
  repositories {
    google()
    gradlePluginPortal()
    maven("https://jitpack.io")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:8.1.0")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.9.0-1.0.13")
    classpath("com.github.iwhys:sdk-editor-plugin:1.1.7")
    classpath("dev.rikka.tools.materialthemebuilder:gradle-plugin:1.4.0")
  }
}

allprojects {
  repositories {
    google()
    maven("https://jitpack.io")
  }
}

task<Delete>("clean") {
  delete(rootProject.buildDir)
}
