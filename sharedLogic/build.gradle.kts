import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
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
    android {
        namespace = "dev.seabat.ramennote.sharedlogic"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    )

    sourceSets {
        commonMain {
            kotlin.srcDir(tasks.named("generateBuildSecrets").map { layout.buildDirectory.dir("generated/secrets") })
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)

            // Firebase
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.ai)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.test)

            // Ktor client for image fetching
            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Coil for image loading
            implementation(libs.coil)

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

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

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
        "generateBuildSecrets"
    )

    (kspAndroidTasks + kspIosTasks).forEach { kspTaskName ->
        tasks.matching { it.name == kspTaskName }.configureEach {
            dependsOn(composeGenTasks)
        }
    }
}
