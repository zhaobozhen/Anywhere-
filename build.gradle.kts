// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
  repositories {
    google()
    gradlePluginPortal()
    maven("https://jitpack.io")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.3.1")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.22")
    classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.7.20-1.0.7")
    classpath("com.github.iwhys:sdk-editor-plugin:1.1.7")
    classpath("com.github.LianjiaTech:gson-plugin:2.1.0")
    classpath("dev.rikka.tools.materialthemebuilder:gradle-plugin:1.3.3")
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
