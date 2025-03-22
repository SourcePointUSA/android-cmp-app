plugins {
    id("com.android.library")
    kotlin("android")
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
    compileSdk = 35
    namespace = "com.sourcepoint.cmplibrary"
    testOptions.unitTests.isIncludeAndroidResources = true
    defaultConfig {
        minSdk = 23
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
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "LOGGER_URL", "\"https://cdn.privacy-mgmt.com/wrapper/metrics/v1/custom-metrics\"")
            buildConfigField("String", "SDK_ENV", "\"PROD\"")
            buildConfigField("String", "VERSION_NAME", "\"$versionLib\"")
            buildConfigField("String", "ENV_QUERY_PARAM", "\"prod\"")
            consumerProguardFiles("cmp-consumer-proguard-rules.pro")
        }
    }

    sourceSets {
        val sharedRes = "${project.rootDir.path}/ui-test-util/jsonFiles"
        getByName("test").resources.srcDir(sharedRes)
        getByName("androidTest").resources.srcDir(sharedRes)
        getByName("main").resources.srcDir("${projectDir.path}/files")

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    namespace = "com.example.cmplibrary"
    testNamespace = "com.sourcepoint.cmplibrary"
    lint {
        abortOnError = false
    }
    tasks {
        compileOptions {
            kotlinOptions.jvmTarget = "11"
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
    implementation("com.sourcepoint:core:0.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // https://mvnrepository.com/artifact/com.android.tools/desugar_jdk_libs
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    testImplementation("io.mockk:mockk:1.13.16")
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
