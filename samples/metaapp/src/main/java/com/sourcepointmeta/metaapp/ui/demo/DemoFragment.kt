package com.sourcepointmeta.metaapp.ui.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.ui.demo.DemoFragment.DemoAction.* // ktlint-disable
import kotlinx.android.synthetic.main.demo_fragment_layout.* // ktlint-disable
import org.koin.android.ext.android.inject

class DemoFragment : Fragment() {

    companion object {
        fun instance(propertyName: String) = DemoFragment().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
            }
        }
    }

    enum class DemoAction {
        GDPR_PM,
        CCPA_PM,
        CCPA_PM_OTT,
        GDPR_PM_OTT,
        LOG
    }

    var demoListener: ((DemoAction) -> Unit)? = null

    private val propertyName by lazy {
        arguments?.getString("property_name") ?: throw RuntimeException("Property name not set!!!")
    }
    private val dataSource by inject<LocalDataSource>()
    private val config: SpConfig by lazy {
        dataSource.getSPConfig(propertyName).getOrNull() ?: throw RuntimeException("Property name not set!!!")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.demo_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        campaign_name_fr.text = propertyName
        review_consents_ccpa_fr_ott.setOnClickListener { demoListener?.invoke(CCPA_PM_OTT) }
        review_consents_gdpr_fr_ott.setOnClickListener { demoListener?.invoke(GDPR_PM_OTT) }
        review_consents_ccpa_fr.setOnClickListener { demoListener?.invoke(CCPA_PM) }
        review_consents_gdpr_fr.setOnClickListener { demoListener?.invoke(GDPR_PM) }
        config.campaigns.find { it.campaignType == CampaignType.CCPA }
            ?.let { review_consents_ccpa_fr.isEnabled = true } ?: kotlin.run {
            review_consents_ccpa_fr.isEnabled = false
        }

        config.campaigns.find { it.campaignType == CampaignType.GDPR }
            ?.let { review_consents_gdpr_fr.isEnabled = true } ?: kotlin.run {
            review_consents_gdpr_fr.isEnabled = false
        }
    }
}
