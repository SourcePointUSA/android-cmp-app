## 6.7.2 (October, 17, 2022)
* [HCD-303](https://sourcepoint.atlassian.net/browse/HCD-303) Update migration guide with enabling multi-campaign toggle (#537)
* [DIA-1006](https://sourcepoint.atlassian.net/browse/DIA-1006) Support Native OTT message (#533)
* [DIA-1212](https://sourcepoint.atlassian.net/browse/DIA-1212) Refresh ext (#531)
* [DIA-1211](https://sourcepoint.atlassian.net/browse/DIA-1211) Reload btn in the demo Activity (#530)
* [DIA-1204](https://sourcepoint.atlassian.net/browse/DIA-1204) ClearAll button logic (#528)
* [DIA-1100](https://sourcepoint.atlassian.net/browse/DIA-1100) Add/Delete property buttons android tv (#520)
* [DIA-1174](https://sourcepoint.atlassian.net/browse/DIA-1174) Add PmId field in the EditScreen (#524)
* [DIA-1162](https://sourcepoint.atlassian.net/browse/DIA-1162) Demo Screen tv (#523)
* [DIA-1161](https://sourcepoint.atlassian.net/browse/DIA-1161) Android tv navigation between activities  (#521)
* [DIA-1155](https://sourcepoint.atlassian.net/browse/DIA-1155)  Refactory of the tv property card (#519)
* [HDC-295](https://sourcepoint.atlassian.net/browse/DIA-295) Add FAQ -- deep links (#514)
* [DIA-572](https://sourcepoint.atlassian.net/browse/DIA-572) Introducing a Sealed class for the CCPA consent status (#517)
* [DIA-1114](https://sourcepoint.atlassian.net/browse/DIA-1114) Edit screen for the property attributes  (#518)
* [DIA-515](https://sourcepoint.atlassian.net/browse/DIA-515) Main activity usable with a directional pad/remotecontrol (#502)
* [DIA-1093](https://sourcepoint.atlassian.net/browse/DIA-1093) Metaapp - Property detail page for OTT (#516)
* [DIA-280](https://sourcepoint.atlassian.net/browse/DIA-280) Upgrade the SDK project to the latest Gradle version (#515)
* [DIA-999](https://sourcepoint.atlassian.net/browse/DIA-999) Graceful degradation (#510)

## 6.7.1 (August, 02, 2022)
* [DIA-1054](https://sourcepoint.atlassian.net/browse/DIA-1054) IABUSPrivacy_String inconsistency in DataStorage (#512)
* [DIA-943](https://sourcepoint.atlassian.net/browse/DIA-943) Updated UI tests on old ott PM (#509)
* [DIA-547](https://sourcepoint.atlassian.net/browse/DIA-547) Adding docs about SpUtil in the README file (#508)

## 6.7.0 (June, 24, 2022)
* [DIA-841](https://sourcepoint.atlassian.net/browse/DIA-841) Behaviour Change onAction Callback in Android SDK 6.6.1 (#505)
* [DIA-563](https://sourcepoint.atlassian.net/browse/DIA-563) Implementation of the deleteCustomConsentTo api (#503)
* [DIA-188](https://sourcepoint.atlassian.net/browse/DIA-188) Docs React Native pt 1 (#504)
* [DIA-638](https://sourcepoint.atlassian.net/browse/DIA-638) Removing the userConfig param from userConsents function (#500)
* [DIA-686](https://sourcepoint.atlassian.net/browse/DIA-686) Configurable ConsentWebView viewId (#499)
* [DIA-580](https://sourcepoint.atlassian.net/browse/DIA-580) Making the elements of the ViewsManagerImpl to have the same lifetime (#501)

## 6.6.1 (June, 03, 2022)
* [DIA-274](https://sourcepoint.atlassian.net/browse/DIA-274) Update of the custom metrics URL in `build.gradle.kts` (#497)
* [DIA-579](https://sourcepoint.atlassian.net/browse/DIA-579) `List<Any>` is substituted with `List<String>` in `CCPAConsentInternal` class (#495)

## 6.6.0 (May, 20, 2022)
* [DIA-273](https://sourcepoint.atlassian.net/browse/DIA-273) From v6.6.0, to surface the OTT Privacy Manager you should use Merge the loadPrivacyManager API, now the CMP SDK is able automatically to detect an OTT property. Follow this [link](https://github.com/SourcePointUSA/android-cmp-app#loading-an-ott-privacy-manager) for more info. (#492)
* [DIA-277](https://sourcepoint.atlassian.net/browse/DIA-277) Privacy Manager translation fix (#493)

## 6.5.0 (May, 06, 2022)
* [SP-9022](https://sourcepoint.atlassian.net/browse/SP-9022) Added applies attribute to the consent object (#483)
* [SP-8824](https://sourcepoint.atlassian.net/browse/SP-8824) Add support of groupPmId and childPmId (#484). This new feature bring some change to the public API. Follow this [link](https://github.com/SourcePointUSA/android-cmp-app#set-a-privacy-manager-id-for-the-property-group) for more info.

## 6.4.3 (April, 22, 2022)
* [SP-9187](https://sourcepoint.atlassian.net/browse/SP-9187) Applied scale factor to the loadPm API (#485)

## 6.4.2 (April, 06, 2022)
* [SP-8981](https://sourcepoint.atlassian.net/browse/SP-8981) Added scale factor for ott properties (#478)

## 6.4.1 (March, 11, 2022)
* [SP-8903](https://sourcepoint.atlassian.net/browse/SP-8903) The reset of the SDK shared preferences won't be execute in the dispose method anymore (#474)

## 6.4.0 (February, 17, 2022)
* [SP-8615](https://sourcepoint.atlassian.net/browse/SP-8615) Give the option to send the Pubdata obj as parameter of the getMessage call (#464)
* [SP-7846](https://sourcepoint.atlassian.net/browse/SP-7846) Remove unused variables from the factory function and the builder obj (#466)
* [SP-8381](https://sourcepoint.atlassian.net/browse/SP-8381) Customtab issue (#463)
* [SP-8329](https://sourcepoint.atlassian.net/browse/SP-8329) Add regression tests for the Native Message feature (#458)

## 6.3.5 (January, 27, 2022)
* [SP-8382](https://sourcepoint.atlassian.net/browse/SP-8382) Fix webview issue on android version prior to API 21 (#455)

## 6.3.4 (December, 15, 2021)
* [SP-8377](https://sourcepoint.atlassian.net/browse/SP-8377) Add callback onNoIntentActivitiesFound to SpClient (#449)

## 6.3.3 (December, 06, 2021)
* [SP-8351](https://sourcepoint.atlassian.net/browse/SP-8351) ClientEventManager working always on the same thread (#444)

## 6.3.2 (December, 01, 2021)
* [SP-8328](https://sourcepoint.atlassian.net/browse/SP-8328) Add OnSpFinished to the PM (#442)
* [SP-8314](https://sourcepoint.atlassian.net/browse/SP-8314) Adding regression test for the MSG_CANCEL, PM_DISMISS actions (#441)

## 6.3.1 (November, 29, 2021)
* [SP-8277](https://sourcepoint.atlassian.net/browse/SP-8277) Added a new OTT api for PM, [more info here](https://github.com/SourcePointUSA/android-cmp-app#loading-an-ott-privacy-manager-on-demand) (#438)
* [SP-8098](https://sourcepoint.atlassian.net/browse/SP-8098) SDK callback signaling that the SDK is done - onSPFinished (#437)
* [SP-8249](https://sourcepoint.atlassian.net/browse/SP-8249) Enhancing stability of the regression tests (#436)

## 6.3.0 (November, 11, 2021)
* [SP-7840](https://sourcepoint.atlassian.net/browse/SP-7840) New CUSTOM ActionType, [more info here](https://github.com/SourcePointUSA/android-cmp-app#the-consentaction-object)
* [SP-7840](https://sourcepoint.atlassian.net/browse/SP-7840) SKD change to acknowledge the new action type for Consent-Paywall (#434)
* [SP-8225](https://sourcepoint.atlassian.net/browse/SP-8225) New onAction api [more info here](https://github.com/SourcePointUSA/android-cmp-app#the-onaction-callback) (#433)
* [SP-8225](https://sourcepoint.atlassian.net/browse/SP-8225) Updating section PubData in the readme file, [more info here](https://github.com/SourcePointUSA/android-cmp-app#pubData) (#433)
* [SP-8210](https://sourcepoint.atlassian.net/browse/SP-8210) Enabling the gdprApplies and gdprApplies functions (#431)
* [SP-8118](https://sourcepoint.atlassian.net/browse/SP-8118) New PubData api in V6 (#430)

## 6.2.3 (October, 22, 2021)
* [SP-8056](https://sourcepoint.atlassian.net/browse/SP-8056) Granted flag for vendors (#428). More info [here](https://github.com/SourcePointUSA/android-cmp-app#the-spconsent-object)
* [SP-7933](https://sourcepoint.atlassian.net/browse/SP-7933) Targeting parameters as JSONObject not as a String (#426)
* [SP-7926](https://sourcepoint.atlassian.net/browse/SP-7926) US Privacy String Set to "" when no message fires (#427)
* [Sp-7840](https://sourcepoint.atlassian.net/browse/SP-7840) New CustomAction type in PM (#425)
* [SP-6821](https://sourcepoint.atlassian.net/browse/SP-6821) Refactor and add support to NativeMessage for both CCPA and GDPR (#416)
* [SP-7969](https://sourcepoint.atlassian.net/browse/SP-7969) Metaapp - Switch to Android App Bundle (#422)

## 6.2.2 (September, 24, 2021)
* [SP-7925](https://sourcepoint.atlassian.net/browse/SP-7925) Adding a back behaviour to the dismiss action (#420)

## 6.2.1 (August, 31, 2021)
* [SP-7820](https://sourcepoint.atlassian.net/browse/SP-7820) IABTCF_TCString missing in the preferences (#412)
* [SP-7742](https://sourcepoint.atlassian.net/browse/SP-7742) Add acceptedCategories to GDPR userConsent (#410)
* [SP-7678](https://sourcepoint.atlassian.net/browse/SP-7678) UI regression tests improvement  (#409)
* [SP-7688](https://sourcepoint.atlassian.net/browse/SP-7688) Revert "[SP-7618] Consent flag for pm (#406)" (#408)

## 6.2.0 (August, 06, 2021)
* [SP-7618](https://sourcepoint.atlassian.net/browse/SP-7618) New client API that adds a consent flag for the Privacy Manager (#406)
* [SP-7552](https://sourcepoint.atlassian.net/browse/SP-7552) Inform the user that a new version is available (#404)

## 6.1.4 (July, 30, 2021)
* [SP-7573](https://sourcepoint.atlassian.net/browse/SP-7573) Support for sharing unix process (#401)

## 6.1.3 (July, 23, 2021)
* [SP-7579](https://sourcepoint.atlassian.net/browse/SP-7579) Fix postMessage error message (#398)
* [SP-7575](https://sourcepoint.atlassian.net/browse/SP-7575) Missing getOrDefault api on Android 6 (#397)
* [SP-7574](https://sourcepoint.atlassian.net/browse/SP-7574) Sdk setup instruction (#396)

## 6.1.2 (July, 16, 2021)
* [SP-7510](https://sourcepoint.atlassian.net/browse/SP-7510) Allowing the user to share a set of selected logs (#393)
* [SP-7084](https://sourcepoint.atlassian.net/browse/SP-7084) Ui regression test for authId consent (#392)
* [SP-7467](https://sourcepoint.atlassian.net/browse/SP-7467) Writing a regression test case for the deep links
* [SP-7481](https://sourcepoint.atlassian.net/browse/SP-7481) Fix query parameter in the CCPA PM URL
* [SP-7462](https://sourcepoint.atlassian.net/browse/SP-7462) Add table of contents to readme  (#389)

## 6.1.1 (July, 02, 2021)
* [SP-7443](https://sourcepoint.atlassian.net/browse/SP-7443) Add group PM param as query parameter (#387)

## 6.1.0 (July, 01, 2021)
* [SP-7430](https://sourcepoint.atlassian.net/browse/SP-7430) Update Metaapp Play Store info (#385)
* [SP-7395](https://sourcepoint.atlassian.net/browse/SP-7395) Renamed campaignEnv to campaignsEnv (#382)

## 6.0.5 (June, 30, 2021)
* [SP-7395](https://sourcepoint.atlassian.net/browse/SP-7395) Move campaignEnv to the top level request body (#382)
* [SP-7416](https://sourcepoint.atlassian.net/browse/SP-7416) Metaapp release build gradle (#381)
* [SP-7406](https://sourcepoint.atlassian.net/browse/SP-7406) Bring GDPR / CCPA consent uuid back to UserData (#380)
* [SP-7378](https://sourcepoint.atlassian.net/browse/SP-7378) Lack of TCData in the user consent (#378)
* [SP-7337](https://sourcepoint.atlassian.net/browse/SP-7337) Added jsonviewer screen (#377)
* [SP-7285](https://sourcepoint.atlassian.net/browse/SP-7285) logging view meta (#375)
* [SP-7315](https://sourcepoint.atlassian.net/browse/SP-7315) Authenticated consent not passing onConsentReady() callback for the new SDk (#376)
* [SP-7272](https://sourcepoint.atlassian.net/browse/SP-7272) Turn campaign env into a targeting parameter  (#374)
* [SP-7273](https://sourcepoint.atlassian.net/browse/SP-7273) Updated CCPA pm host (#373)
* [SP-7229](https://sourcepoint.atlassian.net/browse/SP-7229) Add activity demo (#372)
* [Sp-7228](https://sourcepoint.atlassian.net/browse/Sp-7228) Viewmodel add property (#371)
* [SP-7222](https://sourcepoint.atlassian.net/browse/SP-7222) Viewmodel + businesses logic property list (#370)
* [SP-7168](https://sourcepoint.atlassian.net/browse/SP-7168) Add new property screen (#365)
* [SP-7212](https://sourcepoint.atlassian.net/browse/SP-7212) Add localProd gradle config (#368)

## 6.0.4 (June, 03, 2021)
* [SP-7188](https://sourcepoint.atlassian.net/browse/SP-7188) no onConsentReady() response after second opening of app (#364)
* [SP-7145](https://sourcepoint.atlassian.net/browse/SP-7145) Metaapp Property list (#363)
* [SP-7140](https://sourcepoint.atlassian.net/browse/SP-7140) Components for production tests (#362)
* [SP-7130](https://sourcepoint.atlassian.net/browse/SP-7130) Metaapp LocalDatasource with SQLDelight (#361)
* [SP-7125](https://sourcepoint.atlassian.net/browse/SP-7125) Proguard rules (#360)
* [SP-7119](https://sourcepoint.atlassian.net/browse/SP-7119) Docs for campaignEnv (#359)
* [SP-7054](https://sourcepoint.atlassian.net/browse/SP-7054) Metaapp base config (#358)

## 6.0.3 (May, 21, 2021)
* [SP-7094] OkHttp timeout configurable from builder [#355](https://sourcepoint.atlassian.net/browse/SP-7094)
* [SP-7090] Upgraded android tools to v 4.1.3 [#354](https://sourcepoint.atlassian.net/browse/SP-7090)
* [SP-7068] Improving ui-tests stability [#353](https://sourcepoint.atlassian.net/browse/SP-7068)
* [SP-7061] Configurable timeout [#352](https://sourcepoint.atlassian.net/browse/SP-7061)
* [SP-7006] Wrap of customConsentGDPR feature for Unity3d [#349](https://sourcepoint.atlassian.net/browse/SP-7006)
* [SP-7053] Kotlin upgrade to v1.5.0 [#351](https://sourcepoint.atlassian.net/browse/SP-7053)
* [SP-6963] CampaignManager unit-tests [#350](https://sourcepoint.atlassian.net/browse/SP-6963)
* [SP-6968] MessageModelReqExt unit-tests [#348](https://sourcepoint.atlassian.net/browse/SP-6968)
* [SP-6967] ConsentRespExt unit-tests [#347](https://sourcepoint.atlassian.net/browse/SP-6967)
* [SP-6964] ConsentManager unit-tests [#346](https://sourcepoint.atlassian.net/browse/SP-6964)
* [SP-6961] Service unit-tests [#345](https://sourcepoint.atlassian.net/browse/SP-6961)
* [SP-6972] Ui tests for the multi-campaign flow [#344](https://sourcepoint.atlassian.net/browse/SP-6972)
* [SP-6970] UI tests for the first layer message [#343](https://sourcepoint.atlassian.net/browse/SP-6970)
* [SP-6960] JsonConverter tests [#342](https://sourcepoint.atlassian.net/browse/SP-6960)

## 6.0.2 (May, 07, 2021)
* Improved test coverage
* Fix unity compatibility issue

## 6.0.1 (May, 04, 2021)
* PmTab and MessageLanguage variables have been moved inside the SpConfig object

## 6.0.0 (April, 30, 2021)
* Multi-campaigns feature implemented
* Codebase 100% kotlin
* Kotlin - Lazy delegate + DSL for creation of the Cmp SDK object
* Renaming method for PM from `run()` to `loadPrivacyManager()`
* Java - New builder for creation of the Cmp SDK object
* Improving Memory footprint reusing the WebViews as much as possible.
* Added `dispose()` method to release the resources after the `onDestroy` callback gets called
* Added client object to receive the events from the Cmp SDK
* Removed all SAM interfaces with the same name `run()`
* Fix issue - Multiple consent layers on top of each other [#330](https://github.com/SourcePointUSA/android-cmp-app/issues/330)
* Complete refactor of the architecture to enhance its maintainability and testability

## 5.3.13 (March, 15, 2021)
* Removed all the bintray dependencies [#326](https://github.com/SourcePointUSA/android-cmp-app/pull/326)

## 5.3.12 (January, 21, 2021)
* Added a feature to overwrite default privacy manager tab. You can now configure tab which loads with privacy manager by using the method `.setPrivacyManagerTab` in the builder. [#311](https://github.com/SourcePointUSA/android-cmp-app/pull/311)
* Added a new NativeMessage example to make the easier usage of the native message capability. [#315](https://github.com/SourcePointUSA/android-cmp-app/pull/315)
* Refactoring of error classes with addition of error codes. [#303](https://github.com/SourcePointUSA/android-cmp-app/pull/303)

## 5.3.11 (December, 9, 2020)
* Added a feature that allow the developer to override the default message language.

## 5.3.10 (November, 23, 2020)
* Added a feature to ease sharing consent between native and webview. For more information on this one, check our README. #289
* Fixed an issue that would prevent the Privacy Manager from showing the default tab set on the _Show Options_ action button. #269
* Improved support to OTT. #277
* Fixed an issue that caused the `consentLanguage` on the consent string to always be set to `EN` #279
* Reduced memory footprint by instantiating the WebView only when needed #281
* Removed dependency on `ConstraingLayout`, making the project AndroidX friendly #284

## 5.3.9 (September, 9, 2020)
* add `setOnNoIntentActivitiesFound(String intentUrl)` to avoid crashing when no url intent handler is found.
* prepare for the new  `pubData` feature. Ability to set `Set<Object> pubData` on the `ConsentAction` obj before sending consent:
```java
.setOnBeforeSendingConsent((consentAction, consentHandler) -> {
  consentAction.setPubData(Collections.singletonMap("foo", "bar"));
  consentHandler.post(consentAction);
})
```

## 5.3.8 (August, 26, 2020)
* fix parsing error on native msg causing app crash #249
* fix onError called twice on some cases #246
* fix network exceptions with different structures on different cases #235
* fix `ConsentLibException.BuildException` not used in code #187

## 5.3.7 (August, 24, 2020)
* fix syntax error on JSReceiver in order to prevent webview JS error on api 23 and bellow

## 5.3.6 (August, 17, 2020)
* fix VendorGrants returned from customConsentTo do not reflecting new data #221 #230

## 5.3.5 (August, 17, 2020)
* fix error when receiving unexpected messageEvent type from JSReceiver

## 5.3.4 (August, 14, 2020)
* fix JS error being logged to console + nice and cozy JS code refactoring :100: #223
* remove deprecated `.setInternalStage(Boolean)` from builder

## 5.3.3 (August, 03, 2020)
* change customConsentTo to Collection instead of ArrayList  #216
* add static method StoreClient.getUserConsent() for getting cached user consents obj #225
* fix webview memory leak from not calling android.Webview.destruct() #223

## 5.3.2 (July, 22, 2020)
* fix choiceId passed as Number from JSReceiver (now aways passed as String)

## 5.3.1 (July, 10, 2020)
* revert override of back button behaviour on ConsentWebview

## 5.3.0 (July, 09, 2020)
Bye bye activity reference 👋
Now the callbacks are being posted to the main event looper instead being fired via `Activity.runOnUIThread(Runnable r)`
Even though it's a minor bump up we took care to not change the public api (except for the `releaseActivity()` method) nor the SDKs behaviour.

* Refactor ConsentLib to decouple from activity
* `releaseActivity()` public method taken out of the public api :warning:
* potential fix to app crash on webview build due to null context/activity obj

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
