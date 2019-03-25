## 2.2.0 (March 25, 2019)
* We changed the way the `WebView` is loaded. You know have two options: 
  1. If you don't mind us managing the view for you, make sure to call `setViewGroup` passing the `ViewGroup` in which the `WebView` should be attached to and we'll take care of everything for you.
  2. If you need more control over views, simply don't call `setViewGroup`. You'll need to add/remove `ConsentLib#webView` to/from your view hierarchy by yourself. This will usually be done on `willShowMessage` and `onInteractionComplete`.
* `messageWillShow` callback method was renamed to `onMessageReady`

## 2.1.1 (March 20, 2019)
* Implemented the callback method `willShowMessage`
* Re-throw an exception as `ConsentLibException` happening when the consent string could not be parsed

## 2.1.0 (March 15, 2019)
* Introduced `ConsentLib.onErrorOccurred` callback method.
  * If something goes wrong inside the WebView, we'll assign a (hopefully) meaningful exception to `ConsentLib.error` and call `onErrorOccurred` so you'll be able to decide what to do next.

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
