var {
        eventHandlerRelativePath,
        eventMocks,
        JSReceiverMock,
        expectedArgs
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
    it("acceptAll from PM", () => {
        expect(JSReceiverMock.onAction).not.toBeCalled()
        handleEvents(eventMocks.AcceptAllFromPm)
        expect(JSReceiverMock.onAction).toBeCalledWith(JSON.stringify(expectedArgs.acceptAllFromPm))
    })
    it("actionAll from message dialog", () => {
        expect(JSReceiverMock.onAction).not.toBeCalled()
        handleEvents(eventMocks.acceptAllFromMsg)
        expect(JSReceiverMock.onAction).toBeCalledWith(JSON.stringify(expectedArgs.acceptAllFromMsg))
    }),
    it("actionAll from message dialog (w/ choiceId:number)", () => {
        expect(JSReceiverMock.onAction).not.toBeCalled()
        handleEvents(eventMocks.acceptAllFromMsg)
        expect(JSReceiverMock.onAction).toBeCalledWith(JSON.stringify(expectedArgs.acceptAllFromMsg))
    }),
    it("actionAll from message dialog (w/ choiceId:string) ", () => {
        expect(JSReceiverMock.onAction).not.toBeCalled()
        handleEvents({...eventMocks.acceptAllFromMsg,choiceId:Number(eventMocks.acceptAllFromMsg.choiceId)})
        expect(JSReceiverMock.onAction).toBeCalledWith(JSON.stringify(expectedArgs.acceptAllFromMsg))
    })
})
