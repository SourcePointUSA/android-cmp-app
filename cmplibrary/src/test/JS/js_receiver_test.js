var {relativePath, eventMocks} = require('./test_config.js')

var handleEvents = require(relativePath)

handleEvents(eventMocks.showMessage)

handleEvents(eventMocks.hideMessage)

