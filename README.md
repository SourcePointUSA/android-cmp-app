![Test](https://github.com/SourcePointUSA/android-cmp-app/workflows/Test/badge.svg?branch=develop)

# How to Install ![Maven Central](https://img.shields.io/maven-central/v/com.sourcepoint.cmplibrary/cmplibrary)
To use `cmplibrary` in your app, include `com.sourcepoint.cmplibrary:cmplibrary:x.y.z` as a dependency to your project's build.gradle file.

```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:5.3.13'
}
```

# Usage
## Create new _Config_ object
Use the data builder to obtain a configuration for v6 (Unified SDK). This contains your organization's account information and includes the type of campaigns that will be run on this property. This object will be called when you instantiate your CMP SDK.
```kotlin
    private val spConsentLib by spConsentLibLazy {
        activity = this@MainActivityV6Kt
        spClient = LocalClient()
        privacyManagerTab = PMTab.FEATURES
        config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            +CampaignType.CCPA
            +CampaignType.GDPR
        }
    }
```
## Delegate Methods
Create a client to receive the events from the Cmp SDK

```kotlin
    internal inner class LocalClient : UnitySpClient {
        override fun onMessageReady(message: JSONObject) {}
        override fun onError(error: Throwable) { }
        override fun onConsentReady(consent: SPConsents) { }
        override fun onConsentReady(consent: String) { }
        override fun onAction(view: View, actionType: ActionType) { }
        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
        }
        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
        }
    }
```


## Loading the Privacy Manager on demand
```kotlin
spConsentLib.loadMessage()
```
## Authenticated Consent

```kotlin
spConsentLib.loadMessage(authId = "<authId>")
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
```kotlin
		
    private val spConfig = SpConfigDataBuilder()
        .addAccountId(22)
        .addPropertyName("mobile.multicampaign.demo")
        .addCampaign(CampaignType.GDPR)
        .addCampaign(CampaignType.CCPA)
        .build()

		private lateinit var spConsentLib: SpConsentLib

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spConsentLib = makeConsentLib(
            spConfig = spConfig,
            activity = this,
            messageLanguage = MessageLanguage.ENGLISH
        )
    }

    override fun onResume() {
        super.onResume()
        if (!dataProvider.onlyPm) {
            spConsentLib.loadMessage()
        }
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
            spConsentLib.removeView(view)
        }
        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
        }
    }
```


A few remarks:
1. The web content being loaded (web property) needs to share the same vendor list as the app.
2. The web content needs to include our [js client setup](https://documentation.sourcepoint.com/web-implementation/sourcepoint-gdpr-and-tcf-v2-support/gdpr-and-tcf-v2-setup-and-configuration_v1.1.3) in it.
3. The vendor list's consent scope needs to be set to _Shared Site_ instead of _Single Site_

## Setting a Targeting Param
In order to set a targeting param all you need to do is calling `.setTargetingParam(key: string, value: string)` in the instance of `ConsentLibBuilder`. Example:

```kotlin
    private val spConsentLib by spConsentLibLazy {
        activity = this@MainActivityV6Kt
        spClient = LocalClient()
        privacyManagerTab = PMTab.FEATURES
        config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            +(CampaignType.CCPA to listOf(("location" to "US"), ("language" to "fr")))
            +(CampaignType.GDPR to listOf(("location" to "EU"), ("language" to "fr") ))
        }
    }
```

In this example 2 key/value pairs, "language":"fr" and "location":"EU/US", are passed to the campaign scenario.

## Programmatically consenting the current user
It's possible to programmatically consent the current user to a list of vendors, categories and legitimate interest categories by using the following method from the consentlib:
```kotlin
            spConsentLib.customConsentGDPR(
                vendors = listOf("5ff4d000a228633ac048be41"),
                categories = listOf("608bad95d08d3112188e0e36", "608bad95d08d3112188e0e2f"),
                legIntCategories = emptyList(),
                success = { spCustomConsents -> println("custom consent: [$spCustomConsents]") }
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