# The Nativemessage
## Table of Contents
- [Intro](#intro)
- [Configure a property to use with the Nativemessage](#configure_a_property_to_use_with_the_nativemessage)
- [How to Install](#how-to-install)
- [The Lifecycle](#the_lifecycle)
    - [The Webmessage VS Nativemessage lifecycle](#the_webmessage_vs_nativemessage_lifecycle)
- [The `onNativeMessageReady` callback](#the_onnativemessageready_callback)
    - [The `MessageStructure` obj](#the_messagestructure_obj)
    - [The `NativeMessageController` obj](#the_nativemessagecontroller_obj)

## Intro
The `Nativemessage` feature let the client app to provide a native layout to present the consent text avoiding 
the usage of the Webview object.

## Configure a property to use with the Nativemessage
From our Message Builder, choose the `Native App` option to create the new native property and the press on `New Message`.
Now you can add you native fields.

![Get it on Google Play](art/nm_builder.png)

## How to Install
To install the SDK follow the instruction in the [main page](README.md#how-to-install).

## The Lifecycle

The Nativemessage lifecycle is similar to the one of the Webmessage, we have however some difference:
- After calling the `loadMessage`  function to get the consent message, only the `onNativeMessageReady` will be triggered.
The callback `onUIReady` is **not invoked** anymore.
- After taking an action, like pressing on `Accept All` button, the callbacks `onAction` and `onUIFinished` are not called anymore.
This because the client app now knows exactly when the action happened.
  
### The Webmessage VS Nativemessage lifecycle

This is a comparison of the two lifecycles during the `Accept all` action

| Web message      	| Native message         	| Event                                	|
|------------------	|------------------------	|--------------------------------------	|
| `onUIReady`      	| `onNativeMessageReady` 	| called after `loadMessage`           	|
| `onAction`       	|                        	| called after the button `Accept All` 	|
| `onUIFinished`   	|                        	| called after the button `Accept All` 	|
| `onConsentReady` 	| `onConsentReady`       	| called after the button `Accept All` 	|
| `onSpFinish`     	| `onSpFinish`           	| called after the button `Accept All` 	|

## The `onNativeMessageReady` callback
After the `loadMessage` is triggered, the cmp SDK will give back the consent configuration created with the cmp web Builder.
This callback has two parameters:
- MessageStructure
- NativeMessageController

### The `MessageStructure` obj

The MessageStructure object contains all the info related to current consent like the title, the body, the actions, etc... 
The actions are listed in a `List<NativeAction>` collection where each item contains the action type, its style and other useful info,
following the `MessageStructure` structure:

```
MessageStructure
    |-- messageComponents: MessageComponents?
    |   |-- name: String
    |   |-- title: NativeComponent?
    |   |-- body: NativeComponent?
    |   |-- actions: List<NativeAction>
    |   |-- customFields: Map<String, String>
    |-- campaignType: CampaignType
```

```
NativeAction
    |-- text: String
    |-- style: NativeStyle
    |-- customField: Map<String, String>
    |-- choiceType: NativeMessageActionType
    |-- legislation: CampaignType
```

```
NativeStyle
    |-- fontFamily: String
    |-- fontWeight: Float
    |-- fontSize: Float
    |-- color: String?
    |-- backgroundColor: String
```

### The `NativeMessageController` obj

```
NativeMessageController
    |-- sendConsent(NativeMessageActionType, CampaignType)
    |-- showOptionNativeMessage(CampaignType, pmId: String)
    |-- fun removeNativeView(View)
    |-- fun showNativeView(View)
```
