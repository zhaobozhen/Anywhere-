// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
  repositories {
    google()
    gradlePluginPortal()
    maven("https://jitpack.io")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.4.2")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
    classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.8.22-1.0.11")
    classpath("com.github.iwhys:sdk-editor-plugin:1.1.7")
    classpath("com.github.LianjiaTech:gson-plugin:2.1.0")
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
