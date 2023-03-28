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
  - [Releasing resources](#Releasing-resources)
  - [The *SpConsent* object](#The-SpConsent-object)
  - [Authenticated Consent](#Authenticated-Consent)
  - [Preloading](#Preloading)
  - [Sharing consent with a `WebView`](#Sharing-consent-with-a-WebView)
  - [The `authId`](#The-authId)
  - [Complete Example](#Complete-Example)
  - [Setting a Targeting Param](#Setting-a-Targeting-Param)
  - [Targeting parameters to target the right environment](#Targeting-parameters-to-target-the-right-environment)
  - [Set a Privacy Manager Id for the Property Group](#set-a-privacy-manager-id-for-the-property-group)
  - [ProGuard](#ProGuard)
  - [Adding or Removing custom consents](#adding-or-removing-custom-consents)
  - [The SpUtils file](#The-SpUtils-file)
    - `userConsents`
    - `clearAllData`
    - `campaignApplies`
  - [Adding or Removing custom consents](#Adding-or-Removing-custom-consents)
  - [Vendor Grants object](#Vendor-Grants-object)
  - [The onAction callback](#the-onaction-callback)
  - [The ConsentAction object](#the-consentaction-object)
  - [`pubData`](#pubData)
  - [The Nativemessage](NATIVEMESSAGE_GUIDE.md)
  - [Google Additional Consent](#Google-Additional-Consent)
  - [Delete user data](#Delete-user-data)
  - [Frequently Asked Questions](#Frequently-Asked-Questions)
  - [Artifact Release Process](#Artifact-Release-Process)
- [React Native Integration](docs-reactnative/README-REACTNATIVE.md)
 
# How to Install 
To use `cmplibrary` in your app, include `com.sourcepoint.cmplibrary:cmplibrary:x.y.z` as a dependency to your project's build.gradle file.

```
...
dependencies {
    implementation 'com.sourcepoint.cmplibrary:cmplibrary:7.1.1'
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
                  messageTimeout = 4000 // Optional, default 3000ms
                  clientSideOnly = false
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
            .addPropertyId(16893)
            .addPropertyName("mobile.multicampaign.demo")
            .addMessageLanguage(MessageLanguage.ENGLISH) // Optional, default ENGLISH
            .addCampaignsEnv(CampaignsEnv.PUBLIC) // Optional, default PUBLIC
            .addMessageTimeout(4000) // Optional, default 3000ms
            .addCampaign(CampaignType.GDPR)
            .addCampaign(CampaignType.CCPA)
            .isClientSideOnly(false)
            .build();

```

## Create an instance of the CMP library
The CMP SDK library is designed to follow the Activity lifecycle, this means you will to instantiate the library in the Activity in which you are planning to use the CMP SDK (_not on a `Fragment`).

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

| Main thread            	| Worker thread  	|
|------------------------	|----------------	|
| `onUIReady`            	| `onSpFinished` 	|
| `onError`              	| `onAction`     	|
| `onConsentReady`       	|                	|
| `onNativeMessageReady` 	|                	|
| `onUIFinished`         	|                	|

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

An OTT privacy manager can be **resurfaced** for your project (*e.g. via a button in your project*) through the same API used for a regular property:

Kotlin
```kotlin
    spConsentLib.loadPrivacyManager("<PM_ID>", CampaignType.GDPR) // For a GDPR campaign
```
Java
```java
    spConsentLib.loadPrivacyManager("<PM_ID>", CampaignType.CCPA); // For a CCPA campaign
```

>In case a property was created from the web builder as OTT/CTV, the Privacy Manager is the first layer message itself, this means that as pmId you should use the message id of your first layer message.



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
    |   |-- grants: Map<String, GDPRPurposeGrants>
    |   |-- euconsent: String
    |   |-- acceptedCategories: List<String>
    |-- ccpa?
        |-- uuid: String?
        |-- rejectedCategories: List<String>
        |-- rejectedVendors: List<String>
        |-- status: String?
        |-- uspstring: String
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

## Preloading

When configured in the advanced section of the properties vendor list, UUIDs (and subsequently consent data of a user) 
will no be stored server side.
However, the unique user identifiers (UUIDs) will continue to be stored on the device.
From the SDK point of view to enable the preloading capabilities, you need to set, in the [config object](#create-new-config-object), 
the `clientSideOnly` property to true:

Kotlin
```kotlin
    val cmpConfig : SpConfig = config {
                  accountId = 22
                  propertyId = 16893
                  propertyName = "mobile.multicampaign.demo"
                  messLanguage = MessageLanguage.ENGLISH
                  campaignsEnv = CampaignsEnv.PUBLIC
                  messageTimeout = 4000
                  clientSideOnly = true  // Preloading feature
                  +CampaignType.CCPA
                  +CampaignType.GDPR
                }
```

In case of Java language you can use a factory method to instantiate the Cmp lib

Java
```java
    private final SpConfig cmpConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyId(16893)
            .addPropertyName("mobile.multicampaign.demo")
            .addMessageLanguage(MessageLanguage.ENGLISH)
            .addCampaignsEnv(CampaignsEnv.PUBLIC)
            .addMessageTimeout(4000)
            .addCampaign(CampaignType.GDPR)
            .addCampaign(CampaignType.CCPA)
            .isClientSideOnly(true)  // Preloading feature
            .build();

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
        override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) { /* ... */ }
        override fun onError(error: Throwable) { /* ... */ }
        override fun onConsentReady(consent: SPConsents) { /* ... */ }
        override fun onAction(view: View, consentAction: ConsentAction) : ConsentAction{  return consentAction }
        override fun onUIFinished(view: View) {
        // HERE you can take some action before removing the consent view
            spConsentLib.removeView(view)
        }
        override fun onUIReady(view: View) {
            // HERE you can take some action before inflating the consent view
            spConsentLib.showView(view)
        }
        override fun onSpFinished(sPConsents: SPConsents) { }
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
        public void onNativeMessageReady(@NotNull MessageStructure message, @NotNull  NativeMessageController messageController) { }

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
        public ConsentAction onAction(View view, @NotNull ConsentAction consentAction) { return consentAction; }

        @Override
        public void onSpFinished(@NotNull SPConsents sPConsents) { }
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

## Set a Privacy Manager Id for the Property Group

Property groups allow your organization to group properties together in order to simplify configurations for  mass campaigns and updates. 
In order to use a `Privacy Manager Id for the Property Group`, you should edit the SDK configuration object as follows:

Kotlin
```kotlin
    val cmpConfig : SpConfig = config {
                  accountId = 22
                  propertyName = "mobile.multicampaign.demo"
                  messLanguage = MessageLanguage.ENGLISH // Optional, default ENGLISH
                  campaignsEnv = CampaignsEnv.PUBLIC // Optional, default PUBLIC
                  messageTimeout = 4000 // Optional, default 3000ms
                  + SpCampaign(CampaignType.GDPR, "1234") // 1234 is the id of the privacy manager for the property group
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
            .addCampaign(new SpCampaign(CampaignType.GDPR, "1234")) // 1234 is the property group
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
* Merge the `master` branch into the `develop` branch and push.
```
git checkout develop 
git merge master
git push
```
Now post-release process is done and you have consistent solution. Enjoy!
