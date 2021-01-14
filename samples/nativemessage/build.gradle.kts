import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
}

android {

    compileSdkVersion (30)
    buildToolsVersion = "30.0.2"

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdkVersion (16)
        targetSdkVersion (30)
        versionCode = 1
        versionName = "1.0"

    }


    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions{
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(project(":cmplibrary"))

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:${AndroidXVersion.kotlin}")
    implementation ("androidx.core:core-ktx:${AndroidXVersion.core_ktx}")
    implementation ("androidx.appcompat:appcompat:${AndroidXVersion.core_ktx}")
    implementation ("com.google.android.material:material:${AndroidXVersion.material}")
    implementation ("androidx.constraintlayout:constraintlayout:${AndroidXVersion.constraint_layout}")
    implementation ("androidx.navigation:navigation-fragment-ktx:${AndroidXVersion.navigation}")
    implementation ("androidx.navigation:navigation-ui-ktx:${AndroidXVersion.navigation}")
    testImplementation ("junit:junit:${AndroidXVersion.junit}")
    androidTestImplementation ("androidx.test.ext:junit:${AndroidXVersion.junit_ext}")
    androidTestImplementation ("androidx.test.espresso:espresso-core:${AndroidXVersion.espresso}")
}

object AndroidXVersion {
    const val kotlin = "1.4.10"
    const val core_ktx = "1.2.0"
    const val constraint_layout = "2.0.4"
    const val material = "1.2.1"
    const val junit = "4.13.1"
    const val junit_ext = "1.1.2"
    const val test_runner = "1.0.2"
    const val espresso = "3.3.0"
    const val navigation = "2.3.1"
}

/*
plugins {
    id 'com.android.application'
    id 'kotlin-android'
}

android {
    compileSdkVersion 30
    buildToolsVersion "30.0.2"

    defaultConfig {
        applicationId "com.example.myapplication"
        minSdkVersion 16
        targetSdkVersion 30
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {

    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation 'androidx.core:core-ktx:1.2.0'
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'com.google.android.material:material:1.2.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.3.1'
    implementation 'androidx.navigation:navigation-ui-ktx:2.3.1'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
}

*/