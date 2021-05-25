plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("io.github.dryrum.update-changelog")
    id("io.github.dryrum.replace-in-file")
    id("io.github.dryrum.git-utils")
}

apply(from = "${project.rootDir.path}/gradleutils/ktlint_utils.gradle")
apply(from = "${project.rootDir.path}/gradleutils/test_config.gradle")

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "com.sourcepointmeta.metaapp"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 2
        versionName = "${rootProject.project("cmplibrary").version}"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
//        getByName("debug") { }
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    sourceSets {
        val sharedRes = "${project.rootDir.path}/ui-test-util/jsonFiles"
        getByName("test").resources.srcDir(sharedRes)
        getByName("androidTest").resources.srcDir(sharedRes)
//        getByName("main").resources.srcDir("${projectDir.path}/files")

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

    lintOptions {
        // https://stackoverflow.com/questions/44751469/kotlin-extension-functions-suddenly-require-api-level-24/44752239
        isAbortOnError = false
    }
}

dependencies {
    // kotlin
    implementation(Libs.kotlinxCoroutinesCore)
    implementation(Libs.kotlinReflect)

    implementation(project(":cmplibrary"))

    api(Libs.okHttpLatest)

    // UI
    implementation(Libs.androidxAppcompat)
    implementation(Libs.androidxCore)
    implementation(Libs.material)
    implementation(Libs.constraintLayout)
    implementation(Libs.vectorDrawable)
    implementation(Libs.androidxLifLivedata)
    implementation(Libs.androidxLifViewModel)

    // Koin
    implementation(Libs.koinCore)
    implementation(Libs.koinCoreExt)
    implementation(Libs.koinAndroid)

    // unit-test
    testImplementation(Libs.mockk)
    testImplementation(Libs.mockwebserver)

    // integration-test
    androidTestImplementation(Libs.koinTest)

}