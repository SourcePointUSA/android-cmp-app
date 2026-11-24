## Integrate Didomi SDK

This change swaps Sourcepoint's consent UI, business and data layers for the [Didomi SDK](https://developers.didomi.io/cmp/mobile-sdk/android) under the hood. Consent is collected, presented and stored by Didomi. The Sourcepoint public API is kept as close as possible, however some changes are to be expected.

This file tracks what changes for clients and what stays the same, both **syntactically** (what you have to change in your code) and **semantically** (what behaves differently even if your code compiles). It is a living document: every feature that gets implemented on top of Didomi updates its status here.

> This is not the version-to-version guide. For migrations between regular SDK versions see [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md).

### Status legend

| Status | Meaning |
|---|---|
| **Works** | Implemented on top of Didomi. |
| **Stub** | The method exists but is not implemented yet. It reports `SpClient.onError` and does nothing else. |
| **Ignored** | The method or argument still compiles, but has no effect. |
| **Placeholder** | Works, but returns placeholder data until the feature is implemented. |
| **Removed** | No longer exists. Your code will not compile. |

---

## 1. Update the dependency

Update the SDK version. It now pulls in `io.didomi.sdk:android` as a regular (`implementation`) dependency. Nothing else to add, and you must not add or pin Didomi yourself.

Things to expect from the new transitive dependencies (Didomi, `androidx.fragment:fragment-ktx`, and Didomi's own: AppCompat, Material, ConstraintLayout, RecyclerView, Compose, ...):

- A larger APK and possible version conflicts with the AndroidX / Material / Compose versions you already use.
- `INTERNET` and `ACCESS_NETWORK_STATE` permissions are merged into your manifest by Didomi.
- Didomi's minimum SDK is 21, the same as this SDK's.

## 2. Swap the initializer

```kotlin
// Before
val spConfig = config {
    accountId = 22
    propertyId = 16893
    propertyName = "mobile.demo"
    messLanguage = MessageLanguage.ENGLISH
    campaignsEnv = CampaignsEnv.PUBLIC
    +CampaignType.GDPR
    +(CampaignType.USNAT to setOf(ConfigOption.SUPPORT_LEGACY_USPSTRING))
}

// After
val spConfig = config {
    messLanguage = MessageLanguage.ENGLISH          // optional
    addDidomiConfig(
        DidomiConfig(
            apiKey = "<didomi-api-key>",
            noticeId = "<didomi-notice-id>",        // optional
        )
    )
}
```

Java:

```java
// Before
SpConfig spConfig = new SpConfigDataBuilder()
    .addAccountId(22).addPropertyId(16893).addPropertyName("mobile.demo")
    .addCampaign(CampaignType.GDPR)
    .build();

// After
SpConfig spConfig = new SpConfigDataBuilder()
    .addDidomiConfig(new DidomiConfig("<didomi-api-key>", "<didomi-notice-id>"))
    .build();
```

- `accountId`, `propertyId`, `propertyName` and `campaigns` are gone. The Didomi notice decides which regulation applies.
- `makeConsentLib` and `spConsentLibLazy` throw an `IllegalArgumentException` when `didomiConfig` is missing.
- `DidomiConfig` takes `apiKey` (required), `noticeId`, `languageCode`, `localConfigurationPath` and `remoteConfigurationUrl` (all optional). Use no local configuration in production: a local config is for development and testing only.
- The initializer is expected to change again in a future release. Treat `DidomiConfig` as provisional.

### Config reference

| Before | Now |
|---|---|
| `accountId`, `addAccountId(...)` | **Removed** |
| `propertyId`, `addPropertyId(...)` | **Removed** |
| `propertyName`, `addPropertyName(...)` | **Removed** |
| `+CampaignType.X`, `addCampaign(...)`, `SpCampaign`, `TargetingParam` | **Removed** |
| `CampaignType`, `ConfigOption` and `CampaignType to setOf(...)` | **Removed** |
| `campaignsEnv`, `addCampaignsEnv(...)`, `CampaignsEnv` | **Removed** |
| `spGppConfig`, `addGppConfig(...)` | **Ignored** |
| `messLanguage`, `addMessageLanguage(...)` | **Ignored.** Use `DidomiConfig.languageCode` (`null` = device language). |
| `messageTimeout`, `addMessageTimeout(...)` | **Works**, see [Timeouts](#timeouts). |
| `dismissMessageOnBackPress` | **Ignored** for Didomi's view. |
| `addDidomiConfig(DidomiConfig(...))` | **New, required** |

`makeConsentLib(...)` still accepts a `connectionManager`, but it is **Ignored**: Sourcepoint's network layer is no longer used.

## 3. Host Activity must be a `FragmentActivity`

Didomi's UI needs a `FragmentActivity` (for example `AppCompatActivity`). This is a **breaking change**: when the Activity you pass to `makeConsentLib` / `spConsentLibLazy` is a plain `Activity`, no consent UI is shown and `SpClient.onError` is called.

## 4. Everything else, method by method

### `SpConsentLib`

| Method | Syntax | Semantics | Status |
|---|---|---|---|
| `loadMessage()` and overloads | Unchanged | Asks Didomi whether consent is needed and shows its notice if so. `authId`, `pubData` and `cmpViewId` are **Ignored** for now. Calling it again while a consent UI is displayed (e.g. from `onResume`) does nothing. | **Works** |
| `loadPrivacyManager(...)` (6 overloads) | `campaignType` argument **Removed** (see below) | All overloads open Didomi's preferences screen. `pmId`, `pmTab`, `useGroupPmIfAvailable` and `messageType` are **Ignored**. | **Works** |
| `rejectAll(campaignType)` | Now `rejectAll()` | Not implemented yet. | **Stub** |
| `customConsentGDPR(...)`, `deleteCustomConsentTo(...)` | Unchanged | Not implemented yet. The success callback is never called. | **Stub** |
| `clearLocalData()` | Unchanged | Not implemented yet. It only logs a warning. | **Stub** |
| `dismissMessage()` | Unchanged | Hides Didomi's notice and preferences. | **Works** |
| `showView(view)` / `removeView(view)` | Unchanged | Same helpers, used with the view from `onUIReady` / `onUIFinished`. | **Works** |
| `dispose()` | Unchanged | No longer deprecated. **Optional**: the SDK now releases Didomi's listeners by itself when the Activity is destroyed. | **Works** |
| `cleanUserDataOnError` | Unchanged | Calls `clearLocalData()`, which is a stub. | **Stub** |
| `handleOnBackPress(...)` | Unchanged (already deprecated) | No effect. | **Ignored** |

`loadPrivacyManager` overloads, before and after:

```kotlin
// Before
loadPrivacyManager(pmId = "123", pmTab = PMTab.PURPOSES, campaignType = CampaignType.GDPR)
rejectAll(CampaignType.GDPR)

// After
loadPrivacyManager(pmId = "123", pmTab = PMTab.PURPOSES)   // pmId and pmTab are ignored
rejectAll()
```

These two are still being refactored. Expect their signatures to change again.

### `SpClient`

| Callback | Syntax | Semantics | Status |
|---|---|---|---|
| `onUIReady(view)` | Unchanged | `view` is Didomi's WebView. It is delivered when Didomi creates it, **before the page has finished loading**. Add it with `showView(view)`. | **Works** |
| `onUIFinished(view)` | Unchanged | Remove the view with `removeView(view)`. Didomi may already have destroyed the view, so do not use it for anything else. | **Works** |
| `onConsentReady(consent)` | Unchanged | Called when the user's consent changes, and when no UI is needed (before `onSpFinished`). The `SPConsents` payload is a **Placeholder**, see [Consent data](#consent-data). | **Placeholder** |
| `onSpFinished(consents)` | Unchanged | Called when Didomi's UI is finished, or right after `onConsentReady` when no UI is needed. Same payload caveat. | **Placeholder** |
| `onError(error)` | Unchanged | Didomi errors arrive as `RenderingAppException(description)`. Timeouts and missing-`FragmentActivity` also arrive here. | **Works** |
| `onAction(view, consentAction)` | Unchanged | **Never called** yet. | **Stub** |
| `onNativeMessageReady(...)`, `onMessageReady(...)` | **Removed** | Native messages are no longer supported. | **Removed** |
| `onNoIntentActivitiesFound(url)` | Unchanged | Never called. | **Ignored** |
| `onMessageInactivityTimeout()` | Unchanged | Never called. | **Ignored** |

### Removed types and functions

| Removed | Notes |
|---|---|
| `CampaignType`, `CampaignsEnv`, `SpCampaign`, `TargetingParam`, `ConfigOption` | The Didomi notice decides the regulation. |
| `NativeMessageController`, `MessageStructure` and the native message model | Native messages are no longer supported. |
| `NativeMessageActionType` | Same. |
| `ConsentAction.campaignType`, `SPConsentAction.campaignType` | No campaigns. |
| `SPConsentAction.toCore()` | Sourcepoint core is no longer used. |
| `SpUtils.campaignApplies(...)` | No campaigns. |

## 5. Semantic changes to be aware of

### Threading

All `SpClient` callbacks now run on the **main thread**. Before, `onSpFinished` and `onAction` ran on a worker thread.

### Timeouts

`messageTimeout` now covers Didomi's initialization and the creation of its notice. When it is exceeded, `onError(RenderingAppException)` is called. The timeout stops once Didomi reports that its UI is ready. A UI that shows up after the timeout is still delivered through `onUIReady`.

### Consent data

`SPConsents` is currently a **Placeholder**: it is built from an empty mapping, so its GDPR consent has no `uuid`, an empty `euconsent` string and no grants, and `ccpa`, `usNat`, `globalcmp` and `preferences` are `null`. Mapping Didomi's consent to `SPConsents` (including GDPR, CCPA and US states) is the next piece of work. Until then, read the consent from the standard IAB keys in `SharedPreferences`, which Didomi writes (`IABTCF_*`, `IABGPP_*`, `IABUSPrivacy_String`).

### Storage

Didomi owns consent storage. `userConsents()` and `clearAllData()` in `SpUtils` still read and clear **Sourcepoint's** old state, not Didomi's, until they are migrated.

### Google Consent Mode

`googleConsentMode` on the consent is `null`. Remove any code that derives Google Consent Mode signals from it. Didomi's own Google Consent Mode support is not covered here yet.

### Messages and platforms

| Area | Now |
|---|---|
| Native messages | **No longer supported.** |
| OTT / Android TV messages (`MessageType.OTT`, `LEGACY_OTT`) | **No longer supported.** `messageType` is ignored. |
| `authId` | **Ignored** for now. Planned: mapped to Didomi's user ID and synced before the message loads. |
| `pubData` | **Ignored** for now. Planned: sent with `onAction`. |
| Language | Set once from `DidomiConfig.languageCode`. |
| Back press | `dismissMessageOnBackPress` has no effect on Didomi's view. |

### Rotation and lifecycle

Create the library in the Activity and call `loadMessage()` in `onResume()`, as before. Declare `android:configChanges="orientation|screenSize"` as before. Without it, the Activity is recreated and a new library instance takes over.

## 6. Existing users

Didomi can migrate consent stored by the Sourcepoint SDK (`sp.state`) into its own state on first launch, so migrated users keep their choices. This depends on the Didomi build being used and has not been verified from the Android SDK yet.

The migration of consent stored by pre-7.x versions (`migrateLegacyToNewState`) has been **removed**: it needed the `accountId` and `propertyId` that are no longer part of the configuration.

---

## Open items

Features that still have to be implemented on top of Didomi:

- [ ] Consent mapping: Didomi to `SPConsents` (GDPR, CCPA, US states, `globalcmp`)
- [ ] `rejectAll()`
- [ ] `customConsentGDPR(...)` / `deleteCustomConsentTo(...)` (needs the Sourcepoint IDs of vendors and purposes from Didomi)
- [ ] `clearLocalData()`, `clearAllData()` and `userConsents()` backed by Didomi
- [ ] `onAction(...)`
- [ ] `authId` and `pubData`
- [ ] Refactor `rejectAll` and `loadPrivacyManager` signatures
- [ ] Language handling and Android TV
- [ ] Final initializer
