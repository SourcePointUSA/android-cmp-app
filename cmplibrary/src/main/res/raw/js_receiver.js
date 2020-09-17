function isFromPM(payload) {
    return payload.fromPM || (payload.settings && payload.settings.vendorList != null);
}

function getPmIdAndPmTabFromURL(url) {
    let pmId = url ? url.match(/[?&]message_id(=([^&#]*)|&|#|$)/)[2] : null;
    let pmTabShow = null;
    if(url &&  url.match(/[?&]pmTab(=([^&#]*)|&|#|$)/) && url.match(/[?&]pmTab(=([^&#]*)|&|#|$)/)[2]){
       pmTabShow = url.match(/[?&]pmTab(=([^&#]*)|&|#|$)/)[2];
     }
    return [pmId, pmTabShow];
}

function actionFromMessage(payload) {
    var actionPayload = payload.actions && payload.actions.length && payload.actions[0] && payload.actions[0].data ? payload.actions[0].data : {};
    let pmDetails = getPmIdAndPmTabFromURL(actionPayload.iframe_url);
    return {
        name: payload.name,
        actionType: actionPayload.type,
        choiceId: String(actionPayload.choice_id),
        requestFromPm: false,
        pmId: pmDetails[0],
        pmTab: pmDetails[1],
        saveAndExitVariables: {}
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
        saveAndExitVariables: payload.payload
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
