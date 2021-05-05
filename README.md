![Test](https://github.com/SourcePointUSA/android-cmp-app/workflows/Test/badge.svg?branch=develop)

# How to Install [![Maven Central](https://img.shields.io/maven-central/v/com.sourcepoint.cmplibrary/cmplibrary)](https://search.maven.org/search?q=g:com.sourcepoint.cmplibrary)
To use `cmplibrary` in your app, include `com.sourcepoint.cmplibrary:cmplibrary:x.y.z` as a dependency to your project's build.gradle file.

```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:6.0.1'
}
```

# Usage
## Create new _Config_ object
Use the factory method to obtain a lazy configuration for v6 (Unified SDK). This contains your organization's account information and includes the type of campaigns that will be run on this property. This object will be instantiated at the first usage of the CMP SDK.


Kotlin
```kotlin
    private val spConsentLib by spConsentLibLazy {
        activity = this@MainActivityKotlin
        spClient = LocalClient()
        config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            pmTab = PMTab.FEATURES
            messLanguage = MessageLanguage.ENGLISH
            +CampaignType.CCPA
            +CampaignType.GDPR
        }
    }
```
In case of Java language you can use a factory method to instantiate the Cmp lib  

Java
```java
    // Cmp SDK config
    private final SpConfig spConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addPrivacyManagerTab(PMTab.FEATURES)
            .addMessageLanguage(MessageLanguage.ENGLISH)
            .addCampaign(CampaignType.GDPR)
            .addCampaign(CampaignType.CCPA)
            .build();

    private SpConsentLib spConsentLib = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spConsentLib = FactoryKt.makeConsentLib(
                spConfig,
                this,
                new LocalClient()
        );
    }
```

## Delegate Methods
Create a client to receive the events from the Cmp SDK


Kotlin
```kotlin
    internal inner class LocalClient : SpClient {
        override fun onMessageReady(message: JSONObject) {}
        override fun onError(error: Throwable) { }
        override fun onConsentReady(consent: SPConsents) { }
        override fun onAction(view: View, actionType: ActionType) { }
        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
        }
        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
        }
    }
```
Java
```java
    class LocalClient implements SpClient {

        @Override
        public void onMessageReady(@NotNull JSONObject message) { }

        @Override
        public void onError(@NotNull Throwable error) { error.printStackTrace(); }

        @Override
        public void onConsentReady(@NotNull SPConsents c) { }

        @Override
        public void onAction(View view, @NotNull ActionType actionType) { }

        @Override
        public void onUIFinished(@NotNull View v) {
            spConsentLib.removeView(v);
        }

        @Override
        public void onUIReady(@NotNull View v) {
            spConsentLib.showView(v);
        }
    }
```

## Loading the Privacy Manager on demand
Call `spConsentLib.loadMessage()` to surface the Privacy Manager

Kotlin
```kotlin
    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage()
    }
```
Java
```java
    @Override
    protected void onResume() {
        super.onResume();
        spConsentLib.loadMessage("<authId>");
    }
```
## Releasing resources
Release resources when the activity gets destroyed.

Kotlin
```kotlin
    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }
```
Java
```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        spConsentLib.dispose();
    }
```
## Authenticated Consent
If there is a consent profile associated with `authId` ("JohDoe"), the SDK will bring the consent data from the server, overwriting whatever was stored in the device.

Kotlin
```kotlin
    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage(authId = "<authId>")
    }
```
Java
```java
    @Override
    protected void onResume() {
        super.onResume();
        spConsentLib.loadMessage("<authId>");
    }
```

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
Kotlin
```kotlin

    private val spConsentLib by spConsentLibLazy {
            activity = this@MainActivityKotlin
            spClient = LocalClient()
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                pmTab = PMTab.FEATURES
                messLanguage = MessageLanguage.ENGLISH
                +(CampaignType.GDPR)
                +(CampaignType.CCPA to listOf(("location" to "US")))
            }
    }

    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    internal inner class LocalClient : SpClient {
        override fun onMessageReady(message: JSONObject) { /* ... */ }
        override fun onError(error: Throwable) { /* ... */ }
        override fun onConsentReady(consent: SPConsents) { /* ... */ }
        override fun onAction(view: View, actionType: ActionType) { /* ... */ }
        override fun onUIFinished(view: View) {
        // HERE you can take some action before removing the consent view
            spConsentLib.removeView(view)
        }
        override fun onUIReady(view: View) {
            // HERE you can take some action before inflating the consent view
            spConsentLib.showView(view)
        }
    }
```
Java
```java
    private final SpConfig spConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addPrivacyManagerTab(PMTab.FEATURES)
            .addMessageLanguage(MessageLanguage.ENGLISH)
            .addCampaign(CampaignType.GDPR, Arrays.asList(new TargetingParam("location", "EU")))
            .addCampaign(CampaignType.CCPA, Arrays.asList(new TargetingParam("location", "US")))
            .build();

    private SpConsentLib spConsentLib = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spConsentLib = FactoryKt.makeConsentLib(
                spConfig,
                this,
                new LocalClient()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        spConsentLib.loadMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        spConsentLib.dispose();
    }

    class LocalClient implements SpClient {

        @Override
        public void onMessageReady(@NotNull JSONObject message) { }

        @Override
        public void onError(@NotNull Throwable error) {
            error.printStackTrace();
        }

        @Override
        public void onConsentReady(@NotNull SPConsents c) {
            System.out.println("onConsentReady: " + c);
        }

        @Override
        public void onUIFinished(@NotNull View v) {
            spConsentLib.removeView(v);
        }

        @Override
        public void onUIReady(@NotNull View v) {
            spConsentLib.showView(v);
        }

        @Override
        public void onAction(View view, @NotNull ActionType actionType) { }
    }
```


A few remarks:
1. The web content being loaded (web property) needs to share the same vendor list as the app.
2. The web content needs to include our [js client setup](https://documentation.sourcepoint.com/web-implementation/sourcepoint-gdpr-and-tcf-v2-support/gdpr-and-tcf-v2-setup-and-configuration_v1.1.3) in it.
3. The vendor list's consent scope needs to be set to _Shared Site_ instead of _Single Site_

## Setting a Targeting Param

Targeting params allow you to set arbitrary key/value pairs. These key/value pairs are sent to Sourcepoint servers where they can be used to take a decision within the scenario builder.

Kotlin: customize a unity plus operator to add a list of targeting parameters per campaign type.
```kotlin
    private val spConsentLib by spConsentLibLazy {
        activity = this@MainActivityKotlin
        spClient = LocalClient()
        config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            pmTab = PMTab.FEATURES
            messLanguage = MessageLanguage.ENGLISH
            +(CampaignType.GDPR to listOf(("location" to "EU")))
            +(CampaignType.CCPA to listOf(("location" to "US")))
        }
    }
```
Java: Use `addCampaign` method to add a list of targeting parameters per campaign type. 
```java
    private final SpConfig spConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addPrivacyManagerTab(PMTab.FEATURES)
            .addMessageLanguage(MessageLanguage.ENGLISH)
            .addCampaign(CampaignType.GDPR, Arrays.asList(new TargetingParam("location", "EU")))
            .addCampaign(CampaignType.CCPA, Arrays.asList(new TargetingParam("location", "US")))
            .build();
```
In this example 2 key/value pairs, "language":"fr" and "location":"EU/US", are passed to the campaign scenario.

## Programmatically consenting the current user
It's possible to programmatically consent the current user to a list of vendors, categories and legitimate interest categories by using the following method from the consentlib:
Kotlin
```kotlin
            spConsentLib.customConsentGDPR(
                vendors = listOf("5ff4d000a228633ac048be41"),
                categories = listOf("608bad95d08d3112188e0e36", "608bad95d08d3112188e0e2f"),
                legIntCategories = emptyList(),
                success = { spCustomConsents -> println("custom consent: [$spCustomConsents]") }
            )
```
Java
```java
            spConsentLib.customConsentGDPR(
                    Arrays.asList("5ff4d000a228633ac048be41"),
                    Arrays.asList("608bad95d08d3112188e0e36", "608bad95d08d3112188e0e2f"),
                    new ArrayList<>(),
                    (SPConsents) -> {  return Unit.INSTANCE;  }
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
Note: skip this step and jump to next section if you already have the compiled `cmplibrary-release.aar` binary.

* Clone and open `android-cmp-app` project in Android Studio
* Build the project
* Open `Gradle` menu from right hand side menu in Android Studio and select `assemble` under `:cmplibrary > Tasks > assemble`
<img width="747" alt="screen shot 2018-11-05 at 4 52 27 pm" src="https://user-images.githubusercontent.com/2576311/48029062-4c950000-e11b-11e8-8d6f-a50c9f37e25b.png">

* Run the assemble task by selecting `android-cmp-app:cmplibrary [assemble]` (should be already selected) and clicking the build icon (or selecting Build > Make Project) from the menus.
* The release version of the compiled binary should be under `cmplibrary/build/outputs/aar/cmplibrary-release.aar` directory. Copy this file and import it to your project using the steps below.
