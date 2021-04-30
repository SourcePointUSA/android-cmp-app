plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("io.github.dryrum.update-changelog")
    id("io.github.dryrum.replace-in-file")
    id("io.github.dryrum.git-utils")
}

apply(from = "${project.rootDir.path}/gradleutils/ktlint_utils.gradle")
apply(from = "${project.rootDir.path}/gradleutils/test_config.gradle")
apply(from = "${project.rootDir.path}/scripts/publish-mavencentral.gradle")

val versionLib = project.property("VERSION_NAME") as String

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
            buildConfigField("String", "SDK_ENV", "\"STAGE\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "LOGGER_URL", "\"https://wrapper-api.sp-prod.net/metrics/v1/custom-metrics\"")
            buildConfigField("String", "SDK_ENV", "\"PROD\"")
        }
    }

    sourceSets {
        val sharedRes = "${project.rootDir.path}/ui-test-util/jsonFiles"
        getByName("test").resources.srcDir(sharedRes)
        getByName("androidTest").resources.srcDir(sharedRes)
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
    // kotlin
    implementation(Libs.kotlinxCoroutinesCore)
    implementation(Libs.kotlinReflect)

    // Unfortunately we depend on a outdated version of okhttp due to its support to older versions of Android
    //noinspection GradleDependency
    api(Libs.okHttp)

    testImplementation(Libs.mockk)
    testImplementation(Libs.mockwebserver)

}

tasks.register("versionTxt") {
    group = "release-utility"
    doLast {
        val version = project.property("VERSION_NAME") as String
        File(projectDir, "version.txt").writeText(version)
    }
}

addCommitPushConfig {
    fileList = listOf(
        "${rootDir.path}/CHANGELOG.md",
        "${rootDir.path}/README.md"
    )
}

replaceInFile {
    val versionName = project.property("VERSION_NAME") as String
    docs {
        create("doc") {
            path = "${rootDir.path}/README.md"
            find = "com.sourcepoint.cmplibrary:cmplibrary:(\\d)+\\.(\\d)+\\.(\\d)+"
            replaceWith = "com.sourcepoint.cmplibrary:cmplibrary:$versionName"
        }
    }
}

changeLogConfig {
    val versionName = project.property("VERSION_NAME") as String
    changeLogPath = rootDir.path + "/CHANGELOG.md"
    content = file(  "${rootDir.path}/${project.name}/release_note.txt").readText()
    version = versionName
}