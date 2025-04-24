function isFromPM(payload) {
    return window.spLegislation == "PREFERENCES" || payload.fromPM || (payload.settings && payload.settings.vendorList != null);
}

function getQueryParam(paramName, url) {
    var query = url && url.split && url.split("?")[1] || "";
    var params = query.split("&");
    for (i = 0; i < params.length; i++) {
        var pair = params[i].split("=");
        if(pair[0] === paramName) {
            return pair[1];
        }
    }
    return null;
}

function actionFromMessage(payload) {
    var actionPayload = payload.actions && payload.actions.length && payload.actions[0] && payload.actions[0].data ? payload.actions[0].data : {};
    return {
        messageId: payload.messageId,
        campaignType: window.spLegislation,
        actionType: actionPayload.type,
        requestFromPm: false,
        pmUrl: actionPayload.iframe_url,
        saveAndExitVariables: "{}",
        consentLanguage: payload.consentLanguage,
        customActionId: actionPayload.customAction
    };
}

function actionFromPM(payload) {
    return {
        messageId: payload.messageId,
        campaignType: window.spLegislation,
        actionType: payload.actionType,
        requestFromPm: true,
        pmUrl: payload.iframe_url,
        saveAndExitVariables: JSON.stringify(Object.assign({}, payload.payload)),
        consentLanguage: payload.consentLanguage,
        customActionId: payload.customAction
    };
}

function actionData(payload) {
    var data = isFromPM(payload) ? actionFromPM(payload) : actionFromMessage(payload);
    return data;
}

function notImplemented(callbackName) {
    return function () {
        console.error(callbackName + ' not implemented');
    }
}

function handleEvent(event) {
    console.log("message event:", JSON.stringify(event.data));
    var payload = event.data;
    var sdk = window.JSReceiver || {
        readyForMessagePreload: notImplemented('readyForMessagePreload'),
        readyForConsentPreload: notImplemented('readyForConsentPreload'),
        loaded: notImplemented('onConsentUIReady'),
        onAction: notImplemented('onAction'),
        onError: notImplemented('onError'),
        log: notImplemented('log'),
    };
    try {
        switch (payload.name) {
            case 'sp.loadMessage': break;
            case 'sp.loadConsent': break;
            case 'sp.showMessage':
                sdk.loaded();
                break;
            case 'sp.hideMessage':
                sdk.onAction(JSON.stringify(actionData(payload)));
                break;
            case 'sp.renderingAppError':
                sdk.onError(JSON.stringify(payload));
                break;
            case 'sp.readyForPreload':
                sdk.readyForMessagePreload();
                break;
            case 'sp.readyForPreloadConsent':
                sdk.readyForConsentPreload();
                break;
            default:
                sdk.log("Unexpected event name: " + JSON.stringify(payload));
        }
    } catch (err) {
        sdk.onError(err.stack);
    }
}

window.addEventListener('message', handleEvent);
