[![Test](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/test.yml/badge.svg)](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/test.yml)
[![SampleApp UI Tests](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/instrumentation_tests.yml/badge.svg)](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/instrumentation_tests.yml)
[![Metaap UI Tests](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/metaap_instrumentation_tests.yml/badge.svg)](https://github.com/SourcePointUSA/android-cmp-app/actions/workflows/metaap_instrumentation_tests.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.sourcepoint.cmplibrary/cmplibrary)](https://search.maven.org/search?q=g:com.sourcepoint.cmplibrary)

### Diagnostic tool for our SDK

[![Get it on Google Play](art/gplay.png)](https://play.google.com/store/apps/details?id=com.sourcepointmeta.metaapp)

### Compatibility

<img src="art/react.png" width=10% height=10%/> <img src="art/flutter.png" width=20% height=20%/>

# Table of Contents

- [How to Install](#how-to-install)
- [Usage](#usage)
  - [Create new _Config_ object](#create-new-config-object)
  - [Create an instance of the CMP library](#create-an-instance-of-the-cmp-library)
  - [Delegate Methods](#delegate-methods)
  - [Loading a Privacy Manager on demand](#loading-a-privacy-manager-on-demand)
  - [Loading the OTT First Layer Message](#loading-the-ott-first-layer-message)
  - [Loading an OTT privacy manager](#loading-an-ott-privacy-manager)
  - [Releasing resources](#releasing-resources)
  - [The _SpConsent_ object](#the-spconsent-object)
  - [Authenticated Consent](#authenticated-consent)
  - [Setting a Targeting Param](#setting-a-targeting-param)
  - [Targeting parameters to target the right environment](#targeting-parameters-to-target-the-right-environment)
  - [Setting a Privacy Manager Id for the Property Group](#setting-a-privacy-manager-id-for-the-property-group)
  - [ProGuard](#proguard)
  - [Adding or Removing custom consents](#adding-or-removing-custom-consents)
  - [Sharing consent with a WebView](#sharing-consent-with-a-webview)
  - [The SpUtils file](#the-sputils-file)
    - `userConsents`
    - `clearAllData`
    - `campaignApplies`
  - [Adding or Removing custom consents](#adding-or-removing-custom-consents)
  - [Vendor Grants object](#vendor-grants-object)
  - [The onAction callback](#the-onaction-callback)
  - [The ConsentAction object](#the-consentaction-object)
  - [`pubData`](#pubdata)
  - [The Nativemessage](NATIVEMESSAGE_GUIDE.md)
  - [Google Additional Consent](#google-additional-consent)
  - [Global Privacy Platform (GPP) Multi-State Privacy (MSPS) Support for OTT](#global-privacy-platform-multi-state-privacy-msps-support-for-ott)
  - [Delete user data](#delete-user-data)
  - [Frequently Asked Questions](#frequently-asked-questions)
- [React Native Integration](docs-reactnative/README-REACTNATIVE.md)

# How to Install

To use `cmplibrary` in your app, include `com.sourcepoint.cmplibrary:cmplibrary:x.y.z` as a dependency to your project's build.gradle file.

```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:7.5.2'
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
                  propertyId = 16893
                  propertyName = "mobile.multicampaign.demo"
                  messLanguage = MessageLanguage.ENGLISH // Optional, default ENGLISH
                  campaignsEnv = CampaignsEnv.PUBLIC // Optional, default PUBLIC
                  messageTimeout = 15000 // Optional, default 10000ms
                  +CampaignType.CCPA // See campaign table
                  +CampaignType.GDPR // See campaign table
                }
```

In case of Java language you can use a factory method to instantiate the Cmp lib

Java

```java
    // Cmp SDK config
    private final SpConfig cmpConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyId(16893)
            .addPropertyName("mobile.multicampaign.demo")
            .addMessageLanguage(MessageLanguage.ENGLISH) // Optional, default ENGLISH
            .addCampaignsEnv(CampaignsEnv.PUBLIC) // Optional, default PUBLIC
            .addMessageTimeout(4000) // Optional, default 3000ms
            .addCampaign(CampaignType.GDPR) //see campaign table
            .addCampaign(CampaignType.CCPA) //see campaign table
            .build();

```

Refer to the table below regarding the different campaigns that can be implemented via the SDK:

| Campaign | **Description**                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `GDPR`   | Used if your property runs a GDPR TCF or GDPR Standard campaign                                                                                                                                                                                                                                                                                                                                                                                 |
| `CCPA`   | Used if your property runs a U.S. Privacy (Legacy) campaign                                                                                                                                                                                                                                                                                                                                                                                     |
| `USNAT`  | Used if your property runs a U.S. Multi-State Privacy campaign. Please do not attempt to utilize both `CCPA` and `USNAT` simultaneously as this poses a compliance risk for your organization. <br><br>This campaign type should only be implemented via the config object on mobile devices. [Click here](#global-privacy-platform-multi-state-privacy-msps-support-for-ott) to learn more about implementing U.S. Multi-State Privacy on OTT. |

## Create an instance of the CMP library

The CMP SDK library is designed to follow the Activity lifecycle, this means that the library MUST be created in the Activity in which you are planning to use the CMP SDK (\_not on a `Fragment`).

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
        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
        }
        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
        }
        override fun onMessageReady(message: JSONObject) {} // Deprecated
        override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) { }
        override fun onError(error: Throwable) { }
        override fun onConsentReady(consent: SPConsents) { }
        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction = consentAction
        override fun onNoIntentActivitiesFound(url: String) {}
        override fun onSpFinished(sPConsents: SPConsents) { }
    }
```

Java

```java
    class LocalClient implements SpClient {

        @Override
        public void onNativeMessageReady(@NotNull MessageStructure message, @NotNull  NativeMessageController messageController) { }

        @Override
        public void onMessageReady(@NotNull JSONObject message) { } // Deprecated

        @Override
        public void onError(@NotNull Throwable error) { error.printStackTrace(); }

        @Override
        public void onConsentReady(@NotNull SPConsents c) { }

        @Override
        public ConsentAction onAction(View view, @NotNull ConsentAction consentAction) { return consentAction; }

        @Override
        public void onUIFinished(@NotNull View v) {
            spConsentLib.removeView(v);
        }

        @Override
        public void onUIReady(@NotNull View v) {
            spConsentLib.showView(v);
        }

        @Override
        public void onSpFinished(@NotNull SPConsents sPConsents) { }
    }
```

Meaning of the callbacks :

- `onUIFinished`: the consent view should be removed;
- `onNativeMessageReady`: the native message should be created;
- `onConsentReady`: the client receives the saved consent;
- `onError`: the client has access to the error details;
- `onUIReady`: the consent view should be inflated;
- `onAction`: the client receives the selected action type and has the chance to set the `pubData` fields;
- `onSpFinished`: there is nothing to process, all the work is done.

Some of the above callbacks work on the main thread while others are work on a worker thread. Please see the table below for the distinction:

| Main thread            | Worker thread  |
| ---------------------- | -------------- |
| `onUIReady`            | `onSpFinished` |
| `onError`              | `onAction`     |
| `onConsentReady`       |                |
| `onNativeMessageReady` |                |
| `onUIFinished`         |                |

## Loading the First Layer Message

In order to show the FLM, the method `spConsentLib.loadMessage()` has to be called.

IMPORTANT : The `loadMessage` needs to be invoked from the Activity `onResume` callback because during its execution
all the components involved in the save consent process get created. If you try to load directly the Privacy Manager
without calling the `loadMessage`, you won't be able to save the edited consent.

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

## Loading a Privacy Manager on demand

Call `spConsentLib.loadPrivacyManager` to surface the Privacy Manager. There are three Privacy Manager types:

```kotlin
    enum class MessageType {
        MOBILE,
        OTT,
        LEGACY_OTT
    }
```

- MOBILE: it is used for mobile devices,
- OTT: it presents a new layout and operates on TV devices,
- LEGACY_OTT: it presents the legacy layout and operates on TV devices.

As a default behavior, the type is determined based on the nature of the device on which your application is executed.
For example, if your application is running on a television, the selected type will be 'OTT,' whereas the 'MOBILE'
type will be assigned in all other cases.

Kotlin

```kotlin
    //... IMPLICIT message type selection
    spConsentLib.loadPrivacyManager(
      "<PM_ID>",
      PMTab.PURPOSES,
      CampaignType.GDPR
    )
    //...
    //... EXPLICIT message type selection
    spConsentLib.loadPrivacyManager(
      "<PM_ID>",
      PMTab.PURPOSES,
      CampaignType.GDPR,
      MOBILE
    )
    //...
```

Java

```java
    //... IMPLICIT message type selection
    spConsentLib.loadPrivacyManager(
            "<PM_ID>",
            PMTab.PURPOSES,
            CampaignType.GDPR
            ));
    //...
    //... EXPLICIT message type selection
            spConsentLib.loadPrivacyManager(
            "<PM_ID>",
            PMTab.PURPOSES,
            CampaignType.GDPR,
            MOBILE
            ));
    //...
```

## Loading the OTT First Layer Message

In order to show an OTT message, the method `spConsentLib.loadMessage()` has to be called.

> The `loadMessage` needs to be invoked from the Activity `onResume` callback because during its execution all the components involved in the save consent process gets created. If you try to load directly the OTT privacy manager without calling the `loadMessage`, you won't be able to save the edited consent.

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

## Loading an OTT privacy manager

An OTT privacy manager can be **resurfaced** for your project (_e.g. via a button in your project_) through the same API used for a regular property:

Kotlin

```kotlin
spConsentLib.loadPrivacyManager("<PM_ID>", CampaignType.GDPR, MessageType.OTT) // For a GDPR campaign
```

Java

```java
    spConsentLib.loadPrivacyManager("<PM_ID>", CampaignType.CCPA, MessageType.OTT); // For a CCPA campaign
```

> In case a property was created from the web builder as OTT/CTV, the Privacy Manager is the first layer message itself, this means that as pmId you should use the message id of your first layer message.

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

## The _SpConsent_ object

The `SpConsent` object contains all the info related with the user consent action.
Following its structure:

```
SpConsent
    |-- gdpr?
    |   |-- uuid: String?
    |   |-- tcData: Map<String, String>
    |   |-- grants: Map<String, GDPRPurposeGrants>
    |   |-- euconsent: String
    |   |-- acceptedCategories: List<String>
    |   |-- apply: Boolean
    |   |-- consentStatus: ConsentStatus
    |-- ccpa?
        |-- uuid: String?
        |-- rejectedCategories: List<String>
        |-- rejectedVendors: List<String>
        |-- status: String?
        |-- uspstring: String
        |-- apply: Boolean
```

### The grants parameter and the GDPRPurposeGrants object

The `grants` object contains information about each vendor and its purposes, it is using the Map class in which

- the key represent a `vendorId` as a String,
- the value represents a `GDPRPurposeGrants` object.

The `GDPRPurposeGrants` class contains:

- a `granted` param which inform the user that all consents have been granted for that vendor,
- a `purposeGrants` param which is a Map of purposes. Each purpose can be opted-in (`true`) or opted-out (`false`)
  Following the structure of the `GDPRPurposeGrants` class.

```
GDPRPurposeGrants
    |-- granted: Boolean
    |-- purposeGrants: Map<String, Boolean>
```

Kotlin

```kotlin
          override fun onConsentReady(consent: SPConsents) {
            val grants = consent.gdpr?.consent?.grants
            grants?.forEach { grant ->
              val granted = grants[grant.key]?.granted
              val purposes = grants[grant.key]?.purposeGrants
              println("vendor: ${grant.key} - granted: $granted - purposes: $purposes")
            }
          }
```

Java

```java
          @Override
          public void onConsentReady(@NotNull SPConsents consent) {
                  Map<String, GDPRPurposeGrants> grants = consent.getGdpr().getConsent().getGrants(); // Nullable
                  Boolean granted = grants.get("<vendorId>").getGranted(); // Nullable
                  Map<String, Boolean> purposes = grants.get("<vendorId>").getPurposeGrants(); // Nullable
                  Boolean acceptedPurpose = purposes.get("<purposeId>"); // Nullable
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
The default value is set to `CampaignsEnv.PUBLIC`

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

## Setting a Privacy Manager Id for the Property Group

Property groups allow your organization to group properties together in order to simplify configurations for mass campaigns and updates.
In order to use a `Privacy Manager Id for the Property Group`, you should edit the SDK configuration object as follows:

Kotlin

```kotlin
    val cmpConfig : SpConfig = config {
                  accountId = 22
                  propertyName = "mobile.multicampaign.demo"
                  messLanguage = MessageLanguage.ENGLISH // Optional, default ENGLISH
                  campaignsEnv = CampaignsEnv.PUBLIC // Optional, default PUBLIC
                  messageTimeout = 4000 // Optional, default 3000ms
                  + SpCampaign(campaignType = CampaignType.GDPR, groupPmId = "1234" ) // 1234 is the id of the privacy manager for the property group
                }
```

Java

```java
    // Cmp SDK config
    private final SpConfig cmpConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addMessageLanguage(MessageLanguage.ENGLISH) // Optional, default ENGLISH
            .addCampaignsEnv(CampaignsEnv.PUBLIC) // Optional, default PUBLIC
            .addMessageTimeout(4000) // Optional, default 3000ms
            .addCampaign(new SpCampaign(CampaignType.GDPR, Collections.emptyList(), "1234")) // 1234 is the property group
            .build();

```

After adding the `Privacy Manager Id for the Property Group`, you should set the flag `useGroupPmIfAvailable`, in the `loadPrivacyManager`, to true:

```kotlin
            spConsentLib.loadPrivacyManager(
              pmId = 1000,
              pmTab = PMTab.PURPOSES,
              campaignType = CampaignType.GDPR,
              useGroupPmIfAvailable = true      // enable the SDK to use the group Pm Id
            )
```

```java
            spConsentLib.loadPrivacyManager(
              1000,                 // pmId
              PMTab.PURPOSES,       // PMTab
              CampaignType.GDPR,    // CampaignType
              true                  // useGroupPmIfAvailable, enable the SDK to use the group Pm Id
            )
```

**Note**: CCPA campaign `Privacy Manager Id for the Property Group` feature is currently not supported.

## ProGuard

Using ProGuard in your project you might need to add the following rules

```editorconfig
# Sourcepoint (CMP)
-keep interface com.sourcepoint.** { *; }
-keep class com.sourcepoint.** { *; }

# kotlinx-serialization-json
# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Don't print notes about potential mistakes or omissions in the configuration for kotlinx-serialization classes
# See also https://github.com/Kotlin/kotlinx.serialization/issues/1900
-dontnote kotlinx.serialization.**

# Serialization core uses `java.lang.ClassValue` for caching inside these specified classes.
# If there is no `java.lang.ClassValue` (for example, in Android), then R8/ProGuard will print a warning.
# However, since in this case they will not be used, we can disable these warnings
-dontwarn kotlinx.serialization.internal.ClassValueWrapper
-dontwarn kotlinx.serialization.internal.ParametrizedClassValueWrapper

```

## Sharing consent with a WebView

After the user grants consent to all the applicable campaigns, the `onSpFinished` callback from `LocalClient` is being triggered with according consent statuses. This SDK provides an API to inject this consent into a WebView, so the web portion of your application does not invoke a consent dialog and will contain the same consent data as the native part.

Kotlin

```kotlin
class YourKotlinActivity {
    // ...
    private var yourWebView: WebView
    // ...
    internal inner class LocalClient : SpClient {
        // ...
        override fun onSpFinished(sPConsents: SPConsents) {
            // ...
            yourWebView.preloadConsent(sPConsents)
            // ...
        }
        // ...
    }
}
```

Java

```java
public class MainActivityJava {
    // ...
    private WebView yourWebView;
    // ...
    class LocalClient implements SpClient {
        // ...
        @Override
        public void onSpFinished(@NotNull SPConsents sPConsents) {
            // ...
            WebViewExtKt.preloadConsent(yourWebView, sPConsents);
            // ...
        }
        // ...
    }
}
```

> Note: Keep in mind that injecting the consent into a WebView should happen on UI thread, and it is up to you to choose the best approach.

A few remarks:

1. The web content being loaded (web property) needs to share the same vendor list as the app.
2. The vendor list's consent scope needs to be set to _Shared Site_ instead of _Single Site_.
3. Your web content needs to be loaded (or loading) on the webview and our web SDK should be included in it. Furthermore, you need to add the query param `_sp_pass_consent=true` to your URL, this will signal to Sourcepoint's web SDK it needs to wait for the consent data to be injected from the native code, instead of immediately querying it from our servers.

## Adding or Removing custom consents

It's possible to programmatically consent the current user to a list of vendors, categories and legitimate interest categories by using the following method from the consent lib:
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

Using the same strategy for the custom consent, it's possible to programmatically delete the current user consent to a list of vendors, categories and legitimate interest categories by using the following method from the consent lib:
Kotlin

```kotlin
            spConsentLib.deleteCustomConsentTo(
                vendors = listOf("5ff4d000a228633ac048be41"),
                categories = listOf("608bad95d08d3112188e0e36", "608bad95d08d3112188e0e2f"),
                legIntCategories = emptyList(),
                success = { spCustomConsents -> println("custom consent: [$spCustomConsents]") }
            )
```

Java

```java
            spConsentLib.deleteCustomConsentTo(
                    Arrays.asList("5ff4d000a228633ac048be41"),
                    Arrays.asList("608bad95d08d3112188e0e36", "608bad95d08d3112188e0e2f"),
                    new ArrayList<>(),
                    (SPConsents) -> {  return Unit.INSTANCE;  }
            )
```

The ids passed will be removed to the list of already accepted vendors, categories and leg. int. categories. The method is asynchronous so you must pass a `Runnable` that will receive back an instance of `GDPRUserConsent` in case of success or it'll call the `onError` callback in case of failure.

It's important to notice, this method is intended to be used for **custom** vendors and purposes only. For IAB vendors and purposes, it's still required to get consents via the consent message or privacy manager.

## The SpUtils file

In some cases it is useful to work with the consent already stored in the `Shared Preferencies` without using an
instance of the Cmp SDK. In order to do that, you can use the `SpUtils.kt` file which contains the following public functions:

- `userConsents`,
- `clearAllData`,
- `campaignApplies`.

### userConsents

This utility function gives you back an instance of the [`SpConsent` object](#the-SpConsent-object).
You can use it as follows:

Kotlin:

```kotlin
import com.sourcepoint.cmplibrary.util.userConsents

// ...
val consent = userConsents(context)
// ...

```

Java:

```java
import com.sourcepoint.cmplibrary.util.SpUtils;

// ...
SpConsent consent = SpUtils.userConsents(context);
// ...

```

### clearAllData

This function is used whenever you need to cancel all the stored data:

Kotlin:

```kotlin
import com.sourcepoint.cmplibrary.util.clearAllData

// ...
val consent = clearAllData(context)
// ...

```

Java:

```java
import com.sourcepoint.cmplibrary.util.SpUtils;

// ...
SpUtils.clearAllData(context);
// ...

```

### campaignApplies

This function is used whenever you need to know if a specific legislation applies:

Kotlin:

```kotlin
import com.sourcepoint.cmplibrary.util.campaignApplies

// ...
val applies = campaignApplies(context, CampaignType.GDPR)
// ...

```

Java:

```java
import com.sourcepoint.cmplibrary.util.SpUtils;

// ...
boolean applies = SpUtils.campaignApplies(context, CampaignType.GDPR);
// ...

```

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

## The `onAction` callback

The on Action callback is created with the purpose of giving the client the chance to know with kind of action was selected
and to set custom information using the `pubData` object.

## The `ConsentAction` object

The `ConsentAction` contains

```
ConsentAction
|-- actionType: ActionType
|-- pubData: JSONObject
|-- campaignType: CampaignType
|-- customActionId: String?
```

- `actionType` is an enumeration type which has the following values: `SHOW_OPTIONS`, `REJECT_ALL`, `ACCEPT_ALL`, `MSG_CANCEL`, `CUSTOM`, `SAVE_AND_EXIT`,`PM_DISMISS`;
- `customActionId` is a `nullable` field which returns the custom id set along with the custom action from our web message builder;
- `pubData` is a JSONObject object, it is used to send custom parameters to our BE;
- `campaignType` is the campaign type associated with the selected action.

## `pubData`

When the user takes an action within the consent UI, it's possible to attach an arbitrary payload to the action data and have it sent to our endpoints.
Those values are `key-values` pairs that have to be added inside the object `ConsentAction` during the `onAction` callback execution.
The `onAction` callback is a non-blocking call for the UI, this means that it gets executed outside the UI main thread.
Following an example:

```kotlin
        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            consentAction.pubData.put("pb_key", "pb_value")
            return consentAction
        }
```

```java
        @Override
        public ConsentAction onAction(@NotNull View view, @NotNull ConsentAction consentAction) {
            consentAction.getPubData().put("pb_key", "pb_value");
            return consentAction;
        }
```

The `pubData` object can be also attached during the call to load the First Layer Message, in this case you only need to create
a `JSONObject` entity with the desired structure and send it as parameter of the `loadMessage` call, following an example

Kotlin

```kotlin

    private val pubData: JSONObject = JSONObject().apply {
      put("timeStamp", 1628620031363)
      put("key_1", "value_1")
      put("key_2", true)
      put("key_3", JSONObject())
    }

    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage(pubData)
    }
```

Java

```java
    @Override
    protected void onResume() {
        super.onResume();
        JSONObject pubData = new JSONObject();
        pubData.put("timeStamp", 1628620031363);
        pubData.put("key_1", "value_1");
        pubData.put("key_2", true);
        pubData.put("key_3", new JSONObject());
        spConsentLib.loadMessage(pubData);
    }
```

## Google Additional Consent

Google additional consent is a concept created by Google and the IAB Framework to pass end-user consent to Google Ad Technology Providers (ATP) despite not adhering to the IAB TCF framework. [Click here](https://docs.sourcepoint.com/hc/en-us/articles/4405115143955) for more information.

Google additional consent is supported in our mobile SDKs and is stored in the `IABTCF_AddtlConsent` key in the user's local storage. Look for the key in the user's local storage and pass the value to Google's SDKs.

## Global Privacy Platform Multi-State Privacy (MSPS) Support for OTT

Starting with version 7.3.0, if your configuration contains a ccpa campaign, it will automatically set GPP data. Unless configured otherwise, the following MSPA attributes will default to:

- MspaCoveredTransaction: `NO`
- MspaOptOutOptionMode: `NOT_APPLICABLE`
- MspaServiceProviderMode: `NOT_APPLICABLE`

Optionally, your organization can customize support for the MSPS by configuring the above attributes as part of the GPP config. [Click here](<https://github.com/SourcePointUSA/android-cmp-app/wiki/Global-Privacy-Platform-(GPP)-Multi%E2%80%90State-Privacy-(MSPS)>) for more information on each attribute, possible values, and examples for signatories and non-signatories of the MSPA.

Kotlin

```kotlin
class MainActivityKotlin : AppCompatActivity() {

    private val sourcePointGppConfig = SpGppConfig(
        coveredTransaction = SpGppOptionBinary.NO, // optional
        optOutOptionMode = SpGppOptionTernary.NOT_APPLICABLE, // optional
        serviceProviderMode = SpGppOptionTernary.NOT_APPLICABLE, // optional
    )

    private val spConsentLib by spConsentLibLazy {
        // ...
        config {
            // ...
            spGppConfig = sourcePointGppConfig
            // ...
        }
    }
}
```

Java

```java
public class MainActivityJava extends AppCompatActivity {

    private SpGppConfig sourcePointGppConfig = new SpGppConfig(
            SpGppOptionBinary.NO,
            SpGppOptionTernary.NOT_APPLICABLE,
            SpGppOptionTernary.NOT_APPLICABLE
    );

    private final SpConfig spConfig = new SpConfigDataBuilder()
            // ...
            .addGppConfig(sourcePointGppConfig)
            // ...
            .build();
}
```

## Delete user data

Utilize the following method if an end-user requests to have their data deleted:

Kotlin

```kotlin
import com.sourcepoint.cmplibrary.util.clearAllData

clearAllData(context: Context)
```

Java

```java
import com.sourcepoint.cmplibrary.util.SpUtils;

SpUtils.clearAllData(context: Context)
```

## Frequently Asked Questions

### 1. How big is the SDK?

The SDK is pretty slim, there are no assets, a single dependency, it's just pure code. The SDK shouldn't exceed `2 MB`.

### 2. What's the lowest Android API supported?

Although our SDK can be technically added to projects targeting Android API 16, we support Android API >= 21 only.

We'll update this list over time, if you have any questions feel free to open an issue or contact your SourcePoint account manager.

### 3. Are deep links supported?

Sourcepoint does not support deep linking due to an HTML sanitizer used in our message rendering app (used by our in-app SDKs to render messages in a webview). Changing the configuration to our HTML sanitizer would compromise our security and introduce vulnerabilities for cross-site scripting (XSS) attacks.

Your organization can mirror deep linking by creating a button with a **Custom Action** choice option in your first layer message and leveraging the following code in your implementation:

```kotlin
override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
    if(consentAction.actionType == ActionType.CUSTOM &&
        consentAction.customActionId == "id-specified-in-portal") {
        // navigate user to intended screen
    }
    return consentAction
}
```
