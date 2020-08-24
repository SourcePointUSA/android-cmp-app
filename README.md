# How to Install
To use `cmplibrary` in your app, include `com.sourcepoint.cmplibrary:cmplibrary:x.y.z` as a dependency to your project's build.gradle.

:heavy_exclamation_mark: **IMPORTANT** if you still haven't moved to TCFv2, use `v4.x`.
```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:4.1.5'
}
```

The README for the older version can be found [here](https://github.com/SourcePointUSA/android-cmp-app/blob/aa51fcc0f6bc475e734c6846b3b60abb487732f9/README.md).

The following documentation and code is suitable for properties supporting TCFv2
```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:5.3.7'
}

```

# Usage
* In your main activity, create an instance of `ConsentLib` class using `ConsentLib.newBuilder()` class function passing the configurations and callback handlers to the builder and call `.run()` on the instantiated `ConsentLib` object to load the CMP like following:

```java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "**MainActivity";

    private ViewGroup mainViewGroup;

    private PropertyConfig config;

    private GDPRConsentLib buildGDPRConsentLib() {
        return GDPRConsentLib.newBuilder(config.accountId, config.propertyName, config.propertyId, config.pmId,this)
                        .setOnConsentUIReady(view -> {
                            showView(view);
                            Log.i(TAG, "onConsentUIReady");
                        })
                        .setOnConsentUIFinished(view -> {
                            removeView(view);
                            Log.i(TAG, "onConsentUIFinished");
                        })
                        .setOnConsentReady(consent -> {
                            Log.i(TAG, "onConsentReady");
                            Log.i(TAG, "consentString: " + consent.consentString);
                            Log.i(TAG, consent.TCData.toString());
                            for (String vendorId : consent.acceptedVendors) {
                                Log.i(TAG, "The vendor " + vendorId + " was accepted.");
                            }
                            for (String purposeId : consent.acceptedCategories) {
                                Log.i(TAG, "The category " + purposeId + " was accepted.");
                            }
                            for (String purposeId : consent.legIntCategories) {
                                Log.i(TAG, "The legIntCategory " + purposeId + " was accepted.");
                            }
                            for (String specialFeatureId : consent.specialFeatures) {
                                Log.i(TAG, "The specialFeature " + specialFeatureId + " was accepted.");
                            }
                        })
                .setOnError(error -> {
                    Log.e(TAG, "Something went wrong: ", error);
                    Log.i(TAG, "ConsentLibErrorMessage: " + error.consentLibErrorMessage);
                })
                .build();
    }

    private NativeMessage buildNativeMessage(){
        return new NativeMessage(this){
            @Override
            public void init(){
                super.init();
                // When using a customized layout one can completely override the init method
                // not calling super.init() and inflating the native view with the chosen layout instead.
                // In this case its important to set all the default child views using the setter methods
                // like its done in the super.init()
            }
            @Override
            public void setAttributes(NativeMessageAttrs attrs){
                super.setAttributes(attrs);
                //Here one can extend this method in order to set customized attributes other then the ones
                //already set in the super.setAttributes. No need to completely override this method.
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "init");
        buildGDPRConsentLib().run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        config = getConfig(R.raw.mobile_demo_web);
        findViewById(R.id.review_consents).setOnClickListener(_v -> buildGDPRConsentLib().showPm());
    }
    private void showView(View view) {
        if(view.getParent() == null){
            view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.bringToFront();
            view.requestLayout();
            mainViewGroup.addView(view);
        }
    }
    private void removeView(View view) {
        if(view.getParent() != null)
            mainViewGroup.removeView(view);
    }

    private PropertyConfig getConfig(int configResource){
        PropertyConfig config = null;
        try {
            config = new PropertyConfig(new JSONObject(new Scanner(getResources().openRawResource(configResource)).useDelimiter("\\A").next()));
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse config file.", e);
        }
        return config;
    }

}
```

## Programmatically consenting the current user
It's possible to programmatically consent the current user to a list of vendors, categories and legitimate interest categories by using the following method from the consentlib:
```java
customConsentTo(
            ArrayList<String> vendors,
            ArrayList<String> categories,
            ArrayList<String> legIntCategories,
            OnConsentReadyCallback onCustomConsentReady
    )
```
The ids passed will be appended to the list of already accepted vendors, categories and leg. int. categories. The method is asynchronous so you must pass a `Runnable` that will receive back an instance of `GDPRUserConsent` in case of success or it'll call the `onError` callback in case of failure.

It's important to notice, this method is intended to be used for **custom** vendors and purposes only. For IAB vendors and purposes, it's still required to get consents via the consent message or privacy manager.

## Authenticated Consent

In order to use the authenticated consent all you need to do is calling `.setAuthId(String)` in the instance of `ConsentLibBuilder`. Example: 

```java
ConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",this)
    // calling other .set methods
    .setAuthId("JohnDoe")
    .build();
```

This way, if we already have consent for that token (`"JohDoe"`) we'll bring the consent profile from the server, overwriting whatever was stored in the device.

## Vendor Grants object

* `vendorGrants` is an attribute of `GDPRUserConsent` class. The `vendorGrants` attribute, simply put, is an Map reprensenting the consent state (on a legal basis) of all vendors and its purposes for the current user. For example:
```Java
[
  "vendorId1": VendorGrant(
    vendorGrant: boolean,
    purposeGrants: [
      "purposeId1": boolean,
      "purposeId2": boolean,
      // more purposes here
    ]
  )
  // more vendors here
]
```

## Setting a Targeting Param

In order to set a targeting param all you need to do is calling `.setTargetingParam(key: string, value: string)
` in the instance of `ConsentLibBuilder`. Example: 

```java
ConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",this)
    // calling other .set methods
    .setTargetingParam("language", "fr")
    .build();
```

In this example a key/value pair "language":"fr" is passed to the sp scenario and can be useded, wiht the proper scenario setup, to show a french message instead of a english one.

## Frequently Asked Questions
### 1. How big is the SDK?
The SDK is pretty slim, there are no assets, a single dependency, it's just pure code. The SDK shouldn't exceed `2 MB`.
### 2. What's the lowest Android API supported?
Although our SDK can be technically added to projects targeting Android API 16, we support Android API >= 21 only.

We'll update this list over time, if you have any questions feel free to open an issue or concact your SourcePoint account manager.

--- 

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
