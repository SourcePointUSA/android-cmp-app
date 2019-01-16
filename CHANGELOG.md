## 1.5 (January 16, 2018)

* Fix a bug that that would crash apps built for Android API < 19

## 1.4 (January 08, 2018)

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
