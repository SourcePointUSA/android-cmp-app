function isFromPM(payload) {
    return payload.fromPM || (payload.settings && payload.settings.vendorList != null);
}

function getPmIdFromURL(url) {
    return url ? url.match(/[?&]message_id(=([^&#]*)|&|#|$)/)[2] : null;
}

function actionFromMessage(payload) {
    var actionPayload = payload.actions && payload.actions.length && payload.actions[0] && payload.actions[0].data ? payload.actions[0].data : {};
    return {
        name: payload.name,
        actionType: actionPayload.type,
        choiceId: String(actionPayload.choice_id),
        requestFromPm: false,
        pmId: getPmIdFromURL(actionPayload.iframe_url),
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
        onConsentUIReady: notImplemented('onCosentReady'),
        onAction: notImplemented('onAction'),
        onError: notImplemented('onError'),
        log: notImplemented('log')
    };
    try {
        sdk.log(JSON.stringify(payload));
        if (payload.name === 'sp.showMessage')
            sdk.onConsentUIReady(isFromPM(payload));
        else
            sdk.onAction(JSON.stringify(actionData(payload)));
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
} catch {
  /* no-op */
}

window.addEventListener('message', handleEvent);
