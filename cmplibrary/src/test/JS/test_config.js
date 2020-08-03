var eventHandlerRelativePath = '../../main/res/raw/js_receiver.js'

var expectedArgs = {
  acceptAllFromPm: {
    "name": "sp.hideMessage",
    "actionType": 11,
    "choiceId": null,
    "requestFromPm": true,
    "pmId": null,
    "saveAndExitVariables": {
      "foo": "bar",
    }
  },
  acceptAllFromMsg: { 
    "name": "sp.hideMessage", 
    "actionType": 11, 
    "choiceId": "1067098", 
    "requestFromPm": false, 
    "pmId":null,
    "saveAndExitVariables": {} 
  },
  showOptions: {
    "name": "sp.hideMessage",
    "actionType": 12,
    "choiceId": "1067098",
    "requestFromPm": false,
    "pmId":"122058",
    "saveAndExitVariables": {}
  },
}

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
  AcceptAllFromPm: {
    data: {
      "name": "sp.hideMessage",
      "fromPM": true,
      "actionType": 11,
      "payload": {
        "foo": "bar",
      }
    },
  },
  acceptAllFromMsg: {
    data: {
      "name": "sp.hideMessage",
      "actions": [
        {
          "type": "choice",
          "data": {
            "choice_id": 1067098,
            "type": 11,
            "iframe_url": null,
            "button_text": "1589214494409"
          }
        }
      ],
      "settings": {
        "showClose": true,
        "useBrowserDefault": true,
        "width": {
          "type": "px",
          "value": 600
        },
        "border": {
          "borderWidth": 1,
          "borderColor": "#ffffff",
          "borderTopLeftRadius": 0,
          "borderTopRightRadius": 0,
          "borderBottomLeftRadius": 0,
          "borderBottomRightRadius": 0,
          "borderStyle": "solid"
        },
        "defaultLanguage": "EN",
        "selectedLanguage": "EN",
        "closeAlign": "right",
        "closeFont": {
          "fontSize": 24,
          "fontWeight": "800",
          "color": "#999999",
          "fontFamily": "tahoma,geneva,sans-serif"
        }
      }
    }
  },
  showOptions: {
    data: {
      "name": "sp.hideMessage",
      "actions": [
        {
          "type": "choice",
          "data": {
            "choice_id": 1067098,
            "type": 12,
            "iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?site_id=7639&message_id=122058",
            "button_text": "1589214494409"
          }
        }
      ],
      "settings": {
        "showClose": true,
        "useBrowserDefault": true,
        "width": {
          "type": "px",
          "value": 600
        },
        "border": {
          "borderWidth": 1,
          "borderColor": "#ffffff",
          "borderTopLeftRadius": 0,
          "borderTopRightRadius": 0,
          "borderBottomLeftRadius": 0,
          "borderBottomRightRadius": 0,
          "borderStyle": "solid"
        },
        "defaultLanguage": "EN",
        "selectedLanguage": "EN",
        "closeAlign": "right",
        "closeFont": {
          "fontSize": 24,
          "fontWeight": "800",
          "color": "#999999",
          "fontFamily": "tahoma,geneva,sans-serif"
        }
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
  JSReceiverMock,
  expectedArgs
}