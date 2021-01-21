![Test](https://github.com/SourcePointUSA/android-cmp-app/workflows/Test/badge.svg?branch=develop)

# How to Install ![Bintray](https://img.shields.io/bintray/v/sourcepoint/sourcepoint/cmplibrary)
To use `cmplibrary` in your app, include `com.sourcepoint.cmplibrary:cmplibrary:x.y.z` as a dependency to your project's build.gradle file.

```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:5.3.12'
}
```

# Usage
Instantiate and build the `ConsentLib` class via `ConsentLib.newBuilder()` static function passing the configurations and callback handlers to the builder.
Once you wish to trigger the consent workflow simply call `.run()` on the instantiated `ConsentLib`.

```java
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "**MainActivity";

    final static int accountId = 22;
    final static int propertyId = 7639;
    final static String propertyName = "tcfv2.mobile.webview";
    final static String pmId = "122058";

    private ViewGroup mainViewGroup;

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
        if(view.getParent() != null) mainViewGroup.removeView(view);
    }

    private GDPRConsentLib buildGDPRConsentLib() {
        return GDPRConsentLib.newBuilder(accountId, propertyName, propertyId, pmId,this)
                .setOnConsentUIReady(this::showView)
                .setOnAction(actionType  -> Log.i(TAG , "ActionType: " + actionType.toString()))
                .setOnConsentUIFinished(this::removeView)
                .setOnConsentReady(consent -> {
                    // at this point it's safe to initialise vendors
                    for (String line : consent.toString().split("\n"))
                        Log.i(TAG, line);
                })
                .setOnError(error -> Log.e(TAG, "Something went wrong"))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGDPRConsentLib().run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        findViewById(R.id.review_consents).setOnClickListener(_v -> buildGDPRConsentLib().showPm());
    }
}
```

## Loading the Privacy Manager on demand
You can load the Privacy Manager (that UI with the toggles) any time programatically by calling the `.showPM()` method. The SDK will follow the same exact same lifecycle as with the 1st layer consent message. First calling the delegate method `onConsentUIReady` when the PM is ready and then calling `onConsentReady` after the user takes an action.

## OTT
If you're working on a Android compatible OTT environment, when building the SDK make sure to call the builder method `.setIsOTT(true)`.
This will instruct the SDK to load OTT-specific message and privacy manager.

## ConsentLibBuilder
On top of the methods exemplified above, the `ConsentLibBuilder` has the following methods:
* `.setStagingCampaign(boolean env)`: passing `true` to this method will instruct the SDK to load a Stage campaign.
* `.setTargetingParams(key, value)`: check the _Setting Targeting Params_ section below.
* `.setIsOTT(boolean)`: instructs the SDK to load OTT specific assets.
* `.setMessageTimeOut(int milliseconds)`: this will control how long it takes between calling `.run()` and one of the callback such as `.onConsentUIReady / .onConsentReady / .onError`. We set this value to 10 seconds by default.
* `.setAuthId(String authID)`: check the section on authenticated consent below.
* `.setOnBeforeSendingConsent(_Callback_ ConsentAction action)`: we'll call this method just before sending the consent action taken by the user to the server.
* `.setShouldCleanConsentOnError(boolean flag)`: if this flag is set to `false`, the SDK won't wipe consent data when the `.onError` callback is called. By default this flag is set to `true`.
* `.setMessageLanguage(MessageLanguage messageLanguage)`: set the language parameter to receive a message in the same language. The message should be translated in the Dashboard to the given language otherwise English will be used as a fallback.

## Authenticated Consent
In order to use the authenticated consent all you need to do is calling the instance method `.setAuthId(String)` on `ConsentLibBuilder`. Example:

```java
ConsentLib.newBuilder(22, "tcfv2.mobile.webview", 7639,"122058", this)
    // other setters
    .setAuthId("JohnDoe")
    .build();
```

This way, if there's a consent profile associated with that `authId` ("JohDoe") the SDK will bring the consent data from the server, overwriting whatever was stored in the device.

## Sharing consent with a `WebView`
In order to share the consent between native and webview the SDK will rely on authenticated consent (explained in detail below).

### The `authId`:
This feature makes use of what we call [Authenticated Consent](https://documentation.sourcepoint.com/consent_mp/authenticated-consent/authenticated-consent-overview). In a nutshell, you provide an identifier for the current user (username, user id, uuid or any unique string) and we'll take care of associating the consent profile to that identifier.
The authId will then assume 1 of the 2 values below:
1. **User is authenticated and have an id:**
In that case the `authId` is going to be that user id.
2. **User is _not_ authenticated and I'm only interested in using consent in this app.**
We recommend using a randomly generated `UUID` as `authId`. Make sure to persist this `authId` before passing it to the builder method `.setAuthId(String)`

### Complete Example
```java
import com.sourcepoint.gdpr_cmplibrary.WebViewUtils;

String authId = UUID.randomUUID().toString(); // make sure to persist this value and reuse it whenever building the SDK

// First execute the SDK passing a `authId`. Either a randomly generated one or whatever token you use to identify an user.
ConsentLib.newBuilder(22, "tcfv2.mobile.webview", 7639, "122058", this)
    // other setters
    .setAuthId(authId)
    .build()
    .run();

// After your `onConsentReady` callback is called. The consent information is ready and persisted in the user's device.
// At this point you're free to use the static method `WebViewUtils.setAuthId` in order to inject authId into the webview.
// When your page is loaded, assuming our JavaScript client is correctly setup, the consent information for that user
// is going to be brought from our server to the webview.
WebView webview = findViewById(R.id.webview);
webview.setWebViewClient(new WebViewClient() {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        WebViewUtils.setAuthId(authId, view);
        super.onPageStarted(view, url, favicon);
    }
});
```

A few remarks:
1. The web content being loaded (web property) needs to share the same vendor list as the app.
2. The web content needs to include our [js client setup](https://documentation.sourcepoint.com/web-implementation/sourcepoint-gdpr-and-tcf-v2-support/gdpr-and-tcf-v2-setup-and-configuration_v1.1.3) in it.
3. The vendor list's consent scope needs to be set to _Shared Site_ instead of _Single Site_

## Setting a Targeting Param
In order to set a targeting param all you need to do is calling `.setTargetingParam(key: string, value: string)` in the instance of `ConsentLibBuilder`. Example:

```java
ConsentLib.newBuilder(22, "tcfv2.mobile.webview", 7639,"122058", this)
    // other setters
    .setTargetingParam("language", "fr")
    .setTargetingParam("foo", "bar")
    .build();
```

In this example 2 key/value pairs, "language":"fr" and "foo":"bar", are passed to the campaign scenario.

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

## Vendor Grants object
The `vendorGrants` is an attribute of `GDPRUserConsent` class. The `vendorGrants` attribute, simply put, is an Map representing the consent state (on a legal basis) of all vendors and its purposes for the current user. For example:
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

## `pubData`
When the user takes an action within the consent UI, it's possible to attach an arbitrary payload to the action data an have it sent to our endpoints. For more information on how to do that check our wiki: [Sending arbitrary data when the user takes an action](https://github.com/SourcePointUSA/android-cmp-app/wiki/Sending-arbitrary-data-when-the-user-takes-an-action)

## Frequently Asked Questions
### 1. How big is the SDK?
The SDK is pretty slim, there are no assets, a single dependency, it's just pure code. The SDK shouldn't exceed `2 MB`.
### 2. What's the lowest Android API supported?
Although our SDK can be technically added to projects targeting Android API 16, we support Android API >= 21 only.

We'll update this list over time, if you have any questions feel free to open an issue or contact your SourcePoint account manager.

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
