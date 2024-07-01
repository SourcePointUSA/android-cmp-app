package com.sourcepointmeta.metaapp.ui.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.databinding.DemoFragmentLayoutBinding
import com.sourcepointmeta.metaapp.ui.demo.DemoFragment.DemoAction.* // ktlint-disable
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
        USNAT_PM,
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

    private lateinit var binding: DemoFragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DemoFragmentLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.campaignNameFr.text = propertyName
        binding.reviewConsentsCcpaFr.setOnClickListener { demoListener?.invoke(CCPA_PM) }
        binding.reviewConsentsGdprFr.setOnClickListener { demoListener?.invoke(GDPR_PM) }
        binding.reviewConsentsUsnatFr.setOnClickListener { demoListener?.invoke(USNAT_PM) }

        config.campaigns.find { it.campaignType == CampaignType.GDPR }
            ?.let { binding.reviewConsentsGdprFr.isEnabled = true }
            ?: run { binding.reviewConsentsGdprFr.isEnabled = false }
        config.campaigns.find { it.campaignType == CampaignType.CCPA }
            ?.let { binding.reviewConsentsCcpaFr.isEnabled = true }
            ?: run { binding.reviewConsentsCcpaFr.isEnabled = false }
        config.campaigns.find { it.campaignType == CampaignType.USNAT }
            ?.let { binding.reviewConsentsUsnatFr.isEnabled = true }
            ?: run { binding.reviewConsentsUsnatFr.isEnabled = false }
    }
}
