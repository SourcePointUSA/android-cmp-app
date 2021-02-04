package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.sourcepoint.cmplibrary.ConsentLib
import com.sourcepoint.cmplibrary.ConsentLibImpl
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.create
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.util.ConnectionManager
import com.sourcepoint.cmplibrary.util.ExecutorManager
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.cmplibrary.util.create
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference

fun makeConsentLib(
    accountId: Int,
    propertyName: String,
    propertyId: Int,
    pmId: String,
    context: Activity,
    privacyManagerTab: PrivacyManagerTab
): ConsentLib {

    val account = Campaign(accountId, propertyId, propertyName, pmId)
    val appCtx: Context = context.applicationContext
    val client = createClientInfo()
    val errorManager = errorMessageManager(account, client)
    val logger = createLogger(errorManager)
    val jsonConverter = JsonConverter.create()
    val connManager = ConnectionManager.create(appCtx)
    val responseManager = ResponseManager.create(jsonConverter)
    val networkClient = networkClient(OkHttpClient(), responseManager)
    val dataStorage = DataStorage.create(appCtx)
    val service: Service = Service.create(networkClient, dataStorage)
    val viewManager = ViewsManager.create(WeakReference<Activity>(context))
    val execManager = ExecutorManager.create(appCtx)
    val urlManager: HttpUrlManager = HttpUrlManagerSingleton

    return ConsentLibImpl(
        urlManager, account, privacyManagerTab, appCtx, logger, jsonConverter, connManager, service, viewManager, execManager
    )
}
