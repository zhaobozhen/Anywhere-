import com.android.build.api.component.analytics.AnalyticsEnabledApplicationVariant
import com.android.build.api.variant.impl.ApplicationVariantImpl
import java.nio.file.Paths

plugins {
  id("com.android.application")
  kotlin("android")
  id("com.google.devtools.ksp")
  id("com.ke.gson.plugin")
  id("kotlin-parcelize")
  id("sdk-editor")
  id("dev.rikka.tools.materialthemebuilder")
}

val verName = "2.4.1"
val verCode = 2040101

android {
  compileSdk = 32
  ndkVersion = "21.4.7075529"

  defaultConfig {
    applicationId = "com.absinthe.anywhere_"
    namespace = "com.absinthe.anywhere_"
    minSdk = 23
    targetSdk = 32
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
    viewBinding = true
    compose = true
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
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.2.0-beta03"
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

//    externalNativeBuild {
//        cmake {
//            path = file("CMakeLists.txt")
//        }
//    }

  packagingOptions {
    resources {
      excludes += "META-INF/**"
      excludes += "okhttp3/**"
      excludes += "kotlin/**"
      excludes += "org/**"
      excludes += "**.properties"
      excludes += "**.bin"
    }
  }

  sourceSets {
    named("main") {
      jniLibs.srcDir("libs")
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

val optimizeReleaseRes = task("optimizeReleaseRes").doLast {
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

tasks.whenTaskAdded {
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

  implementation(files("libs/color-picker.aar"))
  implementation(files("libs/IceBox-SDK-1.0.6.aar"))

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2")

  implementation("com.github.zhaobozhen.libraries:me:1.1.1")
  implementation("com.github.zhaobozhen.libraries:utils:1.1.1")

  val appCenterSdkVersion = "4.4.3"
  implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
  implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")

  //Android X
  val roomVersion = "2.4.2"
  implementation("androidx.room:room-runtime:${roomVersion}")
  implementation("androidx.room:room-ktx:${roomVersion}")
  ksp("androidx.room:room-compiler:${roomVersion}")
  androidTestImplementation("androidx.room:room-testing:${roomVersion}")

  val lifecycleVersion = "2.4.1"
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:${lifecycleVersion}")
  implementation("androidx.lifecycle:lifecycle-common-java8:${lifecycleVersion}")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${lifecycleVersion}")

  implementation("androidx.browser:browser:1.4.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
  implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
  implementation("androidx.recyclerview:recyclerview:1.2.1")
  implementation("androidx.drawerlayout:drawerlayout:1.1.1")
  implementation("androidx.glance:glance-appwidget:1.0.0-alpha03")

  //KTX
  implementation("androidx.collection:collection-ktx:1.2.0")
  implementation("androidx.activity:activity-ktx:1.4.0")
  implementation("androidx.fragment:fragment-ktx:1.4.1")
  implementation("androidx.palette:palette-ktx:1.0.0")
  implementation("androidx.core:core-ktx:1.8.0")
  implementation("androidx.preference:preference-ktx:1.2.0")

  //Google
  implementation("com.google.android.material:material:1.6.1")

  //Function
  implementation("com.github.bumptech.glide:glide:4.13.2")
  ksp("com.github.bumptech.glide:compiler:4.13.2")

  implementation("com.google.code.gson:gson:2.9.0")
  implementation("com.google.zxing:core:3.5.0")
  implementation("com.blankj:utilcodex:1.31.0")
  implementation("com.tencent:mmkv-static:1.2.13")
  implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.7")
  implementation("com.github.heruoxin.Delegated-Scopes-Manager:client:master-SNAPSHOT")
  implementation("com.github.topjohnwu.libsu:core:5.0.2")
  implementation("com.github.thegrizzlylabs:sardine-android:0.8")
  implementation("com.jonathanfinerty.once:once:1.3.1")
  implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")

  //UX
  implementation("com.drakeet.about:about:2.5.1")
  implementation("com.drakeet.multitype:multitype:4.3.0")
  implementation("com.drakeet.drawer:drawer:1.0.3")
  implementation("com.github.sephiroth74:android-target-tooltip:2.0.4")
  implementation("com.leinardi.android:speed-dial:3.3.0")
  implementation("me.zhanghai.android.fastscroll:library:1.1.8")

  val shizukuVersion = "12.1.0"
  // required by Shizuku and Sui
  implementation("dev.rikka.shizuku:api:$shizukuVersion")
  // required by Shizuku
  implementation("dev.rikka.shizuku:provider:$shizukuVersion")

  implementation("dev.rikka.rikkax.appcompat:appcompat:1.4.1")
  implementation("dev.rikka.rikkax.core:core:1.4.0")
  implementation("dev.rikka.rikkax.material:material:2.4.0")
  implementation("dev.rikka.rikkax.recyclerview:recyclerview-ktx:1.3.1")
  implementation("dev.rikka.rikkax.widget:borderview:1.1.0")
  implementation("dev.rikka.rikkax.preference:simplemenu-preference:1.0.3")
  implementation("dev.rikka.rikkax.insets:insets:1.2.0")
  implementation("dev.rikka.rikkax.layoutinflater:layoutinflater:1.2.0")
  implementation("dev.rikka.rikkax.material:material-preference:1.0.0")

  //Network
  implementation("com.squareup.okhttp3:okhttp:4.9.3")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("com.squareup.okio:okio:3.1.0")

  //Rx
  implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
  implementation("io.reactivex.rxjava2:rxjava:2.2.21")
  implementation("org.reactivestreams:reactive-streams:1.0.4")

  //Debug
  testImplementation("junit:junit:4.13.2")
  debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")
  androidTestImplementation("androidx.test:runner:1.4.0")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
