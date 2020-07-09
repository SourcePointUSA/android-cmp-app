## 5.3.0 (July, 09, 2020)
Bye bye activity reference 👋
Now to callbacks are being posted to the main event looper instead being fired via `Activity.runOnUIThread(Runnable r)`
Even though it's a minor bump up we took care to not change the public api (except for the `releaseActivity()` method) nor the SDKs behaviour.

* Refactor ConsentLib to decouple from activity
* `releaseActivity()` public method taken out of the public api :warning:

## 5.2.7 (July, 07, 2020)
* fix error when cancel 1st layer message on 1st launch
* fix webview crash when hyperlink is selected from within it

## 5.2.6 (July, 01, 2020)
* fix NativeMessage crash on setAttributes method call
* set NativeMessage attributes to public

## 5.2.5 (Jun, 25, 2020)
* save CMP_SDK_ID and CMP_SDK_VERSION on consentLib constructor

## 5.2.4 (Jun, 19, 2020)
* make VendorGrant class public
* add life cycle methods from iOS SDK
    - onPmReady
    - onMessageReady
    - onPmFinished
    - onMessageFinished
    - onAction
* fix crash on android lollipop webview #178
* ensure to catch errors on async methods

## 5.2.3 (Jun, 04, 2020)
* add vendorGrants to UserConsents obj
* add cached UserConsents obj feature #167
* add release activity public method #168

## 5.2.2 (Jun, 02, 2020)
* fix an issue when loading the PM for properties belonging to property groups - 7c098
* fix an issue preventing campaign with targeting params from showing consent message - 5890c

## 5.2.1 (May, 20, 2020)
* fix issue preventing release of 5.2.0

## 5.2.0 (May, 20, 2020)
* Added the method `customConsentTo` to `GDPRConsentLib` #139
* fixed consent persistence for pm as first layer message
* network calls library changed for okHttp
* check for parent before open/close view

## 5.1.2 (May, 11, 2020)
* fixed an issue with back button while the consent webview was open.
* open links from webview on external browser
* flush IAB consent data before storing new one
* add configurable timeout
* fix login logout behaviour from SDK side

## 4.1.6 (April, 22, 2020)
* fix timeout error on loadPm() from very first launch
* fix message  auto closing after ~16s

## 5.1.1 (April, 19, 2020)
* specialFeatures and lgtInterests added to userConsent

## 5.1.0 (April, 16, 2020)
* fix pmSave&exit action flow
* ConsentAction added

## 5.0.3 (April, 03, 2020)
* Storing IAB consent data earlier by persisting it at the very first http call #114

## 4.1.5 (March, 27, 2020)
* goBack action returns to consent message from page inside webview

##5.0.2 (March, 26, 2020)
* support type changes in nativeMessageJSON

## 5.0.1 (March, 23, 2020)
* added support to [TCFv2](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20CMP%20API%20v2.md#in-app-details) for native message
* removed all warnings during build time
* fix web view transparency issue
* no time out configuration needed any more
* fix app crashing when having no internet connection


## 4.1.4 (March, 09, 2020)
* fix web view transparency issue
* no time out configuration needed any more

## 5.0.0 (February, 23, 2020)
* Added TCFV2 support

## 4.1.3 (February, 23, 2020)
* fix app crashing when having no internet connection

## 4.1.2 (February, 14, 2020)
* keep stored consentString when server does not return one
* simplified webview settings -> no side effects on app cookies  

## 4.1.1 (February, 11, 2020)
* clearAllData() public method added to consentLib
* fix privacy manager not opening from web message
* fix authId issues

## 4.1.0 (February, 05, 2020)
Here we finished a major improvement to the SDK: behold the native message!
You'll be able to build your own consent message using naive elements and layout, simply by extending it from our NativeMessage class. It also allows for style and content customisation via our good and trusted message builder. The basic usage is intended to be very much straight forward and it is exemplified in the updated README file.

* Full native message implementation

## 4.0.3 (January, 29, 2020)
* Fix iabConsentString saved with consentUUID value

## 4.0.2 (January, 23, 2020)
* AndroidManifest updated - solved issue integrating with remote CCPA dependency.

## 4.0.1 (January, 21, 2020)
* Fixed unexpected behavior on error from webview - sheredPreferences data was beeing deleted.

## 4.0.0 (January, 17, 2020)
Alright ladies and gentlemen, what your're that's not a regular release... THAT'S A FULL-ON REWRITE 🔥
There are many small changes in the public API so instead of listing them here we kindly ask you to check the [README](https://github.com/SourcePointUSA/android-cmp-app/blob/develop/README.md). It should provide you with everything you need to get up and running with the changes.

Long story short we have:
* Fixed a naming conflict issue uncovered when using both GPDR and [CCPA](https://github.com/SourcePointUSA/CCPA_Android_SDK) SDKs
* Completely re-wrote the way we load the consent message and Privacy Manager. I don't mean to brag but we have seen an huge performance boost!

As usual, if you see something wrong or have a question feel free to create an issue in our repo or contact us via slack directly.

## 3.1.0 (December, 4, 2019)
* In order to maintain compliance even in the event of an outage on our side, we’re now clearing all local consent information of a user on onErrorOccurred. This behaviour is opt-in be default but can be opted-out by calling ConsentLibBuilder.setShouldCleanConsentOnError(false)
* Changed initialisation params from siteId and siteName to propertyId and property (after all, it makes no sense to have “site” inside our apps…
* Improved test coverage

## 3.0.0 (October, 15, 2019)
Oh wow, time flies when we're having fun huh? This is a major release and, with major releases comes major ~~responsibilities~~ changes.

### New Message script
Our Web Team worked pretty hard to slim down our consent message platform and improve its performance. In this release we make use of the new message script.

**It's important to notice, SDK version 3 onwards will only be compatible with messages built using the new message builder.**

### Consent message lifecycle
* `onMessageReady` is now called only when there's a consent message to be shown rather than being always called but with a boolean flag (`willShowMessage`) indicating if the message is going to show or not.
* Renamed `onInteractionComplete` to `onConsentReady` to better reflect the meaning of that callback.

### Plug & Play Privacy Manager
Prior to this release, there was no way to show the Privacy Manager programmatically, without relying on setting up a tricky scenario on our Dashboard.

We've changed that (keep reading).

### Constructor and Builder changes
In order to support the Plug & Play Privacy Manager, we needed to add extra parameters to `ConsentLib`'s constructor. The additional parameters are:
* `siteId`: a `Number` representing the property id - available on the dashboard
* `privacyManagerId`: a `String` representing the id of the Privacy Manager you wish to show - available on the dashboard

Additionally, a new method was introduced on `ConsentLibBuilder` -> `ConsentLibBuilder.setShowPM(Boolean showPM)`. When set to true, we'll load the Privacy Manager (that one screen with the toggles) rather than "asking" the scenario for a consent message.

### Other improvements
* Reduced the amount of network calls
* Improved our timeout mechanism
* Simplified the Javascript Interface
* Forced `https` everywhere

## 2.4.4 (Aug 20, 2019)
* implemented gdpr_status_check on every SDK initialization.
* implemented onFailure callbacks for api calls.
* Added isEmpty check on "euconsent" string. 

## 2.4.3 (July 10, 2019)
* implemented `ConsentLib.MESSAGE_OPTIONS` enum

## 2.4.2 (July 9, 2019)
* Fixed an issue that would cause the host application to crash with a `NullPointerException`.

## 2.4.1 (July 4, 2019)
* Fixed an issue that would cause the host application to crash. (Cheers to Pauland @mypplication for the pull request)

## 2.4.0 (July 1, 2019)
* Implemented the Identity feature. If you have a "logged-in user" just call `ConsentLibBuilder.setAuthId(String)` passing a unique token that identifies that user and, if the user has consent data
stored in our server, we'll load it instead of a new profile.

## 2.3.6 (June 5, 2019)
* Implemented remove calls to `setWebContentsDebuggingEnabled(true);` `enableSlowWholeDocumentDraw();` in `ConsentWebView` when application is not debuggable (performance and security issues)

## 2.3.5 (May 20, 2019)
* Implemented `ConsentLibBuilder.enableNewPM` when called with true will make possible for the user to switch between the "old" and "new" Privacy Manager.

## 2.3.4 (April 17, 2019)
* Fix an issue with `message timeout timer`, canceled timer before releasing `ConsentWebView`.

## 2.3.3 (April 12, 2019)
* Add timeout for `onMessageReady`

## 2.3.2 (April 10, 2019)
* Fix an issue with `ConsentWebView` prevented the consent message from showing intermittently

## 2.3.1 (April 10, 2019)
* Add timeout for `onMessageReady`  

## 2.3.0 (April 2, 2019)
* Fix an issue with `ConsentWebView` prevented the consent message from showing intermittently
* Added timeout to `ConsentWebView`

## 2.2.1 (March 27, 2019)
* Extracted away the `WebView` setup into `ConsentWebView`.
* Fix a bug that'd prevent the `WebView` from loading the first time on Android > 19

## 2.2.0 (March 25, 2019)
* We changed the way the `WebView` is loaded. You know have two options: 
  1. If you don't mind us managing the view for you, make sure to call `setViewGroup` passing the `ViewGroup` in which the `WebView` should be attached to and we'll take care of everything for you.
  2. If you need more control over views, simply don't call `setViewGroup`. You'll need to add/remove `ConsentLib#webView` to/from your view hierarchy by yourself. This will usually be done on `willShowMessage` and `onConsentReady`.
* `messageWillShow` callback method was renamed to `onMessageReady`
* The callback `onMessageReady` even if the message doesn't need to be displayed.
* Introduced the field boolean `willShowMessage`. This field is set to true when the message is ready and it needs to be shown.

## 2.1.1 (March 20, 2019)
* Implemented the callback method `willShowMessage`
* Re-throw an exception as `ConsentLibException` happening when the consent string could not be parsed

## 2.1.0 (March 15, 2019)
* Introduced `ConsentLib.onError` callback method.
  * If something goes wrong inside the WebView, we'll assign a (hopefully) meaningful exception to `ConsentLib.error` and call `onError` so you'll be able to decide what to do next.

## 2.0.3 (March 12, 2019)
* Rolled minSdkVersion back to 16 in order to maintain backwards compatibility
* Throw `BuildException` if API level < 19
* Downgrade to HTTP when API Level < 24 in order to avoid SSL Handshake issue

## 2.0.2 (March 8, 2019)
* Fixed two bugs that would crash the app when using Android API < 21
  * e9d74af- Moved away from `com.iab.gdpr` in favor of `com.iab.gdpr_android`
  * 7842d25 - Downgrade to `HTTP` when Android API level < 21
* Bump the `minSDK` to 19, due to an [issue](https://github.com/SourcePointUSA/android-cmp-app/issues/25) coming from our Javascript code.

## 2.0.1 (March 4, 2019)
* [Enable D8 for dexing](https://android-developers.googleblog.com/2018/04/android-studio-switching-to-d8-dexer.html)
* Update dependencies

## 2.0.0 (March 1, 2019)
Look at that, we barely released version 1.x and we're already launching v2.0 🎉

This is a major release that include several bug fixes and improved stability.
* Fixed a bug that'd sometimes return an instance of `Exception` from the HTTP calls.
* Major internal refactoring and code simplification.

### Migration Guide

* `setAccount`, `setActivity` and `setSiteName` were removed. Just pass the accountId, siteName and activity to `newBuilder` method instead.
* Rename `getPurposeConsents` to `getCustomPurposeConsents`
* Rename `onLoadCompleted` to `onSuccess`
* The methods `getCustomVendorConsent` and `getPurposeConsent` were deleted in favor of using only `getCustomVendorConsents` and `getCustomPurposeConsents` (notice the plural in the end of the method names).
* `getCustomVendorConsents` no longer passes a `ArrayList<Boolean>`  but a `HashSet<CustomVendorConsent>`. This `HashSet` contains all custom vendor the user has given consent as instances of `CustomVendorConsent`. The `*Consent` classes have public `id` and `name` as attributes.

For more information, the [Usage section of our README](https://github.com/SourcePointUSA/android-cmp-app/#usage) has an example of how to use those methods.

## 1.5.3 (January 16, 2019)

* Change IAB_CONSENT_SUBJECT_TO_GDPR to Boolean on user preferences

## 1.5 (January 16, 2019)

* Fix a bug that that would crash apps built for Android API < 19

## 1.4 (January 08, 2019)

* Open links with `target=_blank` in external browser
* Enable back button to navigate in the Webview

## 1.3 (December 10, 2018)

* Fixed a bug that prevented non-IAB consents from being updated
* Fixed a bug that prevented non-IAB purposes from being updated
* Added a button in example app to showcase how to re-open the Consent Webview

## 1.2 (November 28, 2018)

* Add IAB vendor support implementing `getIABVendorConsents` and `getIABPurposeConsents`

## 1.1 (November 23, 2018)

* Release activity on finish, fixing possible memory leaks
* Make `LoadTask` class `static`, fixing possible memory leaks
* Fix several lint warnings
