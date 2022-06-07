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
    classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.6.21-1.0.5")
    classpath("com.github.iwhys:sdk-editor-plugin:1.1.7")
    classpath("com.github.LianjiaTech:gson-plugin:2.1.0")
    classpath("dev.rikka.tools.materialthemebuilder:gradle-plugin:1.3.2")
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
