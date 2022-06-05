// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
  repositories {
    google()
    gradlePluginPortal()
    maven("https://jitpack.io")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.2.1")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")

    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle files
    classpath("com.github.iwhys:sdk-editor-plugin:1.1.7")
    classpath("com.github.LianjiaTech:gson-plugin:2.1.0")
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
