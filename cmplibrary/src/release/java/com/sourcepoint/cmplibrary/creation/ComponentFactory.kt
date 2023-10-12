package com.sourcepoint.cmplibrary.creation

import android.content.Context
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.create

internal fun getConnectionManager(appCtx: Context): ConnectionManager = ConnectionManager.create(appCtx)
