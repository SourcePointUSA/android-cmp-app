addEventListener('message', handleEvent);
function handleEvent(event) {
    try {
        JSReceiver.log(JSON.stringify(event.data, null, 2));
        if (event.data.name === 'sp.showMessage') {
            JSReceiver.onConsentUIReady();
            return;
        }
        const data = eventData(event);
        JSReceiver.log(JSON.stringify(data, null, 2));
        if(data.type) {
            if(data.type === 1) JSReceiver.onSavePM(JSON.stringify(data.payload));
            else JSReceiver.onAction(data.type, data.choiceId);
        }
    } catch (err) {
        JSReceiver.log(err.stack);
    };
};

function eventData(event) {
    return isFromPM(event) ? dataFromPM(event) : dataFromMessage(event);
};

function isFromPM(event) {
    return !!event.data.payload;
};

function dataFromMessage(msgEvent) {
    return {
        name: msgEvent.data.name,
        type: msgEvent.data.actions.length ? msgEvent.data.actions[0].data.type : null,
        choiceId: msgEvent.data.actions.length ? msgEvent.data.actions[0].data.choice_id : null
    };
};

function dataFromPM(pmEvent) {
    const data = {
        name: pmEvent.data.name,
        type: pmEvent.data ? pmEvent.data.payload.actionType : null,
        choiceId: null
    };
    if(data.type === 1) data.payload = userConsents(pmEvent.data.payload);
    return data;
};

function userConsents(payload){
    return {
        acceptedVendors: payload.consents.vendors.accepted,    
        acceptedCategories: payload.consents.categories.accepted    
    };
};