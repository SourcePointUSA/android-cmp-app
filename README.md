Table of Contents
=================
   * [Setup](#setup)
   * [Usage](#usage)
   * [Docs](#docs)
   * [Development](#development)
      * [How to build the `cmplibrary` module from source](#how-to-build-the-cmplibrary-module-from-source)
      * [How to import the master version of `cmplibrary` into existing an Android app project for development](#how-to-import-the-master-version-of-cmplibrary-into-existing-an-android-app-project-for-development)
      * [How to publish a new version into JCenter](#how-to-publish-a-new-version-into-jcenter)

# Setup
To use `cmplibrary` in your app, include `com.sourcepoint.cmplibrary:cmplibrary:x.y.z` as a dependency to your project's build.gradle.

:heavy_exclamation_mark: **IMPORTANT** if you're not yet using the new message builder, make sure to use pod version < 3.
```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:2.4.3'
}
```

The README for the older version can be found [here](https://github.com/SourcePointUSA/android-cmp-app/blob/aa51fcc0f6bc475e734c6846b3b60abb487732f9/README.md).

For consent messages built using the message builder V2, it's safe to use SDK > 3
```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:3.1.0'
}

```

# Usage
* In your main activity, create an instance of `ConsentLib` class using `ConsentLib.newBuilder()` class function passing the configurations and callback handlers to the builder and call `.run()` on the instantiated `ConsentLib` object to load the CMP like following:

```java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ConsentLib consentLib;

    private ConsentLib buildAndRunConsentLib(Boolean showPM) throws ConsentLibException {
        return ConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",this)
                .setStage(true)
                .setViewGroup(findViewById(android.R.id.content))
                .setShowPM(showPM)
                .setOnMessageReady(consentLib -> Log.i(TAG, "onMessageReady"))
                .setOnConsentReady(consentLib -> consentLib.getCustomVendorConsents(results -> {
                    HashSet<CustomVendorConsent> consents = (HashSet) results;
                    for(CustomVendorConsent consent : consents)
                        Log.i(TAG, "Consented to: "+consent);
                }))
                .setOnErrorOccurred(c -> Log.i(TAG, "Something went wrong: ", c.error))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            consentLib = buildAndRunConsentLib(false);
            consentLib.run();
        } catch (ConsentLibException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            try {
                consentLib = buildAndRunConsentLib(true);
                consentLib.run();
            } catch (ConsentLibException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(consentLib != null ) { consentLib.destroy(); }
    }
}
```
# Docs
For the complete documentation, open `./docs/index.html` in the browser.

# Development
## How to build the `cmplibrary` module from source
Note: skip this step and jump to next section if you already have the compiled the compiled `cmplibrary-release.aar` binary.

* Clone and open `android-cmp-app` project in Android Studio
* Build the project
* Open `Gradle` menu from right hand side menu in Android Studio and select `assemble` under `:cmplibrary > Tasks > assemble`
<img width="747" alt="screen shot 2018-11-05 at 4 52 27 pm" src="https://user-images.githubusercontent.com/2576311/48029062-4c950000-e11b-11e8-8d6f-a50c9f37e25b.png">

* Run the assemble task by selecting `android-cmp-app:cmplibrary [assemble]` (should be already selected) and clicking the build icon (or selecting Build > Make Project) from the menus.
* The release version of the compiled binary should be under `cmplibrary/build/outputs/aar/cmplibrary-release.aar` directory. Copy this file and import it to your project using the steps below.

## How to import the master version of `cmplibrary` into existing an Android app project for development

* Open your existing Android project in Android Studio and select the File > New > New Module menu item.
* Scroll down and select `Import .JAR/.AAR Package` and click next.
* Browse and select the distributed `cmplibrary-release.aar` binary file (or the one you generated using the instructions in the last section)
 * In your project's `app/build.gradle` file make sure you have `cmplibrary-release` as a dependency and also add `com.google.guava:guava:20.0` as a dependency:
```
dependencies {
    ...
    implementation project(":cmplibrary-release")
    implementation("com.google.guava:guava:20.0")
}
```

* Make sure in your project's `settings.gradle` file you have:
```
include ':app', ':cmplibrary-release'
```

* Open `app/src/main/AndroidManifest.xml` and add `android.permission.INTERNET` permission if you do not have the permission in your manifest:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.your-app">
    <uses-permission android:name="android.permission.INTERNET" />

    <application>
        ...
    </application>
</manifest>
```

## How to publish a new version into JCenter
- Make sure you have bumped up the library version in `cmplibrary/build.gradle` but changing the line `def VERSION_NAME = x.y.z`
- Open Gradle menu from right hand side menu in Android Studio
- Run the following three tasks in order from the list of tasks under `cmplibrary` by double clicking on each:
  - `build:clean`
  - `build:assembleRelease`
  - `other:bundleZip`

- If everything goes fine, you should have a `cmplibrary-x.y.z` file in `cmplibrary/build` folder.
- At this time, you have to create a new version manually with the same version name you chose above in BinTray.
- Select the version you just created and click on "Upload Files", select the generated `cmplibrary-x.y.z` file and once appeared in the files list, check `Explode this archive` and click on Save Changes.
- Now you need to push the new version to JCenter: go to the version page in BinTray, you will see a notice in the page "Notice: You have 3 unpublished item(s) for this version (expiring in 6 days and 22 hours) ", click on "Publish" in front of the notice. It will take few hours before your request to publish to JCenter will be approved.
