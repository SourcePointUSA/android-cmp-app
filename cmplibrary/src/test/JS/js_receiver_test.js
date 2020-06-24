var {
        relativePath,
        eventMocks,
        JSReceiver,
        callBacksMonitor
    } = require('./test_config.js')
var { describe, it, expects } = require('./ledrest.js')

var handleEvents = require(relativePath)

describe("JSReceiver test cases:", () => {
    it("onConsentUIReady should only be called after showMessage event", () => {
        expects(JSReceiver.onConsentUIReadyCalled).toBeFalsey()
        handleEvents(eventMocks.showMessage)
        expects(callBacksMonitor.onConsentUIReadyCalled).toBeTruthy()
    })
})
