[![Test](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/test.yml/badge.svg)](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/test.yml)
[![SampleApp UI Tests](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/instrumentation_tests.yml/badge.svg)](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/instrumentation_tests.yml)
[![Metaap UI Tests](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/metaap_instrumentation_tests.yml/badge.svg)](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/metaap_instrumentation_tests.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.sourcepoint.cmplibrary/cmplibrary)](https://search.maven.org/search?q=g:com.sourcepoint.cmplibrary)

### Diagnostic tool
[![Get it on Google Play](art/gplay.png)](https://play.google.com/store/apps/details?id=com.sourcepointmeta.metaapp)

# Table of Contents
- [How to Install](#how-to-install)
- [Usage](#usage)
  - [Create new _Config_ object](#create-new-config-object)
  - [Create an instance of the CMP library](#create-an-instance-of-the-cmp-library)
  - [Delegate Methods](#delegate-methods)
  - [Loading the Privacy Manager on demand](#loading-the-privacy-manager-on-demand)
  - [Releasing resources](#Releasing-resources)
  - [The *SpConsent* object](#The-SpConsent-object)
  - [Authenticated Consent](#Authenticated-Consent)
  - [Sharing consent with a `WebView`](#Sharing-consent-with-a-WebView)
  - [The `authId`](#The-authId)
  - [Complete Example](#Complete-Example)
  - [Setting a Targeting Param](#Setting-a-Targeting-Param)
  - [Targeting parameters to target the right environment](#Targeting-parameters-to-target-the-right-environment)
  - [ProGuard](#ProGuard)
  - [Programmatically consenting the current user](#Programmatically-consenting-the-current-user)
  - [Vendor Grants object](#Vendor-Grants-object)
  - [`pubData`](#pubData)
  - [Frequently Asked Questions](#Frequently-Asked-Questions)
  - [Artifact Release Process](#Artifact-Release-Process)
> **Note:** Sourcepoint's native message API is currently not supported in Android SDK v6. 
# How to Install 
To use `cmplibrary` in your app, include `com.sourcepoint.cmplibrary:cmplibrary:x.y.z` as a dependency to your project's build.gradle file.

```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:6.2.0'
}
```

# Usage
## Create new _Config_ object
Use the factory method to obtain a lazy configuration for v6 (Unified SDK). This contains your organization's account information and includes the type of campaigns that will be run on this property. This object will be instantiated at the first usage of the CMP SDK.
The config object is a simple DTO.

Kotlin
```kotlin
    val cmpConfig : SpConfig = config {
                  accountId = 22
                  propertyName = "mobile.multicampaign.demo"
                  messLanguage = MessageLanguage.ENGLISH // Optional, default ENGLISH
                  campaignsEnv = CampaignsEnv.PUBLIC // Optional, default PUBLIC
                  messageTimeout = 4000 // Optional, default 3000ms
                  +CampaignType.CCPA
                  +CampaignType.GDPR
                }
```

In case of Java language you can use a factory method to instantiate the Cmp lib  

Java
```java
    // Cmp SDK config
    private final SpConfig cmpConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addMessageLanguage(MessageLanguage.ENGLISH) // Optional, default ENGLISH
            .addCampaignsEnv(CampaignsEnv.PUBLIC) // Optional, default PUBLIC
            .addMessageTimeout(4000) // Optional, default 3000ms
            .addCampaign(CampaignType.GDPR)
            .addCampaign(CampaignType.CCPA)
            .build();

```

## Create an instance of the CMP library
The CMP SDK library is designed to follow the Activity lifecycle, this means that is **mandatory** to instantiate the library in the Activity in which you are planning to use the CMP SDK.

Kotlin 
```kotlin
class MainActivityKotlin : AppCompatActivity() {
    // ...
    private val spConsentLib by spConsentLibLazy {
      activity = this@MainActivityKotlin
      spClient = LocalClient()
      spConfig = cmpConfig
    }
  // ...
}

```

Java
```java
public class MainActivityJava extends AppCompatActivity {

    // ...
    private SpConsentLib spConsentLib = null;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      spConsentLib = FactoryKt.makeConsentLib(
              cmpConfig,
              this,
              new LocalClient()
      );
    }
    // ...
}
    // ...
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

## Loading the First Layer Message
Call `spConsentLib.loadMessage()` from the Activity `onResume` callback  to layout the First Layer Message 

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
        spConsentLib.loadMessage();
    }
```

## Loading the Privacy Manager on demand
Call `spConsentLib.loadPrivacyManager` to surface the Privacy Manager

Kotlin
```kotlin
    //...
    spConsentLib.loadPrivacyManager(
      "<PM_ID>",
      PMTab.PURPOSES,
      CampaignType.GDPR
    )
    //...
```
Java
```java
    //...
    spConsentLib.loadPrivacyManager(
            "<PM_ID>",
            PMTab.PURPOSES,
            CampaignType.GDPR
            ));
    //...
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
## The *SpConsent* object
The `SpConsent` object contains all the info related with the user consent action. 
Following its structure:

```
SpConsent
    |-- gdpr?
    |   |-- uuid: String?
    |   |-- tcData: Map<String, String>
    |   |-- grants: Map<String, Map<String, Boolean>>
    |   |-- euconsent: String
    |-- ccpa?
        |-- uuid: String?
        |-- rejectedCategories: List<Any>
        |-- rejectedVendors: List<Any>
        |-- status: String?
        |-- uspstring: String
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
class MainActivityKotlin : AppCompatActivity() {
  
    private val cmpConfig : SpConfig = config {
      accountId = 22
      propertyName = "mobile.multicampaign.demo"
      messLanguage = MessageLanguage.ENGLISH // Optional, default ENGLISH
      campaignsEnv = CampaignsEnv.PUBLIC // Optional, default PUBLIC
      messageTimeout = 4000 // Optional, default 3000ms
      +CampaignType.CCPA
      +CampaignType.GDPR
    }

    private val spConsentLib by spConsentLibLazy {
            activity = this@MainActivityKotlin
            spClient = LocalClient()
            spConfig = cmpConfig
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
}
```
Java
```java
public class MainActivityJava extends AppCompatActivity {
    
    private final SpConfig cmpConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
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
                cmpConfig,
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

    val cmpConfig : SpConfig = config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            messLanguage = MessageLanguage.ENGLISH
            +(CampaignType.GDPR to listOf(("location" to "EU")))
            +(CampaignType.CCPA to listOf(("location" to "US")))
    }

```
Java: Use `addCampaign` method to add a list of targeting parameters per campaign type. 
```java
    private final SpConfig cmpConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addMessageLanguage(MessageLanguage.ENGLISH)
            .addCampaign(CampaignType.GDPR, Arrays.asList(new TargetingParam("location", "EU")))
            .addCampaign(CampaignType.CCPA, Arrays.asList(new TargetingParam("location", "US")))
            .build();
```
In this example 2 key/value pairs, "language":"fr" and "location":"EU/US", are passed to the campaign scenario.

### Targeting parameters to target the right environment

In order to select the campaign environment you should add the following targeting parameter for each campaign.
The default value is set to ``CampaignsEnv.PUBLIC``

Kotlin

```kotlin
    val cmpConfig : SpConfig = config {
            //  ...
            campaignsEnv = CampaignsEnv.PUBLIC
            //  ...
    }
```

Java

```java
    private final SpConfig spConfig = new SpConfigDataBuilder()
            //
            .addCampaignsEnv(CampaignsEnv.PUBLIC)
            //
            .build();
```

## ProGuard

Using ProGuard in your project you might need to add the following rules

```editorconfig
# Sourcepoint (CMP)
-keep interface com.sourcepoint.** { *; }
-keep class com.sourcepoint.** { *; }
```

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

# Artifact Release Process
## Release
To publish new release artifact you need to do following:
* Checkout `develop` branch and pull the latest commits.
```
git checkout develop
git pull
```
* Create a new branch based on pure develop branch code named `release/x.y.z` where x.y.z stands for ordinal version of upcoming release. Push it to `origin`. 
```
git checkout -b release/x.y.z
git push --set-upstream origin release/x.y.z
```
* Using your preffered text editor, go to `cmplibrary/grade.property` and upgrade `VERSION_NAME = x.y.z`. Don't forget to save the changes!
* Then go to `cmplibrary/release_note.txt`, clear it and fulfill with description of every single commit pushed to `develop` branch since the last artifact release. Please, stick to style of description which appears in that file! This part will appear in changelog after artifact release will be accomplished.
* Commit and push these two files **ONLY**. The commit message **MUST** be “release/x.y.z”. The reason of such strict rules relates to our automated release process; please, take a note that **from now on, committing to `develop` branch is forbidden until successful artifact release.**
```
git add .
git commit -m “release/x.y.z”
git push
```
* Go to your browser and create a new pull request from your `release/x.y.z` branch to `master` branch.
* Hit `Squash and merge` button.
* Go to `Actions` tab of github and wait patientfully unless release process ends. Regularly, it takes no longer than 5 minutes.
* Go to sonatype repository https://s01.oss.sonatype.org/
* Log in with your credentials. Please take a note you should receive permission in order to access to SourcePoint repo.
* Select artifact and click “close” then “confirm”
* Refresh the page and click “release” then ”confirm”
 
From now, you may count your release successful. Artifact will appear on Maven Central in few hours and must be already accesible on GitHub repository page.
## Post-release
However, after you have accomplished artifact release process, few more steps need to be done:
* Checkout `master` branch and pull the commits.
```
git checkout master
git pull
```
* Merge `master` branch to `develop` branch. Push this commit (which contains merged code).
```
git merge develop
```
Now post-release process is done and you have consistent solution. Enjoy!
