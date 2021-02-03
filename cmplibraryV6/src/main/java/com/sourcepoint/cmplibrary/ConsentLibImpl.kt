package com.sourcepoint.cmplibrary

import android.content.Context
import android.view.View
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.util.ConnectionManager
import com.sourcepoint.cmplibrary.util.ExecutorManager
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.gdpr_cmplibrary.NativeMessage
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab
import com.sourcepoint.gdpr_cmplibrary.exception.Logger

internal class ConsentLibImpl(
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton,
    internal val campaign: Campaign,
    internal val pPrivacyManagerTab: PrivacyManagerTab,
    internal val context: Context,
    internal val pLogger: Logger,
    internal val pJsonConverter: JsonConverter,
    internal val pConnectionManager: ConnectionManager,
    internal val networkClient: NetworkClient,
    internal val dataStorage: DataStorage,
    private val viewManager: ViewsManager,
    private val executor: ExecutorManager
) : ConsentLib {

    override var spClient: SpClient?
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun loadMessage() {
        TODO("Not yet implemented")
    }

    override fun loadMessage(authId: String) {
        TODO("Not yet implemented")
    }

    override fun loadMessage(nativeMessage: NativeMessage) {
        TODO("Not yet implemented")
    }

    override fun loadMessage(authId: String, nativeMessage: NativeMessage) {
        TODO("Not yet implemented")
    }

    override fun loadPrivacyManager() {
        TODO("Not yet implemented")
    }

    override fun loadPrivacyManager(authId: String) {
        TODO("Not yet implemented")
    }

    override fun showView(view: View) {
        TODO("Not yet implemented")
    }

    override fun removeView(view: View?) {
        TODO("Not yet implemented")
    }
}
