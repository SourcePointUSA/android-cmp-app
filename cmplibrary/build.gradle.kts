import com.vanniktech.maven.publish.SonatypeHost

var versionLib = project.property("VERSION_NAME") as String

group = "com.sourcepoint.cmplibrary"
version = versionLib

plugins {
    id("com.android.library")
    kotlin("android")
    id("io.github.dryrum.update-changelog")
    id("io.github.dryrum.replace-in-file")
    id("io.github.dryrum.git-utils")
    id("kotlinx-serialization")
    id("com.vanniktech.maven.publish") version "0.32.0"
}

android {
    compileSdk = 35
    namespace = "com.sourcepoint.cmplibrary"
    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug")
        getByName("release") {
            isMinifyEnabled = false
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
}

dependencies {
    implementation("com.sourcepoint:core:0.1.14")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    testImplementation("org.json:json:20250107")
    testImplementation("junit:junit:4.13.2")
}

tasks.register("versionTxt") {
    group = "release-utility"
    doLast {
        File(projectDir, "version.txt").writeText(versionLib)
    }
}

addCommitPushConfig {
    fileList = listOf(
        "${rootDir.path}/CHANGELOG.md",
        "${rootDir.path}/README.md"
    )
}

replaceInFile {
    docs {
        create("doc") {
            path = "${rootDir.path}/README.md"
            find = "com.sourcepoint.cmplibrary:cmplibrary:(\\d)+\\.(\\d)+\\.(\\d)+"
            replaceWith = "com.sourcepoint.cmplibrary:cmplibrary:$versionLib"
        }
    }
}

changeLogConfig {
    changeLogPath = rootDir.path + "/CHANGELOG.md"
    content = file(  "${rootDir.path}/${project.name}/release_note.txt").readText()
    this.version = versionLib
}

apply(from = "${project.rootDir.path}/gradleutils/ktlint_utils.gradle")

mavenPublishing {
    coordinates(group.toString(), "cmplibrary", versionLib)
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    pom {
        name = "Sourcepoint Android CMP"
        description = "The internal Network & Data layers used by our mobile SDKs"
        url = "https://github.com/SourcePointUSA/android-cmp-app"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "andresilveirah"
                name = "Andre Herculano"
                email = "andresilveirah@gmail.com"
            }
        }
        scm {
            connection = "scm:git:github.com/SourcePointUSA/android-cmp-app.git"
            developerConnection = "scm:git:ssh://github.com/SourcePointUSA/android-cmp-app.git"
            url = "https://github.com/SourcePointUSA/android-cmp-app/tree/main"
        }
    }
}
