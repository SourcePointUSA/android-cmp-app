package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.SpConsentLibImpl
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.campaign.create
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.consent.create
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.create
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.create
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.create
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.cmplibrary.util.create
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference

fun makeConsentLib(
    spConfig: SpConfig,
    activity: Activity,
    spClient: SpClient,
    messageLanguage: MessageLanguage
): SpConsentLib {

    val appCtx: Context = activity.applicationContext
    val client = createClientInfo()
    val dataStorageGdpr = DataStorageGdpr.create(appCtx)
    val dataStorageCcpa = DataStorageCcpa.create(appCtx)
    val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa)
    val campaignManager: CampaignManager = CampaignManager.create(dataStorage, spConfig, messageLanguage)
    val errorManager = errorMessageManager(campaignManager, client)
    val logger = createLogger(errorManager)
    val jsonConverter = JsonConverter.create()
    val connManager = ConnectionManager.create(appCtx)
    val responseManager = ResponseManager.create(jsonConverter)
    val networkClient = networkClient(OkHttpClient(), responseManager, logger)
    val viewManager = ViewsManager.create(WeakReference<Activity>(activity), connManager)
    val execManager = ExecutorManager.create(appCtx)
    val urlManager: HttpUrlManager = HttpUrlManagerSingleton
    val consentManagerUtils: ConsentManagerUtils = ConsentManagerUtils.create(campaignManager, dataStorage, logger)
    val service: Service = Service.create(networkClient, campaignManager, consentManagerUtils, dataStorage, logger)
    val consentManager: ConsentManager = ConsentManager.create(service, consentManagerUtils, Env.STAGE, logger, dataStorage, execManager)

    val env = Env.values().find { it.name == BuildConfig.SDK_ENV } ?: Env.PROD

    return SpConsentLibImpl(
        context = appCtx,
        pLogger = logger,
        pJsonConverter = jsonConverter,
        service = service,
        executor = execManager,
        viewManager = viewManager,
        campaignManager = campaignManager,
        consentManager = consentManager,
        urlManager = urlManager,
        env = env,
        spClient = spClient
    )
}
