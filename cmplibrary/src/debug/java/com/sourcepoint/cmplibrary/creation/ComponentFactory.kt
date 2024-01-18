package com.sourcepoint.cmplibrary.creation

import android.content.Context
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.create
import com.sourcepoint.cmplibrary.data.network.createNetworkClient
import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataResp
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.exception.Logger
import okhttp3.OkHttpClient

internal fun getConnectionManager(appCtx: Context): ConnectionManager {
    val mockObject: ConnectionManager? = com.sourcepoint.cmplibrary.util.check {
        PreferenceManager.getDefaultSharedPreferences(appCtx).all
            .toList()
            .find { it.first == "connectionTest" }
            ?.let { it.second as? Boolean }
            ?.let {
                object : ConnectionManager {
                    override val isConnected: Boolean
                        get() {
                            // emulate the delay in a real device
                            Thread.sleep(400)
                            return it
                        }
                }
            }
    }.getOrNull()

    return mockObject ?: ConnectionManager.create(appCtx)
}

internal fun networkClient(appCtx: Context, netClient: OkHttpClient, responseManage: ResponseManager, logger: Logger): NetworkClient {

    val nc = createNetworkClient(
        httpClient = netClient,
        responseManager = responseManage,
        urlManager = HttpUrlManagerSingleton,
        logger = logger
    )

    val mockObject: NetworkClient? = com.sourcepoint.cmplibrary.util.check {
        PreferenceManager.getDefaultSharedPreferences(appCtx).all
            .toList()
            .find { it.first == "metadata_resp_applies_false" }
            ?.let { it.second as? Boolean }
            ?.let { NCMock(nc, it) }
    }.getOrNull()

    return mockObject ?: nc
}

internal class NCMock(nc: NetworkClient, val applies: Boolean) : NetworkClient by nc {
    override fun getMetaData(param: MetaDataParamReq): Either<MetaDataResp> {
        return Either.Right(
            MetaDataResp(
                gdpr = null,
                ccpa = MetaDataResp.Ccpa(
                    sampleRate = 1.0,
                    applies = applies
                ),
                usNat = null
            )
        )
    }
}
