import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleService)
    alias(libs.plugins.firebaseCrashlytics)
    id("co.touchlab.skie") version "0.10.6"
}

// local.properties から API キーを読み込み、commonMain 向けに BuildSecrets.kt を生成する
val generateBuildSecrets by tasks.registering {
    val localPropertiesFile = rootProject.file("local.properties")
    val outputDir = layout.buildDirectory.dir("generated/secrets")

    inputs.file(localPropertiesFile).optional()
    outputs.dir(outputDir)

    doLast {
        val props = Properties()
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { props.load(it) }
        }
        val unsplashKey = props.getProperty("UNSPLASH_ACCESS_KEY", "")

        val outputFile = outputDir.get().asFile.resolve("BuildSecrets.kt")
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            |package dev.seabat.ramennote.config
            |
            |// このファイルは Gradle によって自動生成されます。直接編集しないでください。
            |object BuildSecrets {
            |    const val UNSPLASH_ACCESS_KEY = "$unsplashKey"
            |}
            """.trimMargin()
        )
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain {
            kotlin.srcDir(tasks.named("generateBuildSecrets").map { layout.buildDirectory.dir("generated/secrets") })
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)

            implementation(libs.koin.android)

            implementation(libs.ktor.client.okhttp)

            implementation(libs.accompanist.permissions)

            // Firebase
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.ai)
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.appcheck)
            implementation(libs.firebase.appcheck.debug)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.test)

            implementation(libs.material.icons.core)

            // Ktor client for image fetching
            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            
            // Coil for image loading
            implementation(libs.coil)
            implementation(libs.coil.compose)

            // AI/ML dependencies
            implementation(libs.gemini.ai)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "dev.seabat.ramennote"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.seabat.ramennote"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 19
        versionName = "1.1.1"
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


dependencies {
    debugImplementation(compose.uiTooling)

    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

// Ensure KSP runs after Compose resource/code generation tasks on Android and iOS
afterEvaluate {
    val kspAndroidTasks = listOf(
        "kspDebugKotlinAndroid",
        "kspReleaseKotlinAndroid"
    )
    val kspIosTasks = listOf(
        "kspKotlinIosSimulatorArm64",
        "kspKotlinIosArm64"
    )
    val composeGenTasks = listOf(
        "generateResourceAccessorsForAndroidDebug",
        "generateResourceAccessorsForAndroidRelease",
        "generateResourceAccessorsForAndroidMain",
        "generateActualResourceCollectorsForAndroidMain",
        "generateComposeResClass",
        "generateResourceAccessorsForCommonMain",
        "generateExpectResourceCollectorsForCommonMain",
        "generateResourceAccessorsForIosArm64Main",
        "generateResourceAccessorsForIosSimulatorArm64Main",
        "generateResourceAccessorsForIosMain",
        "generateResourceAccessorsForAppleMain",
        "generateResourceAccessorsForNativeMain",
        "generateActualResourceCollectorsForIosSimulatorArm64Main",
        "generateActualResourceCollectorsForIosArm64Main"
    )

    (kspAndroidTasks + kspIosTasks).forEach { kspTaskName ->
        tasks.matching { it.name == kspTaskName }.configureEach {
            // Declare explicit dependencies to avoid implicit dependency validation errors
            dependsOn(composeGenTasks)
            dependsOn("generateBuildSecrets")
        }
    }
}

