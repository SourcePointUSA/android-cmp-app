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

For example:
```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:2.0.0'
}

```

# Usage
* In your main activity, create an instance of `ConsentLib` class using `ConsentLib.newBuilder()` class function passing the configurations and callback handlers to the builder and call `.run()` on the instantiated `ConsentLib` object to load the CMP like following:

```java
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreferences sharedPref;

    private ConsentLib buildConsentLib(Activity activity) throws ConsentLibException {
        ConsentLib.newBuilder(22, "mobile.demo", activity)
                // optional, used for running stage campaigns
                .setStage(false)
                // optional, set custom targeting parameters value can be String and Integer
                .setTargetingParam("CMP", showPM.toString())
                // optional, callback triggered when message choice is selected when called choice
                // type will be available as Integer at cLib.choiceType
                .setOnMessageChoiceSelect(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib c) {
                        Log.i(TAG, "Choice type selected by user: " + c.choiceType.toString());
                    }
                })
                // optional, callback triggered when consent data is captured when called
                .setOnInteractionComplete(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib c) {
                        Log.i(TAG, "euconsent prop: " + c.euconsent);
                        Log.i(TAG, "consentUUID prop: " + c.consentUUID);
                        Log.i(TAG, "euconsent in shared preferences: " + sharedPref.getString(ConsentLib.EU_CONSENT_KEY, null));
                        Log.i(TAG, "consentUUID in shared preferences: " + sharedPref.getString(ConsentLib.CONSENT_UUID_KEY, null));
                        Log.i(TAG, "IABConsent_SubjectToGDPR in shared preferences: " + sharedPref.getString(ConsentLib.IAB_CONSENT_SUBJECT_TO_GDPR, null));
                        Log.i(TAG, "IABConsent_ConsentString in shared preferences: " + sharedPref.getString(ConsentLib.IAB_CONSENT_CONSENT_STRING, null));
                        Log.i(TAG, "IABConsent_ParsedPurposeConsents in shared preferences: " + sharedPref.getString(ConsentLib.IAB_CONSENT_PARSED_PURPOSE_CONSENTS, null));
                        Log.i(TAG, "IABConsent_ParsedVendorConsents in shared preferences: " + sharedPref.getString(ConsentLib.IAB_CONSENT_PARSED_VENDOR_CONSENTS, null));

                        try {
                            // Get the consents for a collection of non-IAB vendors
                            c.getCustomVendorConsents(
                                    new String[]{"5bf7f5c5461e09743fe190b3", "5b2adb86173375159f804c77"},
                                    new ConsentLib.OnLoadComplete() {
                                        @Override
                                        public void onSuccess(Object result) {
                                            HashSet<CustomVendorConsent> consents = (HashSet) result;
                                            for (CustomVendorConsent consent : consents) {
                                                if (consent.id.equals("5bf7f5c5461e09743fe190b3")) {
                                                    Log.i(TAG, "Consented to non-IAB vendor 1: "+consent.name);
                                                }
                                                if (consent.id.equals("5b2adb86173375159f804c77")) {
                                                    Log.i(TAG, "Consented to non-IAB vendor 2: "+consent.name);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(ConsentLibException exception) {
                                            Log.d(TAG, "Something went wrong :( " + exception);
                                        }
                                    });

                            // Example usage of getting all purpose consent results
                            c.getCustomPurposeConsents(new ConsentLib.OnLoadComplete() {
                                public void onSuccess(Object result) {
                                    HashSet<CustomPurposeConsent> consents = (HashSet) result;
                                    for (CustomPurposeConsent consent : consents) {
                                        Log.i(TAG, "Consented to purpose: " + consent.name);
                                    }
                                }
                            });

                            // Example usage of getting IAB vendor consent results for a list of vendors
                            boolean[] IABVendorConsents = c.getIABVendorConsents(new int[]{81, 82});
                            Log.i(TAG, String.format("Consented to IAB vendors: 81 -> %b, 82 -> %b",
                                IABVendorConsents[0],
                                IABVendorConsents[1]
                            ));

                            // Example usage of getting IAB purpose consent results for a list of purposes
                            boolean[] IABPurposeConsents = c.getIABPurposeConsents(new int[]{2, 3});
                            Log.i(TAG, String.format("Consented to IAB purposes: 2 -> %b, 3 -> %b",
                                IABPurposeConsents[0],
                                IABPurposeConsents[1]
                            ));

                        } catch (ConsentLibException e) {
                            e.printStackTrace();
                        }
                    }
                })
                // generate ConsentLib at this point modifying builder will not do anything
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.review_consents);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final Activity activity = this;

        try {
            // build the consent lib and run it on app start
            buildConsentLib(activity).run();

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View _v) {
                    try{
                        // build the consent lib and run it on button click
                        buildConsentLib(activity).run();
                    }
                    catch (Exception e) { e.printStackTrace(); }
                }
            });
        } catch (ConsentLibException e) {
            e.printStackTrace();
        }
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

    <application
        ... >
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
