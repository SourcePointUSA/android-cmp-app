var eventHandlerRelativePath = '../../main/res/raw/js_receiver.js'

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
  showPm: {
    data: {
      "name": "sp.showMessage",
      "actions": [],
      "settings": {
        "vendorList": "5ed8f883b8e05c4a06748e19",
        "useStacks": false,
        "lockScroll": true,
        "width": {
          "type": "%",
          "value": 100
        },
        "border": {
          "borderWidth": 0,
          "borderColor": "#000000",
          "borderTopLeftRadius": 0,
          "borderTopRightRadius": 0,
          "borderBottomLeftRadius": 0,
          "borderBottomRightRadius": 0,
          "borderStyle": "solid"
        },
        "showVeil": true,
        "veilOpacity": 0.55
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

var JSReceiverMock = {
  onConsentUIReady: jest.fn(),
  onError: jest.fn(),
  onAction: jest.fn(),
  onSavePM: jest.fn(),
  log: jest.fn()
}

global.window = {
  addEventListener: function () { }
}

global.JSReceiver = JSReceiverMock

module.exports = {
    eventHandlerRelativePath,
    eventMocks,
    JSReceiverMock
}