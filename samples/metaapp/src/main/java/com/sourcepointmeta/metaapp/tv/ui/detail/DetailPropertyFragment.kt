package com.sourcepointmeta.metaapp.tv.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.data.localdatasource.MetaTargetingParam
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign

class DetailPropertyFragment : DetailsSupportFragment() {

    private val listener: (View) -> Unit = { view ->
        Toast.makeText(requireContext(), "Run", Toast.LENGTH_SHORT).show()
    }

    private val actionListener: (Action, Property) -> Unit = { a, i ->
        Toast.makeText(requireContext(), "Run Action", Toast.LENGTH_SHORT).show()
    }

    private val helper by lazy {
        FullWidthDetailsOverviewSharedElementHelper().apply {
            this.setSharedElementEnterTransition(requireActivity(), "t_for_transition")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareEntranceTransition()

        adapter = ArrayObjectAdapter(createPresenterSelector(prop1, actionListener, listener, helper)).apply {
            add(DetailsOverviewRow(prop1).arrayObjectAdapter(Pair(1, "Run Demo")))
        }

        initEntranceTransition()
    }
}

private val tp = listOf(
    MetaTargetingParam("test", CampaignType.GDPR, "key1", "val1"),
    MetaTargetingParam("test", CampaignType.GDPR, "key2", "val2"),
    MetaTargetingParam("test", CampaignType.GDPR, "key3", "val3"),
)

val prop1 = Property(
    accountId = 1,
    propertyName = "prop1",
    timeout = 1,
    authId = null,
    messageLanguage = "ENGLISH",
    pmTab = "DEFAULT",
    is_staging = false,
    targetingParameters = tp,
    statusCampaignSet = setOf(StatusCampaign("prop1", CampaignType.GDPR, true)),
    messageType = "App",
    gdprPmId = 1212L,
    ccpaPmId = 1313L,
    campaignsEnv = CampaignsEnv.STAGE
)
