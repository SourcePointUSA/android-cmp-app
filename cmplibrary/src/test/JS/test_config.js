var relativePath = '../../main/res/raw/js_receiver.js'

var eventMocks = {
  showMessage: {
    data: {
      name: "sp.showMessage",
      actions: [],
      settings: {
        "language": "browser"
      }
    }
  },
  hideMessage: {
    data: {
      "name": "sp.hideMessage",
      "actions": [
        {
          "type": "choice",
          "data": {
            "choice_id": 920716,
            "type": 11,
            "iframe_url": null,
            "button_text": "1583247568929"
          }
        }
      ],
      "settings": {
        "language": "browser"
      }
    }
  }
}

var callBacksMonitor = {
    onConsentUIReadyCalled: false
}

var onConsentUIReady =  () => callBacksMonitor.onConsentUIReadyCalled = true

var JSReceiver = {
  onConsentUIReady,
  onError: console.log,
  onAction: console.log,
  onSavePM: console.log,
  log: () => null
}

global.window = {
  addEventListener: function () { }
}

global.JSReceiver = JSReceiver

var expects = thing => ({
  toEqual: other => assert(other.equals ? other.equals(thing) : other == thing),
  toBe: other => assert(other === thing),
  toBeTruthy: () => assert(!!!!thing),
  toBeFalsey: () => assert(!!!thing)
})

var it = (message, assertions) => {console.log(message); assertions()}

var config = {
    it,
    expects,
    relativePath,
    eventMocks,
    JSReceiver,
    callBacksMonitor
}

module.exports = config

var FgRed = "\x1b[31m"
var FgGreen = "\x1b[32m"

function assert(assertion){
    assertion ? console.log(FgGreen, "✓") : console.log(FgRed, "✕")
}