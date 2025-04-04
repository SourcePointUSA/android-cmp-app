plugins {
    id("com.android.application")
    kotlin("android")
    id("io.github.dryrum.update-changelog")
    id("io.github.dryrum.replace-in-file")
    id("io.github.dryrum.git-utils")
    id("io.github.dryrum.bump-version-code")
    id("com.squareup.sqldelight")
    id("kotlin-android")
}

apply(from = "${project.rootDir.path}/gradleutils/ktlint_utils.gradle")
apply(from = "${project.rootDir.path}/gradleutils/test_config.gradle")

val versionCodeMeta = (project.property("VERSION_CODE") as String).toInt()

@Suppress("UnstableApiUsage")
android {
    compileSdk = 35
    namespace = "com.sourcepointmeta.metaapp"
    defaultConfig {
        applicationId = "com.sourcepointmeta.metaapp"
        minSdk = 23
        targetSdk = 35
        versionCode = versionCodeMeta
        versionName = "${rootProject.project("cmplibrary").version}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/license.txt")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/NOTICE.txt")
            excludes.add("META-INF/notice.txt")
            excludes.add("META-INF/ASL2.0")
            excludes.add("META-INF/INDEX.LIST")
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            buildConfigField("String", "URL_PROPERTY_FILE", "\"https://raw.githubusercontent.com/SourcePointUSA/android-cmp-app/master/cmplibrary/gradle.properties\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "URL_PROPERTY_FILE", "\"https://raw.githubusercontent.com/SourcePointUSA/android-cmp-app/master/cmplibrary/gradle.properties\"")
        }
    }

    sourceSets {
        val sharedRes = "${project.rootDir.path}/ui-test-util/jsonFiles"
        getByName("test").resources.srcDir(sharedRes)
        getByName("androidTest").resources.srcDir(sharedRes)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions.jvmTarget = "11"
        }
    }

    testOptions {
        // JSONObject return null during unit tests
        // https://stackoverflow.com/questions/49667567/android-org-json-jsonobject-returns-null-in-unit-tests/57592457#57592457
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    lint {
        // https://stackoverflow.com/questions/44751469/kotlin-extension-functions-suddenly-require-api-level-24/44752239
        abortOnError = false
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

sqldelight {
    database("MetaAppDB") {
        packageName = "com.sourcepointmeta.metaapp.db"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

    // kotlin
    implementation(Libs.kotlinxCoroutinesCore)
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")

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
    implementation("androidx.fragment:fragment-ktx:1.8.4")
    implementation("androidx.core:core-ktx:1.13.1") // ext drawable
    implementation("io.github.g00fy2:versioncompare:1.4.1")

    // TV
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.leanback:leanback:1.0.0")

    // Koin
    implementation(Libs.koinAndroid)
    implementation(Libs.koinViewModel)

    // SQLDelight
    implementation("com.squareup.sqldelight:android-driver:1.5.4")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:1.5.4")

    // tv
    implementation(Libs.leanback)
    implementation(Libs.leanback_pref)

    // unit-test
    testImplementation("io.mockk:mockk:1.13.13")
    androidTestImplementation("io.insert-koin:koin-test:4.0.0")
    androidTestImplementation("io.mockk:mockk-android:1.13.13")
}

versionCodePropPath {
    path = "gradle.properties"
}

addCommitPushConfig {
    fileList = listOf(
        "${rootDir.path}/samples/metaapp/gradle.properties"
    )
}
