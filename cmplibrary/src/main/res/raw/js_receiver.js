function isFromPM(payload) {
    return payload.fromPM || (payload.settings && payload.settings.vendorList != null);
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
        name: payload.name,
        actionType: actionPayload.type,
        choiceId: String(actionPayload.choice_id),
        requestFromPm: false,
        pmId: getQueryParam("message_id", actionPayload.iframe_url),
        pmTab: getQueryParam("pmTab", actionPayload.iframe_url),
        saveAndExitVariables: {},
        consentLanguage: payload.consentLanguage
    };
}

function actionFromPM(payload) {
    return {
        name: payload.name,
        actionType: payload.actionType,
        choiceId: null,
        requestFromPm: true,
        pmId: null,
        pmTab: null,
        saveAndExitVariables: payload.payload,
        consentLanguage: payload.consentLanguage
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
    var payload = event.data;
    var sdk = window.JSReceiver || {
        onConsentUIReady: notImplemented('onConsentUIReady'),
        onAction: notImplemented('onAction'),
        onError: notImplemented('onError'),
        log: notImplemented('log')
    };
    try {
        sdk.log(JSON.stringify(payload));
        switch (payload.name) {
            case 'sp.showMessage':
                sdk.onConsentUIReady(isFromPM(payload));
                break;
            case 'sp.hideMessage':
                sdk.onAction(JSON.stringify(actionData(payload)));
                break;
            default:
                sdk.log("Unexpected event name: " + payload.name);
        }
    } catch (err) {
        sdk.onError(err.stack);
    }
}

/*
We export the handleEvent function inside a try-catch block
because newer WebViews will throw an error when a variable
is not defined. In this case, module is not defined by us
but the Node runtime.
*/
try {
    module.exports = handleEvent;
} catch (error){
  /* no-op */
}

window.addEventListener('message', handleEvent);
