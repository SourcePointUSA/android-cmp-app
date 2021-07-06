package com.sourcepointmeta.metaapp

import com.example.uitestutil.* // ktlint-disable
import com.sourcepointmeta.metaapp.db.MetaAppDB
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import java.util.*

class TestUseCaseMeta {
    companion object {

        fun checkMessageDisplayed() {
            isDisplayedAllOfByResId(resId = R.id.message)
        }

        fun tapFab() {
            performClickById(R.id.fab)
        }

        fun addTestProperty() {
            addProperty(
                propertyName = "mobile.multicampaign.demo",
                accountId = "22",
                gdprPmId = "488393",
                ccpaPmId = "509688",
                gdprTps = listOf(Pair("a", "a"), Pair("b", "b")),
                ccpaTps = listOf(Pair("c", "c"))
            )
        }

        fun saveProperty() = scrollAndPerformClickById(R.id.save_btn)

        fun addProperty(
            propertyName: String,
            accountId: String,
            gdprPmId: String,
            ccpaPmId: String? = null,
            autId: String? = null,
            gdprTps: List<Pair<String, String>>? = null,
            ccpaTps: List<Pair<String, String>>? = null
        ) {
            addTextById(R.id.prop_name_ed, propertyName)
            addTextById(R.id.account_id_ed, accountId)
            addTextById(R.id.gdpr_pm_id_ed, gdprPmId)
            ccpaPmId?.let { addTextById(R.id.ccpa_pm_id_ed, it) }
            autId?.let { addTextById(R.id.auth_id_ed, it) }
            gdprTps?.let {
                it.forEach { tp ->
                    scrollAndPerformClickById(R.id.btn_targeting_params_gdpr)
                    addTextById(R.id.tp_key_ed, tp.first)
                    addTextById(R.id.tp_value_et, tp.second)
                    pressAlertDialogBtn("CREATE")
                }
            }
            ccpaTps?.let {
                it.forEach { tp ->
                    scrollAndPerformClickById(R.id.btn_targeting_params_ccpa)
                    addTextById(R.id.tp_key_ed, tp.first)
                    addTextById(R.id.tp_value_et, tp.second)
                    pressAlertDialogBtn("CREATE")
                }
            }
        }

        fun clickFirstItem(){
            clickListItem<PropertyAdapter.Vh>(0, R.id.property_list)
        }

        fun runDemo(){
            clickElementListItem<PropertyAdapter.Vh>(R.id.play_demo_btn, R.id.property_list)
        }

        fun MetaAppDB.addTestProperty() {
            campaignQueries.insertProperty(
                property_name = "mobile.multicampaign.demo",
                account_id = 22,
                gdpr_pm_id = 488393L,
                ccpa_pm_id = 509688L,
                campaign_env = "prod",
                timeout = 3000L,
                timestamp = Date().time,
                is_staging = 0,
                message_type = "WebView",
                auth_Id = null,
                pm_tab = "PURPOSES",
                message_language = "ENGLISH"
            )
            campaignQueries.insertStatusCampaign(
                property_name = "mobile.multicampaign.demo",
                campaign_type = "GDPR",
                enabled = 1
            )
            campaignQueries.insertStatusCampaign(
                property_name = "mobile.multicampaign.demo",
                campaign_type = "CCPA",
                enabled = 0
            )
        }
    }
}
