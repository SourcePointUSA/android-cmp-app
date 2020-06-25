var {
        eventHandlerRelativePath,
        eventMocks,
        JSReceiverMock,
        callBacksMonitor
    } = require('./test_config.js')
var { describe, it, expects } = require('./ledrest.js')

var handleEvents = require(eventHandlerRelativePath)

describe("JSReceiver/EventHandler test cases:", () => {
    it("onConsentUIReady", () => {
        expects(callBacksMonitor.onConsentUIReady).toBeFalsey()
        handleEvents(eventMocks.showMessage)
        expects(callBacksMonitor.onConsentUIReady[0]).toEqual(false)
        handleEvents(eventMocks.showPm)
        expects(callBacksMonitor.onConsentUIReady[0]).toEqual(true)
    })
})
