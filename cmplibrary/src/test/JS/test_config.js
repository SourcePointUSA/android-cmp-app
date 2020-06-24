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

module.exports = {
    relativePath,
    eventMocks,
    JSReceiver,
    callBacksMonitor
}