package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp

internal interface Service : NetworkClient, DataStorage {
    companion object
}

internal fun Service.Companion.create(nc: NetworkClient, ds: DataStorage): Service = ServiceImpl(nc, ds)

private class ServiceImpl(
    private val nc: NetworkClient,
    private val ds: DataStorage
) : Service, NetworkClient by nc, DataStorage by ds {

    override fun getMessage(messageReq: MessageReq, success: (MessageResp) -> Unit, error: (Throwable) -> Unit) {
        nc.getMessage(
            messageReq,
            { messageResp ->
                success(messageResp)
                // TODO save the data into the local storage
            },
            ::error
        )
    }

    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {
        nc.getNativeMessage(
            messageReq,
            { nativeMessageResp ->
                success(nativeMessageResp)
                // TODO save the data into the local storage
            },
            ::error
        )
    }
}
