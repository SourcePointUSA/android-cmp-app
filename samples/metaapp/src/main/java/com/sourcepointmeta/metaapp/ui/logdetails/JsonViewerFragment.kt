package com.sourcepointmeta.metaapp.ui.logdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import kotlinx.android.synthetic.main.jsonviewer_layout.*
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject

class JsonViewerFragment : Fragment() {

    companion object {
        fun instance(propertyName: String) = JsonViewerFragment().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
            }
        }
    }
    private val propertyName by lazy {
        arguments?.getString("property_name") ?: throw RuntimeException("Property name not set!!!")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.jsonviewer_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_json.bindJson(test)

    }
}

val test = """
    {
        "propertyId": 16893,
        "propertyPriorityData": {
            "stage_message_limit": 1,
            "site_id": 16893,
            "public_campaign_type_priority": [
                4,
                1,
                2
            ],
            "multi_campaign_enabled": true,
            "stage_campaign_type_priority": [
                4,
                1
            ],
            "public_message_limit": 3
        },
        "campaigns": [
            {
                "type": "GDPR",
                "applies": true,
                "userConsent": {
                    "euconsent": "CPIMpN7PIMpN7AGABCENBfCgAAAAAAAAAAYgAAAAAAAA.YAAAAAAAAAAA",
                    "grants": {
                        "5e7ced57b8e05c485246cce0": {
                            "vendorGrant": false,
                            "purposeGrants": {
                                "608bad95d08d3112188e0e29": false,
                                "608bad95d08d3112188e0e2f": false,
                                "608bad95d08d3112188e0e36": false,
                                "608bad95d08d3112188e0e3d": false,
                                "608bad96d08d3112188e0e4d": false,
                                "608bad96d08d3112188e0e53": false,
                                "608bad96d08d3112188e0e59": false,
                                "608bad96d08d3112188e0e5f": false
                            }
                        },
                        "5f1b2fbeb8e05c306f2a1eb9": {
                            "vendorGrant": false,
                            "purposeGrants": {
                                "608bad95d08d3112188e0e29": false,
                                "608bad95d08d3112188e0e2f": true
                            }
                        },
                        "5ff4d000a228633ac048be41": {
                            "vendorGrant": false,
                            "purposeGrants": {
                                "608bad95d08d3112188e0e2f": false,
                                "608bad95d08d3112188e0e36": false,
                                "60b65857619abe242bed971e": false
                            }
                        }
                    },
                    "addtlConsent": "1~",
                    "consentedToAll": null,
                    "rejectedAny": null,
                    "childPmId": null,
                    "hasConsentData": false,
                    "dateCreated": "2021-06-22T15:28:49.758Z",
                    "TCData": {
                        "IABTCF_AddtlConsent": "1~",
                        "IABTCF_CmpSdkID": 6,
                        "IABTCF_CmpSdkVersion": 2,
                        "IABTCF_PolicyVersion": 2,
                        "IABTCF_PublisherCC": "DE",
                        "IABTCF_PurposeOneTreatment": 0,
                        "IABTCF_UseNonStandardStacks": 0,
                        "IABTCF_TCString": "CPIMpN7PIMpN7AGABCENBfCgAAAAAAAAAAYgAAAAAAAA.YAAAAAAAAAAA",
                        "IABTCF_VendorConsents": "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                        "IABTCF_VendorLegitimateInterests": "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                        "IABTCF_PurposeConsents": "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                        "IABTCF_PurposeLegitimateInterests": "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                        "IABTCF_SpecialFeaturesOptIns": "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                        "IABTCF_PublisherConsent": "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                        "IABTCF_PublisherLegitimateInterests": "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                        "IABTCF_PublisherCustomPurposesConsents": "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                        "IABTCF_PublisherCustomPurposesLegitimateInterests": "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                        "IABTCF_gdprApplies": 1
                    }
                },
                "message": {
                    "message_json": {
                        "type": "Notice",
                        "name": "GDPR Message",
                        "settings": {
                            "selected_privacy_manager": {
                                "type": 12,
                                "data": {
                                    "button_text": "1619766959808",
                                    "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=488393",
                                    "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2"
                                }
                            }
                        },
                        "children": [
                            {
                                "type": "Text",
                                "name": "Text",
                                "settings": {
                                    "languages": {
                                        "EN": {
                                            "text": "<p>GDPR Message</p>"
                                        }
                                    },
                                    "text": "<p>GDPR Message</p>",
                                    "font": {
                                        "fontSize": 24,
                                        "fontWeight": "400",
                                        "color": "#000000",
                                        "fontFamily": "arial, helvetica, sans-serif"
                                    }
                                },
                                "children": []
                            },
                            {
                                "type": "Button",
                                "name": "Button",
                                "settings": {
                                    "languages": {
                                        "EN": {
                                            "text": "Show Options"
                                        }
                                    },
                                    "text": "Show Options",
                                    "choice_option": {
                                        "type": 12,
                                        "data": {
                                            "button_text": "1619766996522",
                                            "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=488393",
                                            "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2"
                                        }
                                    },
                                    "font": {
                                        "fontSize": 14,
                                        "fontWeight": "400",
                                        "color": "#1890ff",
                                        "fontFamily": "arial, helvetica, sans-serif"
                                    },
                                    "background": "#ffffff"
                                },
                                "children": []
                            },
                            {
                                "type": "Button",
                                "name": "Button",
                                "settings": {
                                    "languages": {
                                        "EN": {
                                            "text": "Reject All"
                                        }
                                    },
                                    "text": "Reject All",
                                    "choice_option": {
                                        "type": 13,
                                        "data": {
                                            "button_text": "1619767038123",
                                            "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2",
                                            "consent_language": "EN"
                                        }
                                    },
                                    "background": "#ed719e"
                                },
                                "children": []
                            },
                            {
                                "type": "Button",
                                "name": "Button",
                                "settings": {
                                    "languages": {
                                        "EN": {
                                            "text": "Accept All"
                                        }
                                    },
                                    "text": "Accept All",
                                    "choice_option": {
                                        "type": 11,
                                        "data": {
                                            "button_text": "1619767045875",
                                            "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2",
                                            "consent_language": "EN"
                                        }
                                    }
                                },
                                "children": []
                            }
                        ],
                        "compliance_status": true,
                        "compliance_list": [
                            {
                                "1": true
                            },
                            {
                                "2": true
                            },
                            {
                                "3": true
                            },
                            {
                                "4": true
                            },
                            {
                                "5": true
                            },
                            {
                                "6": true
                            },
                            {
                                "7": true
                            },
                            {
                                "8": true
                            },
                            {
                                "9": true
                            },
                            {
                                "10": true
                            },
                            {
                                "11": true
                            }
                        ]
                    },
                    "message_choice": [
                        {
                            "choice_id": 4094524,
                            "type": 15,
                            "iframe_url": null,
                            "button_text": "Dismiss"
                        },
                        {
                            "choice_id": 4094525,
                            "type": 12,
                            "iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=488393",
                            "button_text": "1619766996522"
                        },
                        {
                            "choice_id": 4094526,
                            "type": 13,
                            "iframe_url": null,
                            "button_text": "1619767038123"
                        },
                        {
                            "choice_id": 4094527,
                            "type": 11,
                            "iframe_url": null,
                            "button_text": "1619767045875"
                        }
                    ],
                    "categories": [
                        {
                            "_id": "608bad95d08d3112188e0e29",
                            "type": "IAB_PURPOSE",
                            "name": "Store and/or access information on a device",
                            "description": "Cookies, device identifiers, or other information can be stored or accessed on your device for the purposes presented to you."
                        },
                        {
                            "_id": "608bad95d08d3112188e0e2f",
                            "type": "IAB_PURPOSE",
                            "name": "Select basic ads",
                            "description": "Ads can be shown to you based on the content you’re viewing, the app you’re using, your approximate location, or your device type."
                        },
                        {
                            "_id": "608bad95d08d3112188e0e36",
                            "type": "IAB_PURPOSE",
                            "name": "Create a personalised ads profile",
                            "description": "A profile can be built about you and your interests to show you personalised ads that are relevant to you."
                        },
                        {
                            "_id": "608bad95d08d3112188e0e3d",
                            "type": "IAB_PURPOSE",
                            "name": "Select personalised ads",
                            "description": "Personalised ads can be shown to you based on a profile about you."
                        },
                        {
                            "_id": "608bad96d08d3112188e0e4d",
                            "type": "IAB_PURPOSE",
                            "name": "Measure ad performance",
                            "description": "The performance and effectiveness of ads that you see or interact with can be measured."
                        },
                        {
                            "_id": "608bad96d08d3112188e0e53",
                            "type": "IAB_PURPOSE",
                            "name": "Measure content performance",
                            "description": "The performance and effectiveness of content that you see or interact with can be measured."
                        },
                        {
                            "_id": "608bad96d08d3112188e0e59",
                            "type": "IAB_PURPOSE",
                            "name": "Apply market research to generate audience insights",
                            "description": "Market research can be used to learn more about the audiences who visit sites/apps and view ads."
                        },
                        {
                            "_id": "608bad96d08d3112188e0e5f",
                            "type": "IAB_PURPOSE",
                            "name": "Develop and improve products",
                            "description": "Your data can be used to improve existing systems and software, and to develop new products"
                        },
                        {
                            "_id": "60b65857619abe242bed971e",
                            "type": "CUSTOM",
                            "name": "Our Custom Purpose",
                            "description": "It's a custom purpose/category for demo.",
                            "hasConsent": true,
                            "hasLegInt": false
                        },
                        {
                            "_id": "5e37fc3e973acf1e955b8966",
                            "name": "Use precise geolocation data",
                            "description": "Your precise geolocation data can be used in support of one or more purposes. This means your location can be accurate to within several meters."
                        }
                    ],
                    "site_id": 16893,
                    "language": "EN"
                },
                "url": "https://cdn.privacy-mgmt.com/index.html?consentUUID=46768728-d4f5-41ea-b311-5aae89c8d348&message_id=488398&consentLanguage=EN&preload_message=true",
                "messageMetaData": {
                    "messageId": 488398,
                    "prtnUUID": "3088e402-6f9d-452a-94f3-e69ff163f98c",
                    "msgDescription": "",
                    "bucket": 22,
                    "categoryId": 1,
                    "subCategoryId": 5
                }
            },
            {
                "type": "CCPA",
                "applies": true,
                "url": "https://cdn.privacy-mgmt.com/index.html?ccpaUUID=f706872e-ec42-4be0-880e-080485bc8a58&message_id=509690&consentLanguage=EN&preload_message=true",
                "userConsent": {
                    "dateCreated": "2021-06-16T08:43:32.447Z",
                    "newUser": false,
                    "rejectedCategories": [],
                    "rejectedVendors": [],
                    "rejectedAll": false,
                    "status": "consentedAll",
                    "signedLspa": false,
                    "uspstring": "1YNN"
                },
                "message": {
                    "message_json": {
                        "type": "Notice",
                        "name": "CCPA Message",
                        "settings": {},
                        "children": [
                            {
                                "type": "Text",
                                "name": "Text",
                                "settings": {
                                    "languages": {
                                        "EN": {
                                            "text": "<p>CCPA Message</p>"
                                        }
                                    },
                                    "text": "<p>CCPA Message</p>"
                                },
                                "children": []
                            },
                            {
                                "type": "Button",
                                "name": "Button",
                                "settings": {
                                    "languages": {
                                        "EN": {
                                            "text": "Show Options"
                                        }
                                    },
                                    "text": "Show Options",
                                    "choice_option": {
                                        "type": 12,
                                        "data": {
                                            "button_text": "1623758277257",
                                            "privacy_manager_iframe_url": "https://ccpa-notice.sp-prod.net/ccpa_pm/index.html?message_id=509688",
                                            "consent_origin": "https://ccpa-service.sp-prod.net"
                                        }
                                    }
                                },
                                "children": []
                            },
                            {
                                "type": "Button",
                                "name": "Button",
                                "settings": {
                                    "languages": {
                                        "EN": {
                                            "text": "Reject All"
                                        }
                                    },
                                    "text": "Reject All",
                                    "choice_option": {
                                        "type": 13,
                                        "data": {
                                            "button_text": "1623758165801",
                                            "consent_origin": "https://ccpa-service.sp-prod.net",
                                            "consent_language": "EN"
                                        }
                                    },
                                    "background": "#9a244f"
                                },
                                "children": []
                            },
                            {
                                "type": "Button",
                                "name": "Button",
                                "settings": {
                                    "languages": {
                                        "EN": {
                                            "text": "Accept All"
                                        }
                                    },
                                    "text": "Accept All",
                                    "choice_option": {
                                        "type": 11,
                                        "data": {
                                            "button_text": "1623758176582",
                                            "consent_origin": "https://ccpa-service.sp-prod.net",
                                            "consent_language": "EN"
                                        }
                                    },
                                    "background": "#4f7a28"
                                },
                                "children": []
                            }
                        ],
                        "compliance_status": false,
                        "compliance_list": []
                    },
                    "message_choice": [
                        {
                            "choice_id": 4322220,
                            "type": 15,
                            "iframe_url": null,
                            "button_text": "Dismiss"
                        },
                        {
                            "choice_id": 4322221,
                            "type": 12,
                            "iframe_url": "https://ccpa-notice.sp-prod.net/ccpa_pm/index.html?message_id=509688",
                            "button_text": "1623758277257"
                        },
                        {
                            "choice_id": 4322222,
                            "type": 13,
                            "iframe_url": null,
                            "button_text": "1623758165801"
                        },
                        {
                            "choice_id": 4322223,
                            "type": 11,
                            "iframe_url": null,
                            "button_text": "1623758176582"
                        }
                    ],
                    "site_id": 16893
                },
                "messageMetaData": {
                    "messageId": 509690,
                    "prtnUUID": "d94fa0b9-130e-45a7-a4fe-f48a70e09dc6",
                    "msgDescription": "",
                    "bucket": 567,
                    "categoryId": 2,
                    "subCategoryId": 1
                }
            }
        ],
        "localState": "{\"gdpr\":{\"mmsCookies\":[\"_sp_v1_uid=1:22:4d5fc654-ef04-4b38-a0db-378e91c0b888\",\"_sp_v1_data=2:338205:1623764240:0:13:0:13:0:0:_:-1\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_consent=1!0:-1:-1:-1:-1:-1\",\"_sp_v1_stage=\",\"_sp_v1_csv=null\",\"_sp_v1_lt=1:\"],\"uuid\":\"8fde743d-f79b-4dfc-8058-2cefec5c5021\",\"propertyId\":16893,\"messageId\":488398},\"ccpa\":{\"mmsCookies\":[\"_sp_v1_uid=1:567:d98bdb60-d5d4-46c8-b5c8-f98677a31b1b\",\"_sp_v1_data=2:358645:1623828787:0:7:0:7:0:0:_:-1\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_consent=1!0:-1:-1:-1:-1:-1\",\"_sp_v1_stage=\",\"_sp_v1_csv=null\",\"_sp_v1_lt=1:\"],\"uuid\":\"f706872e-ec42-4be0-880e-080485bc8a58\",\"dnsDisplayed\":true,\"propertyId\":16893,\"messageId\":509690}}"
    }
""".trimIndent()