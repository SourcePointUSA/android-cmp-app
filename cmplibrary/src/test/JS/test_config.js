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

var JSReceiver = {
  onConsentUIReady: () => console.log("msgRdy!"),
  onError: console.log,
  onAction: console.log,
  onSavePM: console.log,
  log: console.log
}

global.window = {
  addEventListener: function () { }
}

global.JSReceiver = JSReceiver

var config = {
    relativePath,
    eventMocks,
    JSReceiver
}

module.exports = config