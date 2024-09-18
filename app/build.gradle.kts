import com.android.build.api.component.analytics.AnalyticsEnabledApplicationVariant
import com.android.build.api.variant.impl.ApplicationVariantImpl
import java.nio.file.Paths

plugins {
  id("com.android.application")
  kotlin("android")
  id("com.google.devtools.ksp")
  id("kotlin-parcelize")
  id("dev.rikka.tools.materialthemebuilder")
}

val verName = "2.5.5"
val verCode = 2050500

android {
  compileSdk = 34
  ndkVersion = "25.0.8775105"

  defaultConfig {
    applicationId = "com.absinthe.anywhere_"
    namespace = "com.absinthe.anywhere_"
    minSdk = 23
    targetSdk = 33
    versionCode = verCode
    versionName = verName
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    manifestPlaceholders["appName"] = "Anywhere-"
    ndk {
      abiFilters += arrayOf("armeabi-v7a", "arm64-v8a")
    }
    resourceConfigurations += arrayOf("en", "zh-rCN", "zh-rTW", "zh-rHK")
  }

  ksp {
    arg("room.incremental", "true")
    arg("room.schemaLocation", "$projectDir/schemas")
  }

  buildFeatures {
    aidl = true
    buildConfig = true
    viewBinding = true
  }

  buildTypes {
    debug {
      applicationIdSuffix = ".debug"
      manifestPlaceholders["appName"] = "Anywhere-β"
      buildConfigField("boolean", "BETA", "true")
    }
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      buildConfigField("boolean", "BETA", "false")
    }
    all {
      buildConfigField(
        "String",
        "APP_CENTER_SECRET",
        "\"" + System.getenv("APP_CENTER_SECRET").orEmpty() + "\""
      )
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  androidComponents.onVariants { v ->
    val variant: ApplicationVariantImpl =
      if (v is ApplicationVariantImpl) v
      else (v as AnalyticsEnabledApplicationVariant).delegate as ApplicationVariantImpl
    variant.outputs.forEach {
      it.outputFileName.set("Anywhere-${verName}-${verCode}-${variant.name}.apk")
    }
  }

  dependenciesInfo.includeInApk = false

    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
        }
    }

  packaging {
    resources {
      excludes += "META-INF/**"
      excludes += "okhttp3/**"
      excludes += "kotlin/**"
      excludes += "org/**"
      excludes += "**.properties"
      excludes += "**.bin"
    }
  }
}

materialThemeBuilder {
  themes {
    create("anywhere") {
      primaryColor = "#8BC34A"
      lightThemeFormat = "Theme.Material3.Light.%s"
      lightThemeParent = "Theme.Material3.Light.Rikka"
      darkThemeFormat = "Theme.Material3.Dark.%s"
      darkThemeParent = "Theme.Material3.Dark.Rikka"
    }
  }
  generatePalette = true
}

repositories {
  mavenCentral()
}

val optimizeReleaseRes: Task = task("optimizeReleaseRes").doLast {
  val aapt2 = File(
    androidComponents.sdkComponents.sdkDirectory.get().asFile,
    "build-tools/${project.android.buildToolsVersion}/aapt2"
  )
  val zip = Paths.get(
    buildDir.path,
    "intermediates",
    "optimized_processed_res",
    "release",
    "resources-release-optimize.ap_"
  )
  val optimized = File("${zip}.opt")
  val cmd = exec {
    commandLine(
      aapt2, "optimize",
      "--collapse-resource-names",
      "--resources-config-path", "aapt2-resources.cfg",
      "-o", optimized,
      zip
    )
    isIgnoreExitValue = false
  }
  if (cmd.exitValue == 0) {
    delete(zip)
    optimized.renameTo(zip.toFile())
  }
}

tasks.configureEach {
  if (name == "optimizeReleaseResources") {
    finalizedBy(optimizeReleaseRes)
  }
}

configurations.all {
  exclude(group = "androidx.appcompat", module = "appcompat")
  exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
  exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
}

dependencies {
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

  implementation(project(":color-picker"))
  implementation(files("libs/IceBox-SDK-1.0.6.aar"))

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

  implementation("com.github.zhaobozhen.libraries:me:1.1.4")
  implementation("com.github.zhaobozhen.libraries:utils:1.1.4")

  val appCenterSdkVersion = "5.0.3"
  implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
  implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")

  //Android X
  val roomVersion = "2.5.2"
  implementation("androidx.room:room-runtime:${roomVersion}")
  implementation("androidx.room:room-ktx:${roomVersion}")
  ksp("androidx.room:room-compiler:${roomVersion}")
  androidTestImplementation("androidx.room:room-testing:${roomVersion}")

  val lifecycleVersion = "2.6.2"
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:${lifecycleVersion}")
  implementation("androidx.lifecycle:lifecycle-common-java8:${lifecycleVersion}")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${lifecycleVersion}")

  implementation("androidx.browser:browser:1.6.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
  implementation("androidx.viewpager2:viewpager2:1.1.0-beta02")
  implementation("androidx.recyclerview:recyclerview:1.3.1")
  implementation("androidx.drawerlayout:drawerlayout:1.2.0")

  //KTX
  implementation("androidx.collection:collection-ktx:1.4.4")
  implementation("androidx.activity:activity-ktx:1.8.1")
  implementation("androidx.fragment:fragment-ktx:1.6.2")
  implementation("androidx.palette:palette-ktx:1.0.0")
  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.preference:preference-ktx:1.2.1")

  //Google
  implementation("com.google.android.material:material:1.9.0")

  //Function
  implementation("com.github.bumptech.glide:glide:4.16.0")
  ksp("com.github.bumptech.glide:compiler:4.16.0")

  implementation("com.google.code.gson:gson:2.9.0")
  implementation("com.google.zxing:core:3.5.2")
  implementation("com.blankj:utilcodex:1.31.1")
  implementation("com.tencent:mmkv-static:1.3.1")
  implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.11")
  implementation("com.github.heruoxin.Delegated-Scopes-Manager:client:master-SNAPSHOT")
  implementation("com.github.topjohnwu.libsu:core:5.2.1")
  implementation("com.github.thegrizzlylabs:sardine-android:0.8")
  implementation("com.jonathanfinerty.once:once:1.3.1")
  implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
  implementation("com.jakewharton.timber:timber:5.0.1")

  //UX
  implementation("com.drakeet.about:about:2.5.2")
  implementation("com.drakeet.multitype:multitype:4.3.0")
  implementation("com.drakeet.drawer:drawer:1.0.3")
  implementation("com.github.sephiroth74:android-target-tooltip:2.0.4")
  implementation("com.leinardi.android:speed-dial:3.3.0")
  implementation("me.zhanghai.android.fastscroll:library:1.3.0")

  val shizukuVersion = "12.2.0"
  // required by Shizuku and Sui
  implementation("dev.rikka.shizuku:api:$shizukuVersion")
  // required by Shizuku
  implementation("dev.rikka.shizuku:provider:$shizukuVersion")

  implementation("dev.rikka.rikkax.appcompat:appcompat:1.6.1")
  implementation("dev.rikka.rikkax.core:core:1.4.1")
  implementation("dev.rikka.rikkax.material:material:2.7.0")
  implementation("dev.rikka.rikkax.recyclerview:recyclerview-ktx:1.3.2")
  implementation("dev.rikka.rikkax.widget:borderview:1.1.0")
  implementation("dev.rikka.rikkax.preference:simplemenu-preference:1.0.3")
  implementation("dev.rikka.rikkax.insets:insets:1.3.0")
  implementation("dev.rikka.rikkax.layoutinflater:layoutinflater:1.3.0")
  implementation("dev.rikka.rikkax.material:material-preference:2.0.0")

  //Network
  implementation("com.squareup.okhttp3:okhttp:4.11.0")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("com.squareup.okio:okio:3.5.0")

  //Rx
  implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
  implementation("io.reactivex.rxjava2:rxjava:2.2.21")
  implementation("org.reactivestreams:reactive-streams:1.0.4")

  //Debug
  testImplementation("junit:junit:4.13.2")
  debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
  androidTestImplementation("androidx.test:runner:1.5.2")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
