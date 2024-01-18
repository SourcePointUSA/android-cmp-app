plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("io.github.dryrum.update-changelog")
    id("io.github.dryrum.replace-in-file")
    id("io.github.dryrum.git-utils")
    id("kotlinx-serialization")
}

apply(from = "${project.rootDir.path}/gradleutils/ktlint_utils.gradle")
apply(from = "${project.rootDir.path}/gradleutils/test_config.gradle")
apply(from = "${project.rootDir.path}/scripts/publish-mavencentral.gradle")

val versionLib = project.property("VERSION_NAME") as String

group = "com.sourcepoint.cmplibrary"
version = versionLib

android {
    compileSdkVersion(33)
    testOptions.unitTests.isIncludeAndroidResources = true
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(33)
        versionCode = 300
        versionName = versionLib
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {
            buildConfigField("String", "LOGGER_URL", "\"https://cdn.privacy-mgmt.com/wrapper/metrics/v1/custom-metrics\"")
            buildConfigField("String", "SDK_ENV", "\"PROD\"")
            buildConfigField("String", "VERSION_NAME", "\"$versionLib\"")
            buildConfigField("String", "ENV_QUERY_PARAM", "\"prod\"")
        }
        create("stage") {
            initWith(getByName("debug"))
            isMinifyEnabled = false
            buildConfigField("String", "LOGGER_URL", "\"https://cdn.privacy-mgmt.com/wrapper/metrics/v1/custom-metrics\"")
            buildConfigField("String", "SDK_ENV", "\"STAGE\"")
            buildConfigField("String", "VERSION_NAME", "\"$versionLib\"")
            buildConfigField("String", "ENV_QUERY_PARAM", "\"stage\"")
        }
        create("preprod") {
            initWith(getByName("debug"))
            isMinifyEnabled = false
            buildConfigField("String", "LOGGER_URL", "\"https://cdn.privacy-mgmt.com/wrapper/metrics/v1/custom-metrics\"")
            buildConfigField("String", "SDK_ENV", "\"PRE_PROD\"")
            buildConfigField("String", "VERSION_NAME", "\"$versionLib\"")
            buildConfigField("String", "ENV_QUERY_PARAM", "\"localProd\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "LOGGER_URL", "\"https://cdn.privacy-mgmt.com/wrapper/metrics/v1/custom-metrics\"")
            buildConfigField("String", "SDK_ENV", "\"PROD\"")
            buildConfigField("String", "VERSION_NAME", "\"$versionLib\"")
            buildConfigField("String", "ENV_QUERY_PARAM", "\"prod\"")
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
        isCoreLibraryDesugaringEnabled = true
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

    lintOptions {
        // https://stackoverflow.com/questions/44751469/kotlin-extension-functions-suddenly-require-api-level-24/44752239
        isAbortOnError = false
    }
}

dependencies {
    // kotlin
    implementation(Libs.kotlinxCoroutinesCore)
    // https://mvnrepository.com/artifact/com.android.tools/desugar_jdk_libs
    coreLibraryDesugaring( "com.android.tools:desugar_jdk_libs:1.1.5")

    //noinspection GradleDependency
    api(Libs.okHttpCmp)

    api("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.0")

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
        "${rootDir.path}/README.md",
        "${rootDir.path}/samples/web-message-demo/build.gradle",
        "${rootDir.path}/samples/native-message-demo/build.gradle"
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
        create("doc1") {
            path = "${rootDir.path}/samples/web-message-demo/build.gradle"
            find = "com.sourcepoint.cmplibrary:cmplibrary:(\\d)+\\.(\\d)+\\.(\\d)+"
            replaceWith = "com.sourcepoint.cmplibrary:cmplibrary:$versionName"
        }
        create("doc2") {
            path = "${rootDir.path}/samples/native-message-demo/build.gradle"
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