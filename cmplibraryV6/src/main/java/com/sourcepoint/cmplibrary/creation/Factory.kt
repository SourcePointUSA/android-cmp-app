package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.SpConsentLibImpl
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.campaign.create
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.create
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import com.sourcepoint.cmplibrary.util.ConnectionManager
import com.sourcepoint.cmplibrary.util.ExecutorManager
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.cmplibrary.util.create
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference

fun makeConsentLib(
    accountId: Int,
    propertyName: String,
    propertyId: Int,
    pmId: String,
    context: Activity,
    privacyManagerTab: PrivacyManagerTabK
): SpConsentLib {

    val account = Campaign(accountId, propertyId, propertyName, pmId)
    val appCtx: Context = context.applicationContext
    val client = createClientInfo()
    val errorManager = errorMessageManager(account, client)
    val logger = createLogger(errorManager)
    val jsonConverter = JsonConverter.create()
    val connManager = ConnectionManager.create(appCtx)
    val responseManager = ResponseManager.create(jsonConverter)
    val networkClient = networkClient(OkHttpClient(), responseManager)
    val dataStorageGdpr = DataStorageGdpr.create(appCtx)
    val dataStorageCcpa = DataStorageCcpa.create(appCtx)
    val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa)
    val service: Service = Service.create(networkClient, dataStorage)
    val viewManager = ViewsManager.create(WeakReference<Activity>(context), connManager)
    val execManager = ExecutorManager.create(appCtx)
    val urlManager: HttpUrlManager = HttpUrlManagerSingleton
    val campaignManager: CampaignManager = CampaignManager.create(dataStorage).apply {
        addCampaign(Legislation.GDPR, account)
    }

    return SpConsentLibImpl(
        urlManager = urlManager,
        campaign = account,
        pPrivacyManagerTab = privacyManagerTab,
        context = appCtx,
        pLogger = logger,
        pJsonConverter = jsonConverter,
        pConnectionManager = connManager,
        service = service,
        viewManager = viewManager,
        executor = execManager,
        dataStorage = dataStorage,
        campaignManager = campaignManager
    )
}
