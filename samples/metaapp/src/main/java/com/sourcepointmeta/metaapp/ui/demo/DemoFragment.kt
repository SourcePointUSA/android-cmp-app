package com.sourcepointmeta.metaapp.ui.demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.ui.demo.DemoFragment.DemoAction.CCPA_PM
import com.sourcepointmeta.metaapp.ui.demo.DemoFragment.DemoAction.GDPR_PM
import kotlinx.android.synthetic.main.demo_fragment_layout.* // ktlint-disable
import org.koin.android.ext.android.inject

class DemoFragment : Fragment() {

    companion object {

        const val AUTH_ID_KEY = "authId_sharing"

        fun instance(propertyName: String, authId: String?) = DemoFragment().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
                putString(AUTH_ID_KEY, authId)
            }
        }
    }

    enum class DemoAction {
        GDPR_PM,
        CCPA_PM,
        LOG
    }

    var demoListener: ((DemoAction, ott: Boolean) -> Unit)? = null

    private val propertyName by lazy {
        arguments?.getString("property_name") ?: throw RuntimeException("Property name not set!!!")
    }
    private val authId: String? by lazy {
        arguments?.getString(AUTH_ID_KEY)
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
        review_consents_ccpa_fr.setOnClickListener { _v: View? -> demoListener?.invoke(CCPA_PM, ott_switch.isChecked) }
        review_consents_gdpr_fr.setOnClickListener { _v: View? -> demoListener?.invoke(GDPR_PM, ott_switch.isChecked) }
        auth_id_activity.setOnClickListener { _v: View? ->
            authId
                ?.let {
                    val i = Intent(activity, DemoActivityAuthId::class.java)
                    i.putExtra(AUTH_ID_KEY, it)
                    startActivity(i)
                }
                ?: run { Toast.makeText(context, "Auth id not set, please configure a value!!!", Toast.LENGTH_SHORT).show() }
        }
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
