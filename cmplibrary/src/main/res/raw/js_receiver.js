window.addEventListener('message', handleEvent);
function handleEvent(event) {
    try {
        JSReceiver.log(JSON.stringify(event.data, null, 2));
        if (event.data.name === 'sp.showMessage') {
            JSReceiver.onConsentUIReady(isFromPM(event));
            return;
        }
        JSReceiver.onAction(JSON.stringify(consentData(event)));
    } catch (err) {
        JSReceiver.log(err.stack);
    };
};

function consentData(event) {
    return isFromPM(event) ? dataFromPM(event) : dataFromMessage(event);
};

function isFromPM(event) {
    return event.data.fromPM || event.data.settings && event.data.settings.vendorList != null;
};

function dataFromMessage(msgEvent) {

    return {
        name: msgEvent.data.name,
        actionType: msgEvent.data.actions.length ? msgEvent.data.actions[0].data.type : null,
        choiceId: msgEvent.data.actions.length ? String(msgEvent.data.actions[0].data.choice_id) : null,
        requestFromPm: false,
        pmId: msgEvent.data.actions.length ? getPmIdFromURL(msgEvent.data.actions[0].data.iframe_url) : null,
        saveAndExitVariables: {}
    };
};

function dataFromPM(pmEvent) {
    return {
        name: pmEvent.data.name,
        actionType: pmEvent.data ? pmEvent.data.actionType : null,
        choiceId: null,
        requestFromPm: true,
        pmId: null,
        saveAndExitVariables: pmEvent.data.payload
    };
};

function getPmIdFromURL(url) {
    return url ? url.match(/[?&]message_id(=([^&#]*)|&|#|$)/)[2] : null;
};

module.exports = handleEvent