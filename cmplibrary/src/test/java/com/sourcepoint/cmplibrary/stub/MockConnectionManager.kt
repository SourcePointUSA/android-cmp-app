package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager

class MockConnectionManager : ConnectionManager {
    override val isConnected: Boolean
        get() = true
}
