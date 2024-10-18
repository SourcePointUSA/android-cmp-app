## 7.11.0-rc2 (October, 18, 2024)
* [DIA-4611](https://sourcepoint.atlassian.net/browse/DIA-4611) Refactor `customConsentGDPR` and `deleteCustomConsentTo` to use `mobile-core`'s implementation. [#836](https://github.com/SourcePointUSA/android-cmp-app/pull/836)
* Updated project dependencies and bump `minSdk` support from 21 to 23 due to [Android's new Security policy](https://developer.android.com/about/versions/14/behavior-changes-all#security) [#840](https://github.com/SourcePointUSA/android-cmp-app/pull/831)
* Bump `compileSdk` and `targetSdk` to 35
* Fixed an issue preventing the MetaApp from being released at Google Play

## 7.11.0-rc1 (October, 18, 2024)
* [DIA-4611](https://sourcepoint.atlassian.net/browse/DIA-4611) Refactor `customConsentGDPR` and `deleteCustomConsentTo` to use `mobile-core`'s implementation. [#836](https://github.com/SourcePointUSA/android-cmp-app/pull/836)
* Updated project dependencies and bump `minSdk` support from 21 to 23 due to [Android's new Security policy](https://developer.android.com/about/versions/14/behavior-changes-all#security) [#840](https://github.com/SourcePointUSA/android-cmp-app/pull/831)
* Fixed an issue preventing the MetaApp from being released at Google Play

## 7.10.2 (September, 30, 2024)
* [DIA-4092](https://sourcepoint.atlassian.net/browse/DIA-4092) Fixed an issue preventing the cancel button on USNat messages from dismissing the consent layer. [#833](https://github.com/SourcePointUSA/android-cmp-app/pull/833)
* [DIA-4456](https://sourcepoint.atlassian.net/browse/DIA-4456) Refactored network client to use [mobile-core](https://github.com/SourcePointUSA/mobile-core/) `/meta-data` implementation. [#835](https://github.com/SourcePointUSA/android-cmp-app/pull/835)
* Updated project dependencies and plugins [#831](https://github.com/SourcePointUSA/android-cmp-app/pull/831)
* Fixed an issue preventing the MetaApp from launching

## 7.10.1 (August, 27, 2024)
* [DIA-3942](https://sourcepoint.atlassian.net/browse/DIA-3942) Update all dependencies and Kotlin from 1.6 to 1.9.24. [#830](https://github.com/SourcePointUSA/android-cmp-app/pull/830)
* Updated sample apps dependencies and project Kotlin version to 1.9.25 [#831](https://github.com/SourcePointUSA/android-cmp-app/pull/831)

## 7.10.0 (August, 13, 2024)
* [DIA-3574](https://sourcepoint.atlassian.net/browse/DIA-3574) Update all dependencies and Kotlin from 1.6 to 1.9.24. [#808](https://github.com/SourcePointUSA/android-cmp-app/pull/808)

## 7.9.0 (August, 07, 2024)
* [DIA-4323](https://sourcepoint.atlassian.net/browse/DIA-4323) New feature enabling developers to programmatically reject all for a given user.[#828](https://github.com/SourcePointUSA/android-cmp-app/pull/828)
* [DIA-3891](https://sourcepoint.atlassian.net/browse/DIA-3891) Handle Accept/Reject all actions from USNat messages.[#829](https://github.com/SourcePointUSA/android-cmp-app/pull/829)
* [DIA-4258](https://sourcepoint.atlassian.net/browse/DIA-4258) Add an example on how to deal with device rotations without closing the consent UI.

## 7.8.5 (July, 10, 2024)
* [DIA-4254](https://sourcepoint.atlassian.net/browse/DIA-4254) Fixed an preventing users from being sampled when calling the pv-data endpoint [#826](https://github.com/SourcePointUSA/android-cmp-app/pull/826)

## 7.8.4 (June, 19, 2024)
* [DIA-4112](https://sourcepoint.atlassian.net/browse/DIA-4112) Fixed an issue preventing links from being opened on external browser [#825](https://github.com/SourcePointUSA/android-cmp-app/pull/825)
* [HCD-525](https://sourcepoint.atlassian.net/browse/HCD-525) Improve documentation on authenticated consent [#823](https://github.com/SourcePointUSA/android-cmp-app/pull/823)

## 7.8.3 (June, 05, 2024)
* [DIA-3945](https://sourcepoint.atlassian.net/browse/DIA-3945) Added Tagalog as message language [#819](https://github.com/SourcePointUSA/android-cmp-app/pull/819)
* [DIA-3946](https://sourcepoint.atlassian.net/browse/DIA-3946) Fixed an issue causing consent uuid to be null for server-sided sampled properties [#820](https://github.com/SourcePointUSA/android-cmp-app/pull/820)
* [DIA-4086](https://sourcepoint.atlassian.net/browse/DIA-4086) Fixed an issue affecting users taking consent action when legislation applies is false [#822](https://github.com/SourcePointUSA/android-cmp-app/pull/822)

## 7.8.2 (May, 02, 2024)
* [DIA-3785](https://sourcepoint.atlassian.net/browse/DIA-3785) Fix an issue preventing USNat consent data from being stored after PM save & exit [#805](https://github.com/SourcePointUSA/android-cmp-app/pull/805)
* Fix an issue causing the SDK to show wrong translations in some scenarios. [#809](https://github.com/SourcePointUSA/android-cmp-app/pull/809)
* [DIA-2066](https://sourcepoint.atlassian.net/browse/DIA-2066) Improve experience of the back button on AndroidTV [#715](https://github.com/SourcePointUSA/android-cmp-app/pull/715)
* [HCD-496](https://sourcepoint.atlassian.net/browse/HCD-496) Improve documentation around MSPS consent status [#802](https://github.com/SourcePointUSA/android-cmp-app/pull/802)
* [HCD-502](https://sourcepoint.atlassian.net/browse/HCD-502) Improve documentation regarding supporting US Privacy String along USNat campaigns [#806](https://github.com/SourcePointUSA/android-cmp-app/pull/806)
* Internal code cleanup and dependencies updates from SDK and Example apps dependencies [#796](https://github.com/SourcePointUSA/android-cmp-app/pull/796), [#798](https://github.com/SourcePointUSA/android-cmp-app/pull/798), [#803](https://github.com/SourcePointUSA/android-cmp-app/pull/803), [#811](https://github.com/SourcePointUSA/android-cmp-app/pull/811), [#814](https://github.com/SourcePointUSA/android-cmp-app/pull/814)

## 7.7.1 (February, 22, 2024)
* [DIA-3622](https://sourcepoint.atlassian.net/browse/DIA-3622) GCM flag missing (#787)
* [DIA-3505](https://sourcepoint.atlassian.net/browse/DIA-3505) Handle no internet edge cases (#782)
* [DIA-3623](https://sourcepoint.atlassian.net/browse/DIA-3623) Add clarity to googleConsentMode object return values (#789)
* [DIA-3624](https://sourcepoint.atlassian.net/browse/DIA-3624) Load test data into the Metaapp by parsing a JSON obj (#788)
* [HCD-489](https://sourcepoint.atlassian.net/browse/DIA-489) Update GCM 2.0 (#786)
* [DIA-2585](https://sourcepoint.atlassian.net/browse/DIA-2585) Added overload of the `deleteCustomConsentTo` method for Unity3D feature support (#779)

## 7.7.0 (February, 08, 2024)
* [DIA-3566](https://sourcepoint.atlassian.net/browse/DIA-3566) Support non-TCF GCM 2.0 (#783)
* [DIA-3425](https://sourcepoint.atlassian.net/browse/DIA-3425) Add ethernet capability (#780)
* [DIA-2024](https://sourcepoint.atlassian.net/browse/DIA-2024) Ship ProGuard rules with the release AAR (#771)

## 7.6.1 (January, 29, 2024)
* [DIA-3366](https://sourcepoint.atlassian.net/browse/DIA-3366) Fix AuthId behaviour (#775)

## 7.6.0 (January, 18, 2024)
* [DIA-2031](https://sourcepoint.atlassian.net/browse/DIA-2031) Support to USNat (#736)
* [DIA-3168](https://sourcepoint.atlassian.net/browse/DIA-3168) Refactoring of SPGDPRConsent.acceptedCategories (#736)
* [DIA-3263](https://sourcepoint.atlassian.net/browse/DIA-3263) Fix OnConsentReady and OnConsentError (#736)
* [DIA-3068](https://sourcepoint.atlassian.net/browse/DIA-3068) Fix drop in page views (#736)

## 7.5.2 (December, 01, 2023)
* [DIA-3183](https://sourcepoint.atlassian.net/browse/DIA-3183) Re-add localstate as query param in the messages url (#758)

## 7.5.1 (November, 22, 2023)
* [DIA-2759](https://sourcepoint.atlassian.net/browse/DIA-2759) Regression test for consent UUID null fix (#751)
* [DIA-2836](https://sourcepoint.atlassian.net/browse/DIA-2836) Regression test for the GDPR applies fix (#740)

## 7.5.0 (November, 09, 2023)
* [DIA-2940](https://sourcepoint.atlassian.net/browse/DIA-2940) Fix for the applies parameter after saving partial GDPR consent (#734)
* [DIA-2786](https://sourcepoint.atlassian.net/browse/DIA-2786) Added support fo the new CCPA OTT PM  (#735)
* [HCD-452](https://sourcepoint.atlassian.net/browse/DIA-452) Add full structure to SpConsent (#733)

## 7.4.4 (November, 02, 2023)
* [DIA-2896](https://sourcepoint.atlassian.net/browse/DIA-2896) Implement consent expiration (#731)
* [DIA-2654](https://sourcepoint.atlassian.net/browse/DIA-2654) Flush data if authId or propertyId changes (#711)

## 7.4.3 (October, 27, 2023)
* [DIA-2886](https://sourcepoint.atlassian.net/browse/DIA-2886) Fix Gdpr applies (#729)

## 7.4.2 (October, 25, 2023)
* [DIA-2918](https://sourcepoint.atlassian.net/browse/DIA-2918) Fix ConsentStatus missing (#726)

## 7.4.1 (October, 24, 2023)
* [DIA-2542](https://sourcepoint.atlassian.net/browse/DIA-2542) Local data versioning (#712)
* [DIA-2757](https://sourcepoint.atlassian.net/browse/DIA-2757) UI test for the applies condition (#714)
* [DIA-2901](https://sourcepoint.atlassian.net/browse/DIA-2901) onConsentReady Fires GDPR Applies False After Saving Partial Consent (#714)
* [DIA-2851](https://sourcepoint.atlassian.net/browse/DIA-2851) Automation test for  the no internet exception (#713)

## 7.4.0 (October, 06, 2023)
* [DIA-2753] 400 Bad request when Passing authId (#702)
* [DIA-2824] App crashes at second message load (#702)
* [DIA-2784] Using an AuthId the pop up is always visualised  (#702)
* [DIA-2840] Reformat and deprecation fix for manifest files (#702)
* [DIA-2819] Fix of the migration from v6 to v7 (#699)
* [DIA-2753] 404 from consent-status
* [DIA-2555] Refactor network client (#677)
* [DIA-2561] Crash fix for NPE in SPConsentLib (#679)
* [DIA-2390] Store GPPData in the local storage (#680)
* [DIA-2479] Allow user to set GPP Config (#683)
* [DIA-2753] 404 from consent-status
* [DIA-2710] Doc for GPP solution (#697)
* [DIA-2628] Needs to handle no-internet better (#696)
* [DIA-2646] Fix for GDPR applies (#698)

## 7.3.0 (September, 04, 2023)
* [DIA-2542](https://sourcepoint.atlassian.net/browse/DIA-2542) Local data versioning (#685)
* [DIA-2578](https://sourcepoint.atlassian.net/browse/DIA-2578) Fix error 400 when calling consent status (#686)
* [DIA-2479](https://sourcepoint.atlassian.net/browse/DIA-2479) Allow user to set GPP Config (#683)
* [DIA-2390](https://sourcepoint.atlassian.net/browse/DIA-2390) Store GPPData in the local storage (#680)

## 7.2.8 (August, 17, 2023)
* [DIA-2561](https://sourcepoint.atlassian.net/browse/DIA-2561) Crash fix for NPE in SPConsentLib (#679)
* [DIA-2525](https://sourcepoint.atlassian.net/browse/DIA-2525) Fix no action wrong numbers in our reports (#678)
* [DIA-2555](https://sourcepoint.atlassian.net/browse/DIA-2555) Refactor network client (#677)

## 7.2.7 (August, 09, 2023)
* [DIA-2531](https://sourcepoint.atlassian.net/browse/DIA-2531) Refactor metadata param for each request (#675)
* [DIA-2462](https://sourcepoint.atlassian.net/browse/DIA-2462) Review params to GET /consent-status endpoint (#673)
* [DIA-1961](https://sourcepoint.atlassian.net/browse/DIA-1961) Extended UnitySpClient for actual Unity port (#669)
* [DIA-2460](https://sourcepoint.atlassian.net/browse/DIA-2460) Review params to GET /choice endpoint (#672)
* [DIA-2458](https://sourcepoint.atlassian.net/browse/DIA-2458) Review params to GET /messages endpoint (#671)
* [DIA-2447](https://sourcepoint.atlassian.net/browse/DIA-2447) Update project dependencies (#668)
* [DIA-2449](https://sourcepoint.atlassian.net/browse/DIA-2449) Not call onError if pv-data fails (#667)
* [DIA-2452](https://sourcepoint.atlassian.net/browse/DIA-2452) Remove unnecessary query params from meta-data (#666)

## 7.2.6 (July, 13, 2023)
* [DIA-2263](https://sourcepoint.atlassian.net/browse/DIA-2263) Create confluence page for the SDK release process (#663)
* [DIA-2360](https://sourcepoint.atlassian.net/browse/DIA-2360) Fix message showing multiple times on reconsent flow (#659)
* [DIA-2353](https://sourcepoint.atlassian.net/browse/DIA-2353) Document consent transfer API (#662)

## 7.2.5 (July, 10, 2023)
* [DIA-2198](https://sourcepoint.atlassian.net/browse/DIA-2198) Manipulate CCPA consent string based on applies (#658)
* [DIA-2243](https://sourcepoint.atlassian.net/browse/DIA-2243) Add tests to verify uspstring is being updated in the DataStorage (#656)
* [DIA-2151](https://sourcepoint.atlassian.net/browse/DIA-2151) Removed singleShotPM from ConsentActionImpl (#657)
* [DIA-2273](https://sourcepoint.atlassian.net/browse/DIA-2273) Removed actions var from GDPR (#652)

## 7.2.4 (June, 23, 2023)
* [DIA-1806](https://sourcepoint.atlassian.net/browse/DIA-1806) Implement consent transfer to Webview (#649)
* [DIA-2301](https://sourcepoint.atlassian.net/browse/DIA-2301) Fix for NULL consent UUIDs (#651)
* [DIA-2244](https://sourcepoint.atlassian.net/browse/DIA-2244) Remove unused error codes (#650)

## 7.2.3 (June, 02, 2023)
* [DIA-2188](https://sourcepoint.atlassian.net/browse/DIA-2188) Fix reconsent message not showing (#644)
* [DIA-2196](https://sourcepoint.atlassian.net/browse/DIA-2196) Fix sdk throwing error on CCPA campaign only (#644)
* [DIA-2198](https://sourcepoint.atlassian.net/browse/DIA-2198) Manipulate CCPA String (#643)
* [DIA-2210](https://sourcepoint.atlassian.net/browse/DIA-2210) Create a regression test for DIA-2106 (#641)

## 7.2.2 (May, 19, 2023)
* [DIA-2181](https://sourcepoint.atlassian.net/browse/DIA-2181) Fix for onAction callback to send pubData (#639)
* [DIA-1936](https://sourcepoint.atlassian.net/browse/DIA-1936) Support Group Pm Id (#640)
* [DIA-2106](https://sourcepoint.atlassian.net/browse/DIA-2106) Make ccpa uuid public on CCPAConsent

## 7.2.1 (May, 10, 2023)
* [DIA-2119](https://sourcepoint.atlassian.net/browse/DIA-2119) Wait on pv-data before sending POST choice request (#633)
* [DIA-2121](https://sourcepoint.atlassian.net/browse/DIA-2121) Added ViewManagerException (#632)

## 7.2.0 (May, 03, 2023)
* [DIA-2134](https://sourcepoint.atlassian.net/browse/DIA-2134) Trigger the timeout for RenderingApp from the onConsentReady callback (#630)
* [DIA-2135](https://sourcepoint.atlassian.net/browse/DIA-2135) Enum with error codes for timeout and invalid response (#629)
* [DIA-1988](https://sourcepoint.atlassian.net/browse/DIA-1988) Call the custom-metrics for incoming errors from the RenderingApp. (#628)
* [DIA-1981](https://sourcepoint.atlassian.net/browse/DIA-1981) Write a more meaningful message for the timeout exception (#627)
* [DIA-2102](https://sourcepoint.atlassian.net/browse/DIA-2102) Added `scriptVersion` and `scriptType` to  all `Url`s and logger requests (#626)
* [DIA-2104](https://sourcepoint.atlassian.net/browse/DIA-2104) Metaapptv Add a button to layout the CCPA Pm (#625)
* [DIA-1990](https://sourcepoint.atlassian.net/browse/DIA-1990) Preloading always enabled (#623)
* [DIA-2055](https://sourcepoint.atlassian.net/browse/DIA-2055) Add custom consent for the preloading feature (#624)
* [DIA-1792](https://sourcepoint.atlassian.net/browse/DIA-1792) Upgraded okHttp lib to 4.9.0 (#588)
* [DIA-1730](https://sourcepoint.atlassian.net/browse/DIA-1730) Authid fix (#622)
* [DIA-1949](https://sourcepoint.atlassian.net/browse/DIA-1949) Fix campaign env parameter (#620)

## 7.1.1 (March, 28, 2023)
* [DIA-1956](https://sourcepoint.atlassian.net/browse/DIA-1956) MsgId in PvData only when json_msg is available (#618)
* [DIA-1970](https://sourcepoint.atlassian.net/browse/DIA-1970) Typo in MessageLanguage (#617)
* [DIA-1876](https://sourcepoint.atlassian.net/browse/DIA-1876) Regressions tests for the preloading feature (#616)
* [DIA-1914](https://sourcepoint.atlassian.net/browse/DIA-1914) Localstate query param (#613)
* [DIA-1929](https://sourcepoint.atlassian.net/browse/DIA-1929) Delete the  test parameter from the network calls (#614)
* [DIA-1877](https://sourcepoint.atlassian.net/browse/DIA-1877) Add a switch btn (enable/disable preloading) to the Metaapp (#612)
* [DIA-1835](https://sourcepoint.atlassian.net/browse/DIA-1835) Cleaning code (#609)

## 7.1.0 (March, 07, 2023)
* [DIA-1808](https://sourcepoint.atlassian.net/browse/DIA-1808) PM's consent preloading (#592), [more info here](https://github.com/SourcePointUSA/android-cmp-app#Preloading)

## 7.0.6 (March, 03, 2023)
* [DIA-1854](https://sourcepoint.atlassian.net/browse/DIA-1854) Fix crash Datetime class on Android 6/7 (#607)

## 7.0.5 (February, 27, 2023)
* [DIA-1859](https://sourcepoint.atlassian.net/browse/DIA-1859) Add hasLocalData in the messages request parameters (#595)
* [DIA-1735](https://sourcepoint.atlassian.net/browse/DIA-1735) Add "linkedNoAction" to CCPA consent status (#594)
* [DIA-1810](https://sourcepoint.atlassian.net/browse/DIA-1810) Change the structure of the custom consent accordingly to the new consent DTO (#587)
* [DIA-1763](https://sourcepoint.atlassian.net/browse/DIA-1763) Metaapp - Add drop db feature (#586)

## 7.0.4 (February, 10, 2023)
*  [DIA-1826](https://sourcepoint.atlassian.net/browse/DIA-1826) Issue on the serialisation (#584)

## 7.0.3 (February, 08, 2023)
* [DIA-1725](https://sourcepoint.atlassian.net/browse/DIA-1725) Extract AcceptedCategories from the Grant object (#579)
* [DIA-1733](https://sourcepoint.atlassian.net/browse/DIA-1733) ConsentLanguage fix (#578)
* [DIA-1523](https://sourcepoint.atlassian.net/browse/DIA-1523) Switch from loadUrl to a different way of execution for JavaScript

## 7.0.2 (February, 03, 2023)
* [DIA-1781](https://sourcepoint.atlassian.net/browse/DIA-1781) Fix: Instant class crash (#576)
* [DIA-1742](https://sourcepoint.atlassian.net/browse/DIA-1742) Fix: empty consent in an edge case (#574)
* [DIA-1728](https://sourcepoint.atlassian.net/browse/DIA-1728) Update proguard rules on README.md.
* [DIA-1738](https://sourcepoint.atlassian.net/browse/DIA-1738) Fix: onActions SHOW_OPTIONS Action Type Does Not Fire

## 7.0.1 (January, 27, 2023)
* [DIA-1696](https://sourcepoint.atlassian.net/browse/DIA-1696) Fix IABTCF type (#569)

## 7.0.0 (December, 15, 2022)
* [DIA-740](https://sourcepoint.atlassian.net/browse/DIA-740) New SDK architecture which optimise the usage of all the resources involve in the process of fetching the consent. The new architecture bring only a new extra parameter from the client prospective, the  `propertyId`. More info about its usage in the [Migration Guide](https://github.com/SourcePointUSA/android-cmp-app/blob/develop/MIGRATION_GUIDE.md).

## 6.7.3 (November, 11, 2022)
* [DIA-1484](https://sourcepoint.atlassian.net/browse/DIA-1484)  Fix crash with payload.customAction during a PM action (#552)
* [DIA-1302](https://sourcepoint.atlassian.net/browse/DIA-1302) Issues with Resurfacing the OTT Privacy Manager (#550)
* [DIA-1368](https://sourcepoint.atlassian.net/browse/DIA-1368) sp.renderingAppError errors are not being listened by the SDK (#542)
* [DIA-1391](https://sourcepoint.atlassian.net/browse/DIA-1391) Add customActionId to pm action payload (#544)

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