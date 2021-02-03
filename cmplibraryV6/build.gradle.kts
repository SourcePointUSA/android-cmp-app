plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

apply(from = "${project.rootDir.path}/gradleutils/ktlint_utils.gradle")
apply(from = "${project.rootDir.path}/gradleutils/test_config.gradle")

val versionLib = project.property("VERSION_NAME_V6") as String

group = "com.sourcepoint.cmplibrary"
version = versionLib

android {
    compileSdkVersion(28)
    testOptions.unitTests.isIncludeAndroidResources = true
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 300
        versionName = versionLib
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {
            buildConfigField("String", "LOGGER_URL", "\"https://wrapper-api.sp-prod.net/metrics/v1/custom-metrics\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "LOGGER_URL", "\"https://wrapper-api.sp-prod.net/metrics/v1/custom-metrics\"")
        }
    }

    sourceSets{
        val sharedRes = "${project.rootDir.path}/ui-test-util/jsonFiles"
        getByName("test").resources.srcDir(sharedRes)
        getByName("main").resources.srcDir("${projectDir.path}/files")

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    testOptions {
        // JSONObject return null during unit tests
        // https://stackoverflow.com/questions/49667567/android-org-json-jsonobject-returns-null-in-unit-tests/57592457#57592457
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to "*.jar", "dir" to "libs")))

    // kotlin
    implementation(Libs.kotlinxCoroutinesCore)
    implementation(Libs.kotlinReflect)

    // gson
    api(Libs.jacksonJr)

    // Unfortunately we depend on a outdated version of okhttp due to its support to older versions of Android
    //noinspection GradleDependency
    api(project(":cmplibrary"))

    testImplementation (Libs.mockk)
    testImplementation(Libs.mockwebserver)

}

//apply(from = "${rootDir}/scripts/publish-mavencentral.gradle")