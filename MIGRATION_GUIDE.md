# Migrate to v6 (Unified SDK)

In this guide we will cover how to migrate your app to the latest version of Sourcepoint's SDK (v6).

## Upgrade library in project's build.gradle file

Navigate to your build.gradle file and upgrade the `cmplibrary`:

**v6 (Unified SDK)**
```
'com.sourcepoint.cmplibrary:cmplibrary:6.0.1-SNAPSHOT'
```

## Remove out of date code from project
With the change to v6 (Unified SDK) the following configurations are no longer used and can be safely removed from your project.

```java
//remove from project

    final static int accountId = 22;
    final static int propertyId = 7639;
    final static String propertyName = "tcfv2.mobile.webview";
    final static String pmId = "122058";

    private ViewGroup mainViewGroup;

    private void showView(View view) {
        if(view.getParent() == null){
            view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.bringToFront();
            view.requestLayout();
            mainViewGroup.addView(view);
        }
    }
    private void removeView(View view) {
        if(view.getParent() != null) mainViewGroup.removeView(view);
    }

    private GDPRConsentLib buildGDPRConsentLib() {
        return GDPRConsentLib.newBuilder(accountId, propertyName, propertyId, pmId,this)
                .setOnConsentUIReady(this::showView)
                .setOnAction(actionType  -> Log.i(TAG , "ActionType: " + actionType.toString()))
                .setOnConsentUIFinished(this::removeView)
                .setOnConsentReady(consent -> {
                    // at this point it's safe to initialise vendors
                    for (String line : consent.toString().split("\n"))
                        Log.i(TAG, line);
                })
                .setOnError(error -> Log.e(TAG, "Something went wrong"))
                .setAuthId(dataProvider.getValue().getAuthId())
                .build();
    }

private void showView(View view) {
        if(view.getParent() == null){
            view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.bringToFront();
            view.requestLayout();
            mainViewGroup.addView(view);
        }
    }

private void removeView(View view) {
        if(view.getParent() != null) mainViewGroup.removeView(view);
    }
```
## Create new _Config_ object
Use the data builder to obtain a configuration for v6 (Unified SDK). This contains your organization's account information and includes the type of campaigns that will be run on this property. This object will be called when you instantiate your CMP SDK.

```java
private final SpConfig spConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addCampaign(CampaignType.GDPR)
            .addCampaign(CampaignType.CCPA)
            .build();
```

## Retrieve CMP SDK instance
Create new library:
```
private SpConsentLib spConsentLib = null;
```
Add the following to `OnCreate`:
```java
spConsentLib = FactoryKt.makeConsentLib(
                spConfig,   // config object
                this,       // activity
                MessageLanguage.ENGLISH //or desired language
        );
```
## Delegate Methods
Previously, in order to receive events from the CMP SDK, you needed to provide multiple delegates/clients using the sets method available.

The v6 (Unified SDK) needs just one delegate which you need to implement and add it to you CMP instance.

```java
class LocalClient implements SpClient {
        @Override
        public void onMessageReady(@NotNull JSONObject message) { /* ... */ }
        @Override
        public void onError(@NotNull Throwable error){ /* ... */ }
        @Override
        public void onConsentReady(@NotNull SPConsents c){ /* ... */ }
        @Override
        public void onAction(@NotNull View view, @NotNull ActionType actionType) { /* ... */ }

				@Override
        public void onUIFinished(@NotNull View v) {
            spConsentLib.removeView(v); // remove the view consent
        }

        @Override
        public void onUIReady(@NotNull View v) {
            spConsentLib.showView(v);  // add the view consent
        }
    }

// set the client
spConsentLib.setSpClient(new LocalClient());
```
## Run the consent
Call `loadMessage` inside `onResume`

**Original Version**
```java
    protected void onResume() {
        super.onResume();
        buildGDPRConsentLib().run();
    }
```
**v6 (Unified SDK)**
```java
    @Override
    protected void onResume() {
        super.onResume();
        spConsentLib.loadMessage();
    }
```
## Release all resources
In order to make sure that all the resources are released after the activity `onDestroy` callback call, you need to execute this method when the activity gets destroyed
```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        spConsentLib.dispose();
    }
```
## App lifecycle
The Android CMP SDK does not modify the Activity lifecycle. This means that every time the `onDestroy` gets called  and the consent WebView is in the foreground, the WebView self gets removed (i.e. during a configuration change).

If you need to show the WebView consent after such an event,  you have to handle the change configuration on the client side.

## Load the privacy manager(s)
Replace `showPM` with the Privacy Managers that will be shown for each campaign

**Original version**
```java
findViewById(R.id.review_consents).setOnClickListener(_v -> buildGDPRConsentLib().showPm());
```
**v6 (Unified SDK)**
```java
        findViewById(R.id.review_consents_gdpr).setOnClickListener(_v ->
                spConsentLib.loadPrivacyManager(
                        "10000", //PM id
                        PMTab.PURPOSES, //Initial PM tab to open
                        CampaignType.GDPR //Campaign type
                ));
        findViewById(R.id.review_consents_ccpa).setOnClickListener(_v ->
                spConsentLib.loadPrivacyManager(
                        "20000",
                        PMTab.PURPOSES,
                        CampaignType.CCPA
                ));
```

# Summary
Below is a full example of the changes covered in this article:
```java
	private final SpConfig spConfig = new SpConfigDataBuilder()
            .addAccountId(22)
            .addPropertyName("mobile.multicampaign.demo")
            .addCampaign(CampaignType.GDPR)
            .addCampaign(CampaignType.CCPA)
            .build();

    private SpConsentLib spConsentLib = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spConsentLib = FactoryKt.makeConsentLib(
                spConfig,
                this,
                MessageLanguage.ENGLISH
        );
        spConsentLib.setSpClient(new LocalClient());
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
        public void onMessageReady(@NotNull JSONObject message) { /* ... */ }
        @Override
        public void onError(@NotNull Throwable error){ /* ... */ }
        @Override
        public void onConsentReady(@NotNull SPConsents c){ /* ... */ }
        @Override
        public void onAction(@NotNull View view, @NotNull ActionType actionType) { /* ... */ }
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
