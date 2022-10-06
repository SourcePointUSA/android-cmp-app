package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.core.layout.model.toNativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.toConsentAction
import com.sourcepoint.cmplibrary.data.network.model.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.data.network.model.v7.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject

/**
 * Factory method to create an instance of a [JsonConverter] using its implementation
 * @return an instance of the [JsonConverterImpl] implementation
 */
internal fun JsonConverter.Companion.create(): JsonConverter = JsonConverterImpl()
internal val JsonConverter.Companion.converter: Json by lazy {
    Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
        explicitNulls = false
        prettyPrint = true
        prettyPrintIndent = "  "
        coerceInputValues = true
        useArrayPolymorphism = true
        allowSpecialFloatingPointValues = true
    }
}

/**
 * Implementation of the [JsonConverter] interface
 */
private class JsonConverterImpl : JsonConverter {

    override fun toUnifiedMessageResp(body: String): Either<UnifiedMessageResp> = check {
        body.toUnifiedMessageRespDto()
    }

    override fun toConsentAction(body: String): Either<ConsentActionImpl> = check {
        body.toConsentAction()
    }

    override fun toNativeMessageResp(body: String): Either<NativeMessageResp> = check {
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        val msgJSON = map.getMap("msgJSON") ?: fail("msgJSON")
        NativeMessageResp(msgJSON = JSONObject(msgJSON))
    }

    override fun toNativeMessageRespK(body: String): Either<NativeMessageRespK> = check {
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        val bean: NativeMessageDto = map.getMap("msgJSON")!!.toNativeMessageDto()
        NativeMessageRespK(msg = bean)
    }

    override fun toConsentResp(body: String, campaignType: CampaignType): Either<ConsentResp> = check {
        val obj = JSONObject(body)
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        val localState = map.getMap("localState")?.toJSONObj() ?: JSONObject()
        val uuid = map.getFieldValue<String>("uuid") ?: "invalid"
        obj.get("userConsent")
        ConsentResp(
            content = JSONObject(body),
            localState = localState.toString(),
            uuid = uuid,
            userConsent = obj["userConsent"].toString(),
            campaignType = campaignType
        )
    }

    override fun toCustomConsentResp(body: String): Either<CustomConsentResp> = check {
        val obj = JSONObject(body)
        CustomConsentResp(obj)
    }

    override fun toNativeMessageDto(body: String): Either<NativeMessageDto> = check {
        JSONObject(body).toTreeMap().toNativeMessageDto()
    }

    override fun toMetaDataRespResp(body: String): Either<MetaDataResp> = check {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toConsentStatusResp(body: String): Either<ConsentStatusResp> = check {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toPvDataResp(body: String): Either<PvDataResp> = check {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toMessagesResp(body: String): Either<MessagesResp> = check {
        JsonConverter.converter.decodeFromString(json)
    }

    /**
     * Util method to throws a [ConsentLibExceptionK] with a custom message
     * @param param name of the null object
     */
    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}

val json = """
    {
      "propertyId": 17801,
      "campaigns": {
        "GDPR": {
          "type": "GDPR",
          "message": {
            "message_json": {
              "type": "Notice",
              "name": "IAB TCFv2 Template - English      ",
              "settings": {
                "showClose": false,
                "defaultLanguage": "EN",
                "selectedLanguage": "EN",
                "padding": {
                  "paddingLeft": 10,
                  "paddingRight": 10,
                  "paddingTop": 8,
                  "paddingBottom": 8
                },
                "width": {
                  "type": "px",
                  "value": 600
                },
                "useBrowserDefault": false,
                "selected_privacy_manager": {
                  "type": 12,
                  "data": {
                    "button_text": "1625690173950",
                    "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                    "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2"
                  }
                },
                "languages": {
                  "EN": {
                    "iframeTitle": "<p>SP Consent Message</p>"
                  }
                },
                "iframeTitle": "<p>SP Consent Message</p>"
              },
              "children": [
                {
                  "type": "Row",
                  "name": "Row",
                  "settings": {
                    "padding": {
                      "paddingLeft": 0,
                      "paddingRight": 0,
                      "paddingTop": 0,
                      "paddingBottom": 0
                    },
                    "margin": {
                      "marginLeft": 0,
                      "marginRight": 0,
                      "marginTop": 0,
                      "marginBottom": 0
                    },
                    "selectedLanguage": "EN"
                  },
                  "children": [
                    {
                      "type": "Column",
                      "name": "Column",
                      "settings": {
                        "padding": {
                          "paddingLeft": 0,
                          "paddingRight": 0,
                          "paddingTop": 0,
                          "paddingBottom": 0
                        },
                        "margin": {
                          "marginLeft": 0,
                          "marginRight": 0,
                          "marginTop": 0,
                          "marginBottom": 0
                        },
                        "selectedLanguage": "EN",
                        "align": "center"
                      },
                      "children": [
                        {
                          "type": "Text",
                          "name": "Text",
                          "settings": {
                            "languages": {
                              "FR": {
                                "text": "<p><br></p>"
                              },
                              "EN": {
                                "text": "<p>Privacy Notice</p>"
                              }
                            },
                            "text": "<p>Privacy Notice</p>",
                            "font": {
                              "fontSize": 22,
                              "fontWeight": "600",
                              "color": "#000000",
                              "fontFamily": "arial, helvetica, sans-serif"
                            },
                            "padding": {
                              "paddingLeft": 15,
                              "paddingRight": 15,
                              "paddingTop": 5,
                              "paddingBottom": 5
                            },
                            "margin": {
                              "marginLeft": 10,
                              "marginRight": 10,
                              "marginTop": 0,
                              "marginBottom": 0
                            },
                            "selectedLanguage": "EN"
                          },
                          "children": []
                        },
                        {
                          "type": "Text",
                          "name": "Text",
                          "settings": {
                            "languages": {
                              "DE": {
                                "text": "<p>Wir benötigen Ihre Zustimmung zur Verwendung von Cookies und anderen Technologien durch uns und unsere Partner, um persönliche Daten auf Ihrem Gerät zu speichern und zu verarbeiten. Diese umfassen unter anderem Wiedererkennungsmerkmale, die dazu dienen Ihnen die bestmögliche Nutzererfahrung auf diesem Angebot zu ermöglichen. Im Folgenden finden Sie eine Übersicht zu welchen Zwecken wir Ihre Daten verarbeiten.</p>"
                              },
                              "ES": {
                                "text": "<p>[Client Name] y nuestros socios tecnológicos le solicitan su consentimiento para el uso de cookies para almacenar y acceder a datos personales en su dispositivo. Esto puede incluir el uso de identificadores únicos e información sobre sus patrones de navegación para crear la mejor experiencia de usuario posible en este sitio web. La siguiente descripción detalla cómo sus datos pueden ser utilizados por nosotros o por nuestros socios.</p>"
                              },
                              "FR": {
                                "text": "<p><br></p>"
                              },
                              "EN": {
                                "text": "<p>[Client Name] and our technology partners ask you to consent to the use of cookies to store and access personal data on your device. This can include the use of unique identifiers and information about your browsing patterns to create the best possible user experience on this website. The following description outlines how your data may be used by us, or by our partners.</p>"
                              }
                            },
                            "text": "<p>[Client Name] and our technology partners ask you to consent to the use of cookies to store and access personal data on your device. This can include the use of unique identifiers and information about your browsing patterns to create the best possible user experience on this website. The following description outlines how your data may be used by us, or by our partners.</p>",
                            "align": "left",
                            "font": {
                              "fontSize": 10,
                              "fontWeight": "400",
                              "color": "#000000",
                              "fontFamily": "verdana,geneva,sans-serif"
                            },
                            "margin": {
                              "marginLeft": 0,
                              "marginRight": 0,
                              "marginTop": 5,
                              "marginBottom": 5
                            },
                            "padding": {
                              "paddingLeft": 5,
                              "paddingRight": 5,
                              "paddingTop": 5,
                              "paddingBottom": 5
                            },
                            "selectedLanguage": "EN"
                          },
                          "children": []
                        },
                        {
                          "type": "Stacks",
                          "name": "Stacks",
                          "settings": {
                            "accordionsSpacing": {
                              "paddingLeft": 5,
                              "paddingRight": 32,
                              "paddingTop": 5,
                              "paddingBottom": 5
                            },
                            "panelSpacing": {
                              "paddingLeft": 10,
                              "paddingRight": 10,
                              "paddingTop": 5,
                              "paddingBottom": 5
                            },
                            "panelFont": {
                              "fontSize": 9,
                              "fontWeight": "400",
                              "color": "#555555",
                              "fontFamily": "verdana,geneva,sans-serif"
                            },
                            "accordionsFont": {
                              "fontSize": 10,
                              "fontWeight": "600",
                              "color": "#000000",
                              "fontFamily": "verdana,geneva,sans-serif"
                            },
                            "selectedLanguage": "EN"
                          },
                          "children": []
                        },
                        {
                          "type": "Text",
                          "name": "Text",
                          "settings": {
                            "languages": {
                              "DE": {
                                "text": "<p>Einige unserer <a href=\"${'$'}${'$'}${'$'}${'$'}:1588534395027\" target=\"_blank\">Partner</a> verarbeiten Ihre Daten auf Grundlage von berechtigtem Interesse, welches Sie jederzeit widerrufen können. Weitere Informationen zu den Datenverabeitungszwecken sowie unseren Partnern finden Sie unter “Einstellungen”.</p><p>&nbsp;</p><p>Es besteht keine Verpflichtung der Verarbeitung Ihrer Daten zuzustimmen, um dieses Angebot zu nutzen. Ohne Ihre Zustimmung können wir Ihnen keine Inhalte anzeigen, für die eine Personalisierung erforderlich ist. Sie können Ihre Auswahl jederzeit unter “Einstellungen” am Seitenende widerrufen oder anpassen. Ihre Auswahl wird ausschließlich auf dieses Angebot angewendet.</p>"
                              },
                              "ES": {
                                "text": "<p>Algunos de nuestros <a href=\"${'$'}${'$'}${'$'}${'$'}:1591230831832\" target=\"_blank\">socios</a> procesan datos personales sobre la base de intereses legítimos. Puede oponerse a dicho procesamiento en cualquier momento. Haga clic en \"Opciones\" a continuación para ver nuestra lista de socios y los fines para los que se requiere el consentimiento.</p><p>&nbsp;</p><p>No tiene que dar su consentimiento para ver la información en este sitio, pero si no lo hace, no estará disponible alguna personalización de contenido y publicidad. Sus elecciones en este sitio web se aplicarán solo a esta página. Puede cambiar su configuración en cualquier momento utilizando el enlace en la parte inferior de la página para volver a abrir las preferencias de privacidad y administrar la configuración.</p>"
                              },
                              "FR": {
                                "text": "<p><br></p>"
                              },
                              "EN": {
                                "text": "<p>Some of our <a href=\"${'$'}${'$'}${'$'}${'$'}:1591232922916\" target=\"_blank\">partners</a> process personal data on the basis of legitimate interest. You can object to such processing at any time. Please click “Options” below to view our list of partners and the purposes for which consent is required.</p><p>&nbsp;</p><p>You don’t have to consent in order to view the information on this site, but if you don’t consent, some personalization of content and advertising won’t be available. Your choices on this site will be applied only to this site. You can change your settings at any time by using the link at the bottom of the page to reopen the Privacy Preferences and managing the setting.</p>"
                              }
                            },
                            "text": "<p>Some of our <a href=\"${'$'}${'$'}${'$'}${'$'}:1591232922916\" target=\"_blank\">partners</a> process personal data on the basis of legitimate interest. You can object to such processing at any time. Please click “Options” below to view our list of partners and the purposes for which consent is required.</p><p>&nbsp;</p><p>You don’t have to consent in order to view the information on this site, but if you don’t consent, some personalization of content and advertising won’t be available. Your choices on this site will be applied only to this site. You can change your settings at any time by using the link at the bottom of the page to reopen the Privacy Preferences and managing the setting.</p>",
                            "choice_options": [
                              {
                                "type": 12,
                                "data": {
                                  "button_text": "1588528989117",
                                  "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350&pmTab=vendors",
                                  "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2"
                                }
                              },
                              {
                                "type": 12,
                                "data": {
                                  "button_text": "1588534395027",
                                  "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                                  "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2"
                                }
                              },
                              {
                                "type": 12,
                                "data": {
                                  "button_text": "1591230831832",
                                  "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                                  "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2"
                                }
                              },
                              {
                                "type": 12,
                                "data": {
                                  "button_text": "1591231308104",
                                  "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                                  "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2"
                                }
                              },
                              {
                                "type": 12,
                                "data": {
                                  "button_text": "1591232922916",
                                  "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                                  "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2"
                                }
                              }
                            ],
                            "margin": {
                              "marginLeft": 0,
                              "marginRight": 0,
                              "marginTop": 0,
                              "marginBottom": 0
                            },
                            "padding": {
                              "paddingLeft": 5,
                              "paddingRight": 5,
                              "paddingTop": 10,
                              "paddingBottom": 10
                            },
                            "font": {
                              "fontSize": 10,
                              "fontWeight": "400",
                              "color": "#000000",
                              "fontFamily": "verdana,geneva,sans-serif"
                            },
                            "selectedLanguage": "EN"
                          },
                          "children": [],
                          "handle": "global-font"
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "Row",
                  "name": "Row",
                  "settings": {
                    "margin": {
                      "marginLeft": 0,
                      "marginRight": 0,
                      "marginTop": 0,
                      "marginBottom": 0
                    },
                    "padding": {
                      "paddingLeft": 0,
                      "paddingRight": 0,
                      "paddingTop": 0,
                      "paddingBottom": 0
                    },
                    "align": "center",
                    "selectedLanguage": "EN"
                  },
                  "children": [
                    {
                      "type": "Column",
                      "name": "Column",
                      "settings": {
                        "align": "center",
                        "width": {
                          "type": "auto",
                          "value": 100
                        },
                        "padding": {
                          "paddingLeft": 0,
                          "paddingRight": 0,
                          "paddingTop": 0,
                          "paddingBottom": 0
                        },
                        "margin": {
                          "marginLeft": 0,
                          "marginRight": 0,
                          "marginTop": 0,
                          "marginBottom": 0
                        },
                        "selectedLanguage": "EN"
                      },
                      "children": [
                        {
                          "type": "Button",
                          "name": "Button",
                          "settings": {
                            "languages": {
                              "DE": {
                                "text": "Einstellungen"
                              },
                              "ES": {
                                "text": "Opciones"
                              },
                              "FR": {
                                "text": ""
                              },
                              "EN": {
                                "text": "Options"
                              }
                            },
                            "text": "Options",
                            "font": {
                              "fontSize": 14,
                              "fontWeight": "500",
                              "color": "#1890ff",
                              "fontFamily": "verdana,geneva,sans-serif"
                            },
                            "width": {
                              "type": "px",
                              "value": 225
                            },
                            "border": {
                              "borderWidth": 1,
                              "borderColor": "#1890ff",
                              "borderTopLeftRadius": 4,
                              "borderTopRightRadius": 4,
                              "borderBottomLeftRadius": 4,
                              "borderBottomRightRadius": 4,
                              "borderStyle": "solid"
                            },
                            "background": "#fffffff",
                            "choice_option": {
                              "type": 12,
                              "data": {
                                "button_text": "1588530813450",
                                "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2",
                                "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350"
                              }
                            },
                            "margin": {
                              "marginLeft": 10,
                              "marginRight": 10,
                              "marginTop": 5,
                              "marginBottom": 5
                            },
                            "padding": {
                              "paddingLeft": 15,
                              "paddingRight": 15,
                              "paddingTop": 10,
                              "paddingBottom": 10
                            },
                            "selectedLanguage": "EN"
                          },
                          "children": []
                        }
                      ]
                    },
                    {
                      "type": "Column",
                      "name": "Column",
                      "settings": {
                        "align": "center",
                        "width": {
                          "type": "auto",
                          "value": 100
                        },
                        "padding": {
                          "paddingLeft": 0,
                          "paddingRight": 0,
                          "paddingTop": 0,
                          "paddingBottom": 0
                        },
                        "margin": {
                          "marginLeft": 0,
                          "marginRight": 0,
                          "marginTop": 0,
                          "marginBottom": 0
                        },
                        "selectedLanguage": "EN"
                      },
                      "children": [
                        {
                          "type": "Button",
                          "name": "Button",
                          "settings": {
                            "font": {
                              "fontSize": 14,
                              "fontWeight": "400",
                              "color": "#ffffff",
                              "fontFamily": "verdana,geneva,sans-serif"
                            },
                            "border": {
                              "borderWidth": 1,
                              "borderColor": "#1890ff",
                              "borderTopLeftRadius": 4,
                              "borderTopRightRadius": 4,
                              "borderBottomLeftRadius": 4,
                              "borderBottomRightRadius": 4,
                              "borderStyle": "solid"
                            },
                            "width": {
                              "type": "px",
                              "value": 225
                            },
                            "choice_option": {
                              "type": 11,
                              "data": {
                                "button_text": "1588529705088",
                                "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2",
                                "consent_language": "EN"
                              }
                            },
                            "languages": {
                              "DE": {
                                "text": "Zustimmen"
                              },
                              "ES": {
                                "text": "Aceptar"
                              },
                              "FR": {
                                "text": ""
                              },
                              "EN": {
                                "text": "Accept"
                              }
                            },
                            "text": "Accept",
                            "padding": {
                              "paddingLeft": 15,
                              "paddingRight": 15,
                              "paddingTop": 10,
                              "paddingBottom": 10
                            },
                            "margin": {
                              "marginLeft": 10,
                              "marginRight": 10,
                              "marginTop": 5,
                              "marginBottom": 5
                            },
                            "selectedLanguage": "EN"
                          },
                          "children": []
                        }
                      ]
                    }
                  ],
                  "handle": "mobile-reverse"
                }
              ],
              "css": "@import url('https://fonts.googleapis.com/css?family=Open+Sans:400,600,700&display=swap');\n\n@media only screen and (max-width: 600px) {\n  .mobile-reverse {\n    flex-direction: column-reverse !important;\n    align-items: center !important;\n  }\n}\n\n.global-font p, .global-font button, .global-font div, .global-font span, .global-font input {\n    font-family: 'Open Sans', Helvetica, Arial, sans-serif !important;\n}",
              "handle": "global-font",
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
                "choice_id": 4486227,
                "type": 15,
                "iframe_url": null,
                "button_text": "Dismiss"
              },
              {
                "choice_id": 4486228,
                "type": 12,
                "iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350&pmTab=vendors",
                "button_text": "1588528989117"
              },
              {
                "choice_id": 4486229,
                "type": 12,
                "iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                "button_text": "1588534395027"
              },
              {
                "choice_id": 4486230,
                "type": 12,
                "iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                "button_text": "1591230831832"
              },
              {
                "choice_id": 4486231,
                "type": 12,
                "iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                "button_text": "1591231308104"
              },
              {
                "choice_id": 4486232,
                "type": 12,
                "iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                "button_text": "1591232922916"
              },
              {
                "choice_id": 4486233,
                "type": 12,
                "iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=521350",
                "button_text": "1588530813450"
              },
              {
                "choice_id": 4486234,
                "type": 11,
                "iframe_url": null,
                "button_text": "1588529705088"
              }
            ],
            "site_id": 17801,
            "language": "en"
          },
          "url": "https://cdn.privacy-mgmt.com/index.html?message_id=521357&consentLanguage=en&preload_message=true",
          "messageMetaData": {
            "messageId": 521357,
            "prtnUUID": "acc10281-503d-4ce7-b303-62683fada039",
            "msgDescription": "",
            "bucket": 588,
            "categoryId": 1,
            "subCategoryId": 5
          },
          "actions": [],
          "cookies": [],
          "euconsent": "CPf9mIAPf9mIAAGABCENCiCgAAAAAHAAAAYgAAAMZgAgMZADCgAQGMhwAIDGRIAEBjIA.YAAAAAAAAAAA",
          "grants": {
            "5f369a02b8e05c308701f829": {
              "vendorGrant": false,
              "purposeGrants": {
                "5fa9a8fb455f9533d77500fb": false,
                "5fa9a8fb455f9533d7750103": true,
                "5fa9a8fb455f9533d775010b": true,
                "5fa9a8fb455f9533d7750113": true,
                "5fa9a8fb455f9533d7750129": false,
                "5fa9a8fb455f9533d7750137": false,
                "5fa9a8fb455f9533d775013f": false
              }
            },
            "5f1aada6b8e05c306c0597d7": {
              "vendorGrant": false,
              "purposeGrants": {
                "5fa9a8fb455f9533d77500fb": false,
                "5fa9a8fb455f9533d7750103": false,
                "5fa9a8fb455f9533d775010b": false,
                "5fa9a8fb455f9533d7750113": false,
                "5fa9a8fb455f9533d775011b": false,
                "5fa9a8fb455f9533d7750122": false,
                "5fa9a8fb455f9533d7750129": false,
                "5fa9a8fb455f9533d7750137": false,
                "5fa9a8fb455f9533d775013f": false,
                "627c250c5abdfe0873e995ad": false
              }
            },
            "5e7ced57b8e05c485246cce5": {
              "vendorGrant": false,
              "purposeGrants": {
                "5fa9a8fb455f9533d77500fb": false,
                "5fa9a8fb455f9533d7750103": false,
                "5fa9a8fb455f9533d7750129": false,
                "5fa9a8fb455f9533d775013f": false
              }
            },
            "5e7ced56b8e05c4854221bb3": {
              "vendorGrant": false,
              "purposeGrants": {
                "5fa9a8fb455f9533d77500fb": false,
                "5fa9a8fb455f9533d7750103": false,
                "5fa9a8fb455f9533d775010b": false,
                "5fa9a8fb455f9533d7750113": false,
                "5fa9a8fb455f9533d775011b": false,
                "5fa9a8fb455f9533d7750122": false,
                "5fa9a8fb455f9533d7750129": false,
                "5fa9a8fb455f9533d7750131": false,
                "5fa9a8fb455f9533d7750137": false,
                "5fa9a8fb455f9533d775013f": false
              }
            },
            "5ebe70ceb8e05c43d547d7b4": {
              "vendorGrant": false,
              "purposeGrants": {
                "5fa9a8fb455f9533d77500fb": false,
                "5fa9a8fb455f9533d7750103": false,
                "5fa9a8fb455f9533d775010b": false,
                "5fa9a8fb455f9533d7750113": false,
                "5fa9a8fb455f9533d775011b": false
              }
            }
          },
          "addtlConsent": "1~",
          "customVendorsResponse": {
            "consentedVendors": [],
            "consentedPurposes": [],
            "legIntPurposes": [
              {
                "_id": "5fa9a8fb455f9533d7750103",
                "name": "Select basic ads"
              },
              {
                "_id": "5fa9a8fb455f9533d775010b",
                "name": "Create a personalised ads profile"
              },
              {
                "_id": "5fa9a8fb455f9533d7750113",
                "name": "Select personalised ads"
              }
            ]
          },
          "childPmId": null,
          "hasLocalData": false,
          "dateCreated": "2022-09-27T11:34:34.350Z",
          "consentStatus": {
            "rejectedAny": true,
            "rejectedLI": false,
            "consentedAll": false,
            "granularStatus": {
              "vendorConsent": "NONE",
              "vendorLegInt": "ALL",
              "purposeConsent": "NONE",
              "purposeLegInt": "ALL",
              "previousOptInAll": false,
              "defaultConsent": true
            },
            "hasConsentData": false,
            "consentedToAny": false
          },
          "TCData": {
            "IABTCF_AddtlConsent": "1~",
            "IABTCF_CmpSdkID": 6,
            "IABTCF_CmpSdkVersion": 2,
            "IABTCF_PolicyVersion": 2,
            "IABTCF_PublisherCC": "DE",
            "IABTCF_PurposeOneTreatment": 0,
            "IABTCF_UseNonStandardStacks": 0,
            "IABTCF_TCString": "CPf9mIAPf9mIAAGABCENCiCgAAAAAHAAAAYgAAAMZgAgMZADCgAQGMhwAIDGRIAEBjIA.YAAAAAAAAAAA",
            "IABTCF_VendorConsents": "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "IABTCF_VendorLegitimateInterests": "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "IABTCF_PurposeConsents": "0000000000",
            "IABTCF_PurposeLegitimateInterests": "0111000000",
            "IABTCF_SpecialFeaturesOptIns": "00",
            "IABTCF_PublisherConsent": "0000000000",
            "IABTCF_PublisherLegitimateInterests": "0000000000",
            "IABTCF_PublisherCustomPurposesConsents": "0000000000",
            "IABTCF_PublisherCustomPurposesLegitimateInterests": "0000000000",
            "IABTCF_gdprApplies": 1,
            "IABTCF_PublisherRestrictions2": "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "IABTCF_PublisherRestrictions3": "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "IABTCF_PublisherRestrictions4": "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
          }
        },
        "CCPA": {
          "type": "CCPA",
          "applies": true,
          "url": "https://cdn.privacy-mgmt.com/index.html?message_id=704632&consentLanguage=en&preload_message=true",
          "dateCreated": "2022-09-27T11:34:34.746Z",
          "newUser": true,
          "consentedAll": false,
          "rejectedCategories": [],
          "rejectedVendors": [],
          "rejectedAll": false,
          "status": "rejectedNone",
          "signedLspa": false,
          "uspstring": "1YNN",
          "message": {
            "message_json": {
              "type": "Notice",
              "name": "Do Not Sell - Bottom Notice (no veil)      ",
              "settings": {
                "showClose": false,
                "padding": {
                  "paddingLeft": 20,
                  "paddingRight": 20,
                  "paddingTop": 20,
                  "paddingBottom": 20
                },
                "width": {
                  "type": "px",
                  "value": 454
                },
                "type": "bottom",
                "showVeil": false,
                "background": "#ECECEC"
              },
              "children": [
                {
                  "type": "Row",
                  "name": "Actions Row",
                  "settings": {
                    "align": "center",
                    "margin": {
                      "marginLeft": 0,
                      "marginRight": 0,
                      "marginTop": 1,
                      "marginBottom": 1
                    },
                    "padding": {
                      "paddingLeft": 10,
                      "paddingRight": 10,
                      "paddingTop": 1,
                      "paddingBottom": 1
                    },
                    "vertical": "center"
                  },
                  "children": [
                    {
                      "type": "Column",
                      "name": "Message Body",
                      "settings": {
                        "width": {
                          "type": "%",
                          "value": 79
                        },
                        "margin": {
                          "marginLeft": 0,
                          "marginRight": 0,
                          "marginTop": 0,
                          "marginBottom": 0
                        },
                        "padding": {
                          "paddingLeft": 0,
                          "paddingRight": 0,
                          "paddingTop": 0,
                          "paddingBottom": 0
                        }
                      },
                      "children": [
                        {
                          "type": "Text",
                          "name": "Message Text",
                          "settings": {
                            "text": "<p>Cookies and other technologies are used on this site to offer users the best experience of relevant content, information and advertising. As a California resident you have the right to adjust what data we and our partner collect to optimize your experience.&nbsp;You can view more information on the specific categories of third parties that are used and which data is accessed and stored by clicking on \"Settings\". For more information, please review our <a href=\"www.privacypolicy.com\" target=\"_blank\">Privacy Policy</a>.</p>",
                            "margin": {
                              "marginLeft": 10,
                              "marginRight": 10,
                              "marginTop": 1,
                              "marginBottom": 1
                            },
                            "padding": {
                              "paddingLeft": 1,
                              "paddingRight": 0,
                              "paddingTop": 0,
                              "paddingBottom": 0
                            },
                            "choice_options": [
                              {
                                "type": 12,
                                "data": {
                                  "button_text": "1573484199354",
                                  "privacy_manager_iframe_url": "https://pm.cmp.sp-stage.net?privacy_manager_id=5da094ce8a88c000127b8da7",
                                  "consent_origin": "https://cmp.sp-stage.net"
                                }
                              }
                            ]
                          },
                          "children": []
                        }
                      ]
                    },
                    {
                      "type": "Button",
                      "name": "Do Not Sell Button",
                      "settings": {
                        "padding": {
                          "paddingLeft": 50,
                          "paddingRight": 50,
                          "paddingTop": 14,
                          "paddingBottom": 14
                        },
                        "text": "Settings",
                        "font": {
                          "fontSize": 16,
                          "fontWeight": "700",
                          "color": "#ffffff",
                          "fontFamily": "arial, helvetica, sans-serif"
                        },
                        "background": "#2f208b",
                        "choice_option": {
                          "type": 12,
                          "data": {
                            "button_text": "1625690580103",
                            "privacy_manager_iframe_url": "https://ccpa-notice.sp-prod.net/ccpa_pm/index.html?message_id=521354",
                            "consent_origin": "https://ccpa-service.sp-prod.net"
                          }
                        },
                        "border": {
                          "borderWidth": 2,
                          "borderColor": "#4228a2",
                          "borderTopLeftRadius": 5,
                          "borderTopRightRadius": 5,
                          "borderBottomLeftRadius": 5,
                          "borderBottomRightRadius": 5,
                          "borderStyle": "solid"
                        },
                        "width": {
                          "type": "px",
                          "value": 202
                        }
                      },
                      "children": []
                    },
                    {
                      "type": "Button",
                      "name": "Button",
                      "settings": {
                        "choice_option": {
                          "type": 11,
                          "data": {
                            "button_text": "1625690549764",
                            "consent_origin": "https://ccpa-service.sp-prod.net",
                            "consent_language": "EN"
                          }
                        },
                        "languages": {
                          "EN": {
                            "text": "Accept All"
                          }
                        },
                        "text": "Accept All"
                      },
                      "children": []
                    }
                  ]
                }
              ],
              "compliance_status": false,
              "compliance_list": []
            },
            "message_choice": [
              {
                "choice_id": 6878114,
                "type": 15,
                "iframe_url": null,
                "button_text": "Dismiss"
              },
              {
                "choice_id": 6878115,
                "type": 12,
                "iframe_url": "https://pm.cmp.sp-stage.net?privacy_manager_id=5da094ce8a88c000127b8da7",
                "button_text": "1573484199354"
              },
              {
                "choice_id": 6878116,
                "type": 12,
                "iframe_url": "https://ccpa-notice.sp-prod.net/ccpa_pm/index.html?message_id=521354",
                "button_text": "1625690580103"
              },
              {
                "choice_id": 6878117,
                "type": 11,
                "iframe_url": null,
                "button_text": "1625690549764"
              }
            ],
            "site_id": 17801
          },
          "messageMetaData": {
            "messageId": 704632,
            "prtnUUID": "dcfd5678-9cfc-4c71-9be3-2b006136a31f",
            "msgDescription": "",
            "bucket": 410,
            "categoryId": 2,
            "subCategoryId": 1
          }
        }
      },
      "errors": [],
      "localState": {
        "gdpr": {
          "mmsCookies": [
            "_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbLKK83J0YlRSkVil4AlqmtrlXTgyqKRGXkghkFtLC59OCWUYgEO1mB4eQAAAA%3D%3D"
          ],
          "propertyId": 17801,
          "messageId": 521357
        },
        "ccpa": {
          "mmsCookies": [
            "_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D"
          ],
          "propertyId": 17801,
          "messageId": 704632
        }
      },
      "nonKeyedLocalState": {
        "gdpr": {
          "_sp_v1_uid": "1:588:87135bf5-ec9b-483e-9a27-682430b4c010",
          "_sp_v1_data": "2:369163:1664278474:0:1:0:1:0:0:_:-1"
        },
        "ccpa": {
          "_sp_v1_uid": null,
          "_sp_v1_data": null
        }
      },
      "priority": [
        1,
        2,
        5
      ]
    }
""".trimIndent()
