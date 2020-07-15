var {
        eventHandlerRelativePath,
        eventMocks,
        JSReceiverMock,
        callBacksMonitor
    } = require('./test_config.js')

var handleEvents = require(eventHandlerRelativePath)

describe("JSReceiver/EventHandler test cases:", () => {
    it("onConsentUIReady", () => {
        expect(JSReceiverMock.onConsentUIReady).not.toBeCalled()
        handleEvents(eventMocks.showMessage)
        expect(JSReceiverMock.onConsentUIReady).toBeCalled()
    })
})
