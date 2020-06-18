var {
        it,
        expects,
        relativePath,
        eventMocks,
        JSReceiver,
        callBacksMonitor
    } = require('./test_config.js')

var handleEvents = require(relativePath)

it("onConsentUIReady should not have been called before showMessage event: ", () =>
    expects(JSReceiver.onConsentUIReadyCalled).toBeFalsey()
);

it("onConsentUIReady should have been called after showMessage event: ", () => {
    handleEvents(eventMocks.showMessage)
    expects(callBacksMonitor.onConsentUIReadyCalled).toBeFalsey()
    expects(callBacksMonitor.onConsentUIReadyCalled).toBeTruthy()
});
