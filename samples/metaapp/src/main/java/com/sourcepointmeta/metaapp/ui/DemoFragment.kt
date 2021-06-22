package com.sourcepointmeta.metaapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils
import androidx.fragment.app.Fragment
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.ui.DemoFragment.DemoAction.CCPA_PM
import com.sourcepointmeta.metaapp.ui.DemoFragment.DemoAction.GDPR_PM
import kotlinx.android.synthetic.main.activity_demo.* // ktlint-disable
import kotlinx.android.synthetic.main.demo_fragment_layout.* // ktlint-disable
import org.koin.android.ext.android.inject

class DemoFragment : Fragment() {

//    internal val adapter by lazy { DemoAdapter() }

    companion object {
        fun instance(propertyName: String) = DemoFragment().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
            }
        }

        private const val GRID_SPAN_COUNT_MIN = 1
        private const val GRID_SPAN_COUNT_MAX = 4
    }

    enum class DemoAction {
        GDPR_PM,
        CCPA_PM,
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
        review_consents_ccpa_fr.setOnClickListener { _v: View? -> demoListener?.invoke(CCPA_PM) }
        review_consents_gdpr_fr.setOnClickListener { _v: View? -> demoListener?.invoke(GDPR_PM) }
        config.campaigns.find { it.campaignType == CampaignType.CCPA }
            ?.let { review_consents_ccpa_fr.isEnabled = true } ?: kotlin.run {
            review_consents_ccpa_fr.isEnabled = false
        }

        config.campaigns.find { it.campaignType == CampaignType.GDPR }
            ?.let { review_consents_gdpr_fr.isEnabled = true } ?: kotlin.run {
            review_consents_gdpr_fr.isEnabled = false
        }
    }

    private fun calculateGridSpanCount(): Int {
        val displayMetrics = resources.displayMetrics
        val displayWidth = displayMetrics.widthPixels
        val itemSize = resources.getDimensionPixelSize(R.dimen.cat_toc_item_size)
        val gridSpanCount = displayWidth / itemSize
        return MathUtils.clamp(
            gridSpanCount,
            GRID_SPAN_COUNT_MIN,
            GRID_SPAN_COUNT_MAX
        )
    }
}
