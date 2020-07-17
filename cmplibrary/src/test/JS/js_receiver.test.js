var {
        eventHandlerRelativePath,
        eventMocks,
        JSReceiverMock,
        callBacksMonitor
    } = require('./test_config.js')

var handleEvents = require(eventHandlerRelativePath)

afterEach(() => {
    jest.clearAllMocks();
});

describe("JSReceiver/EventHandler test cases:", () => {
    it("showMessage", () => {
        expect(JSReceiverMock.onConsentUIReady).not.toBeCalled()
        handleEvents(eventMocks.showMessage)
        expect(JSReceiverMock.onConsentUIReady).toBeCalledWith(false)
    })
    it("showPm", () => {
        expect(JSReceiverMock.onConsentUIReady).not.toBeCalled()
        handleEvents(eventMocks.showPm)
        expect(JSReceiverMock.onConsentUIReady).toBeCalledWith(true)
    })
})
