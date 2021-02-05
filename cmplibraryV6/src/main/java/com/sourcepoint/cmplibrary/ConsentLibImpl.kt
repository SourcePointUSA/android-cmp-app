package com.sourcepoint.cmplibrary

import android.content.Context
import android.os.Build
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.sourcepoint.cmplibrary.core.web.ConsentWebView
import com.sourcepoint.cmplibrary.core.web.JSReceiver
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.PmUrlConfig
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.model.toMessageReq
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.ExecutorManager
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.gdpr_cmplibrary.* // ktlint-disable
import com.sourcepoint.gdpr_cmplibrary.exception.* // ktlint-disable
import org.json.JSONObject

internal class ConsentLibImpl(
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton,
    internal val campaign: Campaign,
    internal val pPrivacyManagerTab: PrivacyManagerTab,
    internal val context: Context,
    internal val pLogger: Logger,
    internal val pJsonConverter: JsonConverter,
    internal val pConnectionManager: ConnectionManager,
    internal val service: Service,
    private val viewManager: ViewsManager,
    private val executor: ExecutorManager
) : ConsentLib {

    override var spClient: SpClient? = null
    private val nativeMsgClient by lazy { NativeMsgDelegate() }

    /** Start Client's methods */
    override fun loadMessage(authId: String) {
        checkMainThread("loadMessage")
        throwsExceptionIfClientNoSet()
        service.getMessage(
            messageReq = campaign.toMessageReq(),
            pSuccess = { messageResp ->
                println()
            },
            pError = { throwable ->
                println()
            }
        )
    }

    override fun loadMessage() {
        checkMainThread("loadMessage")
        throwsExceptionIfClientNoSet()
        /**
         * Test with webview
         */
//        val webView = viewManager.createWebView(this)
//        webView?.run {
//            onError = { consentLibException -> }
//            onNoIntentActivitiesFoundFor = { url -> }
//        }
//
//        (webView as? ConsentWebView)?.let {
//            it.settings
//            showView(it)
//            it.loadConsentUIFromUrl(urlManager.urlLocalTest())
// //            Handler(context.mainLooper).postDelayed({injectData(it)}, 500)
//
//        } ?: throw RuntimeException("webView is not a ConsentWebView")
        /**
         * Test with webview
         */

        service.getMessage(
            messageReq = campaign.toMessageReq(),
            pSuccess = { messageResp ->
//                spClient?.onConsentUIReady(View(context))
                spClient?.onAction(ActionTypes.REJECT_ALL)
            },
            pError = { throwable -> spClient?.onError(throwable.toConsentLibException()) }
        )
    }

    override fun loadMessage(nativeMessage: NativeMessage) {
        checkMainThread("loadMessage")
        throwsExceptionIfClientNoSet()

        service.getNativeMessage(
            campaign.toMessageReq(),
            { messageResp ->
                val jsonResult = messageResp.msgJSON
                executor.executeOnMain {
                    /** configuring onClickListener and set the parameters */
                    nativeMessage.setAttributes(NativeMessageAttrs(jsonResult, pLogger))
                    /** set the action callback */
                    nativeMessage.setActionClient(nativeMsgClient)
                    /** calling the client */
                    spClient?.onConsentUIReady(nativeMessage)
                }
            },
            { throwable -> pLogger.error(throwable.toConsentLibException()) }
        )
    }

    override fun loadMessage(authId: String, nativeMessage: NativeMessage) {
        checkMainThread("loadMessage")
        throwsExceptionIfClientNoSet()
    }

    override fun loadPrivacyManager() {
        checkMainThread("loadPrivacyManager")
        throwsExceptionIfClientNoSet()
        val webView = viewManager.createWebView(this)
        webView?.run {
            onError = { consentLibException -> }
            onNoIntentActivitiesFoundFor = { url -> }
        }
        val pmConfig = PmUrlConfig(
            consentUUID = "89b2d14b-70ee-4344-8cc2-1b7b281d0f2d",
            siteId = "7639",
            messageId = campaign.pmId
        )
        webView?.loadConsentUIFromUrl(urlManager.urlPm(pmConfig))
    }

    override fun loadPrivacyManager(authId: String) {
        checkMainThread("loadPrivacyManager")
        throwsExceptionIfClientNoSet()
    }

    override fun showView(view: View) {
        checkMainThread("showView")
        viewManager.showView(view)
    }

    override fun removeView(view: View?) {
        checkMainThread("removeView")
        viewManager.removeView(view)
    }

    /** end Client's methods */

//    private fun createWebView(): com.sourcepoint.cmplibrary.core.web.ConsentWebView {
//        return ConsentWebView(
//            context = context,
//            connectionManager = pConnectionManager,
//            jsReceiver = JSReceiverDelegate(),
//            logger = pLogger
//        )
//    }

    /** Start Receiver methods */
    inner class JSReceiverDelegate() : JSReceiver {

        override var wv: ConsentWebView? = null

        @JavascriptInterface
        override fun log(tag: String?, msg: String?) {
            println("===================tag [$tag]: $msg=============================")
        }

        @JavascriptInterface
        override fun log(msg: String?) {
            println("===================== msg [$msg] ===========================")
        }

        @JavascriptInterface
        override fun onConsentUIReady(isFromPM: Boolean) {
            wv?.let { viewManager.showView(it) } ?: throw GenericSDKException(description = "WebView is null")
        }

        @JavascriptInterface
        override fun onAction(actionData: String) {
            pJsonConverter
                .toConsentAction(actionData)
                .map { onActionFromWebViewClient(it) }
        }

        @JavascriptInterface
        override fun onError(errorMessage: String) {
            spClient?.onError(GenericSDKException(description = errorMessage))
            pLogger.error(RenderingAppException(description = errorMessage, pCode = errorMessage))
        }
    }

    /** End Receiver methods */

    private fun throwsExceptionIfClientNoSet() {
        spClient ?: throw MissingClientException(description = "spClient instance is missing")
    }

    /**
     * Receive the action performed by the user from the WebView
     */
    internal fun onActionFromWebViewClient(action: ConsentAction) {
        executor.executeOnMain { spClient?.onAction(action.actionType) }
        when (action.actionType) {
            ActionTypes.ACCEPT_ALL -> {
            }
            ActionTypes.MSG_CANCEL -> {
            }
            ActionTypes.SAVE_AND_EXIT -> {
            }
            ActionTypes.SHOW_OPTIONS -> {
            }
            ActionTypes.REJECT_ALL -> {
            }
            ActionTypes.PM_DISMISS -> {
            }
        }
    }

    /**
     * Delegate used by the [NativeMessage] to catch events performed by the user
     */
    inner class NativeMsgDelegate : NativeMessageClient {
        /**
         * onclick listener connected to the acceptAll button in the NativeMessage View
         */
        override fun onClickAcceptAll(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
            spClient?.onAction(ActionTypes.ACCEPT_ALL)
        }

        /**
         * onclick listener connected to the RejectAll button in the NativeMessage View
         */
        override fun onClickRejectAll(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
            spClient?.onAction(ActionTypes.REJECT_ALL)
        }

        override fun onPmDismiss(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {}

        /**
         * onclick listener connected to the ShowOptions button in the NativeMessage View
         */
        override fun onClickShowOptions(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
            spClient?.onAction(ActionTypes.SHOW_OPTIONS)
        }

        /**
         * onclick listener connected to the Cancel button in the NativeMessage View
         */
        override fun onClickCancel(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
            spClient?.onAction(ActionTypes.MSG_CANCEL)
        }

        override fun onDefaultAction(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
        }
    }

    private fun injectData(webView: WebView) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(
                """
                    window.postMessage({ name: 'sp.loadMessage', message_json: ${JSONObject(json)}})
                """.trimIndent()
            ) { res ->
//                if (res == "null") {
//                    injectDataOldApi(webView)
//                }
            }
        }
    }

    private fun injectDataOldApi(webView: WebView) {
        val sp = webView.context.getSharedPreferences("webview", Context.MODE_PRIVATE)
        val authKey = "isAuthIdSet"
//        if (!sp.contains(authKey)) {
        webView.loadUrl("window.postMessage({ name: 'sp.loadMessage', message_json: $json});")
        sp.edit().putBoolean(authKey, true).apply()
        webView.loadUrl(webView.url)
//        }
    }

    val json = """
            {
            "type": "Notice",
            "name": "TCFv2 Basic Modal ",
            "settings": {
              "showClose": true,
              "useBrowserDefault": true,
              "width": {
                "type": "px",
                "value": 600
              },
              "border": {
                "borderWidth": 1,
                "borderColor": "#ffffff",
                "borderTopLeftRadius": 0,
                "borderTopRightRadius": 0,
                "borderBottomLeftRadius": 0,
                "borderBottomRightRadius": 0,
                "borderStyle": "solid"
              },
              "defaultLanguage": "EN",
              "selectedLanguage": "EN",
              "closeAlign": "right",
              "closeFont": {
                "fontSize": 24,
                "fontWeight": "800",
                "color": "#999999",
                "fontFamily": "tahoma,geneva,sans-serif"
              },
              "useSafeArea": true
            },
            "children": [
              {
                "type": "Row",
                "name": "Row",
                "settings": {
                  "align": "space-between",
                  "padding": {
                    "paddingLeft": 8,
                    "paddingRight": 8,
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
                    "type": "Text",
                    "name": "Text",
                    "settings": {
                      "languages": {
                        "EN": {
                          "text": "<p>Privacy Notice</p>"
                        },
                        "ES": {
                          "text": "<p>Aviso de Privacidad</p>"
                        },
                        "DE": {
                          "text": "<p>Datenschutzerklärung</p>"
                        },
                        "FR": {
                          "text": "<p>Avis de Confidentialité</p>"
                        }
                      },
                      "text": "<p>Privacy Notice</p>",
                      "font": {
                        "fontSize": 32,
                        "fontWeight": "700",
                        "color": "#000000",
                        "fontFamily": "arial, helvetica, sans-serif"
                      },
                      "padding": {
                        "paddingLeft": 0,
                        "paddingRight": 0,
                        "paddingTop": 10,
                        "paddingBottom": 10
                      },
                      "margin": {
                        "marginLeft": 0,
                        "marginRight": 0,
                        "marginTop": 10,
                        "marginBottom": 10
                      },
                      "selectedLanguage": "EN"
                    },
                    "children": [],
                    "handle": ""
                  },
                  {
                    "type": "Image",
                    "name": "Image",
                    "settings": {
                      "width": {
                        "type": "px",
                        "value": 184
                      },
                      "url": "https://www.sourcepoint.com/wp-content/themes/sourcepoint/assets/svg/logo.svg",
                      "margin": {
                        "marginLeft": 10,
                        "marginRight": 10,
                        "marginTop": 10,
                        "marginBottom": 10
                      },
                      "height": {
                        "type": "px",
                        "value": 55
                      }
                    },
                    "children": [],
                    "handle": "logo"
                  }
                ],
                "handle": "header-row"
              },
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
                    "type": "Text",
                    "name": "Text",
                    "settings": {
                      "languages": {
                        "EN": {
                          "text": "<p>SourcePoint and our technology partners ask you to consent to the use, store and access of personal data on your device. This can include the use of unique identifiers and information about your browsing patterns to create the best possible user experience on our app. The following description outlines how your data may be used by us, or by our partners.</p><p>&nbsp;</p><p>Some of our partners process personal data on the basis of legitimate interest. You can object to such processing at any time. Please click “Options” below to view our list of partners and the purposes for which consent is required.</p><p><br></p><p>You can view our <a href=\"https://sourcepoint.com\" target=\"_blank\">Privacy Policy</a> for more information.</p>"
                        },
                        "ES": {
                          "text": "<p>[Client Name] y nuestros socios tecnológicos le solicitan su consentimiento para el uso de cookies para almacenar y acceder a datos personales en su dispositivo. Esto puede incluir el uso de identificadores únicos e información sobre sus patrones de navegación para crear la mejor experiencia de usuario posible en este sitio web. La siguiente descripción detalla cómo sus datos pueden ser utilizados por nosotros o por nuestros socios.</p><p>&nbsp;</p><p>Algunos de nuestros <u>socios</u> procesan datos personales sobre la base de intereses legítimos. Puede oponerse a dicho procesamiento en cualquier momento. Haga clic en \"Opciones\" a continuación para ver nuestra lista de socios y los fines para los que se requiere el consentimiento.</p><p>&nbsp;</p><p>No tiene que dar su consentimiento para ver la información en este sitio, pero si no lo hace, no estará disponible alguna personalización de contenido y publicidad. Sus elecciones en este sitio web se aplicarán solo a esta página. Puede cambiar su configuración en cualquier momento utilizando el enlace en la parte inferior de la página para volver a abrir las preferencias de privacidad y administrar la configuración.</p>"
                        },
                        "DE": {
                          "text": "<p>Wir benötigen Ihre Zustimmung zur Verwendung von Cookies und anderen Technologien durch uns und unsere Partner, um persönliche Daten auf Ihrem Gerät zu speichern und zu verarbeiten. Diese umfassen unter anderem Wiedererkennungsmerkmale, die dazu dienen Ihnen die bestmögliche Nutzererfahrung auf diesem Angebot zu ermöglichen. Im Folgenden finden Sie eine Übersicht zu welchen Zwecken wir Ihre Daten verarbeiten.</p><p>&nbsp;</p><p>Einige unserer <u>Partner</u> verarbeiten Ihre Daten auf Grundlage von berechtigtem Interesse, welches Sie jederzeit widerrufen können. Weitere Informationen zu den Datenverabeitungszwecken sowie unseren Partnern finden Sie unter “Einstellungen”.</p><p>&nbsp;</p><p>Es besteht keine Verpflichtung der Verarbeitung Ihrer Daten zuzustimmen, um dieses Angebot zu nutzen. Ohne Ihre Zustimmung können wir Ihnen keine Inhalte anzeigen, für die eine Personalisierung erforderlich ist. Sie können Ihre Auswahl jederzeit unter unter “Einstellungen” am Seitenende widerrufen oder anpassen. Ihre Auswahl wird ausschließlich auf dieses Angebot angewendet.</p>"
                        },
                        "FR": {
                          "text": "<p>[Nom du client] et nos partenaires technologiques vous demandent de consentir à l'utilisation de cookies pour stocker et accéder à des données personnelles sur votre appareil. Cela peut inclure l'utilisation d'identifiants uniques et d'informations sur vos habitudes de navigation afin de créer la meilleure expérience possible pour l'utilisateur sur ce site web. La description suivante décrit comment vos données peuvent être utilisées par nous, ou par nos partenaires.</p><p><br></p><p>Certains de nos <u>partenaires</u> traitent les données personnelles sur la base d'un intérêt légitime. Vous pouvez vous opposer à ce traitement à tout moment. Veuillez cliquer sur \"Options\" ci-dessous pour consulter la liste de nos partenaires et les finalités pour lesquelles le consentement est requis.</p><p>&nbsp;</p><p>Vous n'avez pas à donner votre consentement pour consulter les informations sur ce site, mais si vous ne le faites pas, vous ne pourrez pas personnaliser le contenu et la publicité. Vos choix sur ce site ne seront appliqués qu'à ce site. Vous pouvez modifier vos paramètres à tout moment en utilisant le lien en bas de la page pour rouvrir les préférences de confidentialité et gérer le paramètre.</p>"
                        }
                      },
                      "text": "<p>SourcePoint and our technology partners ask you to consent to the use, store and access of personal data on your device. This can include the use of unique identifiers and information about your browsing patterns to create the best possible user experience on our app. The following description outlines how your data may be used by us, or by our partners.</p><p>&nbsp;</p><p>Some of our partners process personal data on the basis of legitimate interest. You can object to such processing at any time. Please click “Options” below to view our list of partners and the purposes for which consent is required.</p><p><br></p><p>You can view our <a href=\"https://sourcepoint.com\" target=\"_blank\">Privacy Policy</a> for more information.</p>",
                      "padding": {
                        "paddingLeft": 9,
                        "paddingRight": 8,
                        "paddingTop": 8,
                        "paddingBottom": 8
                      },
                      "margin": {
                        "marginLeft": 0,
                        "marginRight": 0,
                        "marginTop": 0,
                        "marginBottom": 0
                      },
                      "selectedLanguage": "EN"
                    },
                    "children": [],
                    "handle": ""
                  }
                ]
              },
              {
                "type": "Stacks",
                "name": "Stacks",
                "settings": {
                  "accordionsSpacing": {
                    "paddingLeft": 16,
                    "paddingRight": 16,
                    "paddingTop": 16,
                    "paddingBottom": 16
                  },
                  "selectedLanguage": "EN"
                },
                "children": [],
                "handle": ""
              },
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
                  "align": "flex-end",
                  "selectedLanguage": "EN"
                },
                "children": [
                  {
                    "type": "Button",
                    "name": "Button",
                    "settings": {
                      "languages": {
                        "EN": {
                          "text": "Options"
                        },
                        "ES": {
                          "text": "Opciones"
                        },
                        "DE": {
                          "text": "Einstellungen"
                        },
                        "FR": {
                          "text": "Options"
                        }
                      },
                      "text": "Options",
                      "choice_option": {
                        "type": 12,
                        "data": {
                          "button_text": "1598878679394",
                          "privacy_manager_iframe_url": "https://notice.sp-prod.net/privacy-manager/index.html?message_id=122058&pmTab=features",
                          "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2"
                        }
                      },
                      "font": {
                        "fontSize": 14,
                        "fontWeight": "700",
                        "color": "#000000",
                        "fontFamily": "arial, helvetica, sans-serif"
                      },
                      "border": {
                        "borderWidth": 1,
                        "borderColor": "#000000",
                        "borderTopLeftRadius": 0,
                        "borderTopRightRadius": 0,
                        "borderBottomLeftRadius": 0,
                        "borderBottomRightRadius": 0,
                        "borderStyle": "solid"
                      },
                      "background": "#ffffff",
                      "selectedLanguage": "EN"
                    },
                    "children": [],
                    "handle": ""
                  },
                  {
                    "type": "Button",
                    "name": "Reject",
                    "settings": {
                      "languages": {
                        "EN": {
                          "text": "Reject"
                        }
                      },
                      "text": "Reject",
                      "choice_option": {
                        "type": 13,
                        "data": {
                          "button_text": "1592840139599",
                          "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2",
                          "consent_language": "EN"
                        }
                      },
                      "padding": {
                        "paddingLeft": 18,
                        "paddingRight": 18,
                        "paddingTop": 10,
                        "paddingBottom": 10
                      },
                      "border": {
                        "borderWidth": 1,
                        "borderColor": "#7b0f08",
                        "borderTopLeftRadius": 0,
                        "borderTopRightRadius": 0,
                        "borderBottomLeftRadius": 0,
                        "borderBottomRightRadius": 0,
                        "borderStyle": "solid"
                      },
                      "background": "#ff1600"
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
                          "button_text": "1589214494409",
                          "consent_origin": "https://sourcepoint.mgr.consensu.org/tcfv2",
                          "consent_language": "EN"
                        }
                      },
                      "languages": {
                        "EN": {
                          "text": "Accept"
                        },
                        "ES": {
                          "text": "Aceptar"
                        },
                        "DE": {
                          "text": "Akzeptieren"
                        },
                        "FR": {
                          "text": "J'accepte"
                        }
                      },
                      "text": "Accept",
                      "font": {
                        "fontSize": 14,
                        "fontWeight": "700",
                        "color": "#fff",
                        "fontFamily": "arial, helvetica, sans-serif"
                      },
                      "border": {
                        "borderWidth": 1,
                        "borderColor": "#008000",
                        "borderTopLeftRadius": 0,
                        "borderTopRightRadius": 0,
                        "borderBottomLeftRadius": 0,
                        "borderBottomRightRadius": 0,
                        "borderStyle": "solid"
                      },
                      "background": "#61B329\t",
                      "selectedLanguage": "EN",
                      "padding": {
                        "paddingLeft": 18,
                        "paddingRight": 18,
                        "paddingTop": 10,
                        "paddingBottom": 10
                      }
                    },
                    "children": [],
                    "handle": "buttons-row"
                  }
                ],
                "handle": "bottom-row"
              }
            ],
            "css": ".stack .accordion .chevron { right: 12px !important; left: auto; }\n\n\n/*Stack Customizations - update default properties as needed*/\n.stack { background-color: #fff !important; }\n.stack .accordion .chevron { color: #000000 !important; }\n.accordion { font-family: arial, helvetica, sans-serif !important; color: #000000 !important; }\n.message-stacks .panel p { font-family: arial, helvetica, sans-serif !important; color: #555555 !important;}\n.message-stacks .panel { border-bottom: 1px solid #e5e9ee !important }\n\n@media only screen and (max-width: 600px) {\n.bottom-row { display: flex; flex-direction: row !important; justify-content: center !important; } \n.header-row { display: flex !important; flex-direction: row !important; }\n.logo { left: 35px !important; right: auto; }\n}",
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
            ],
            "compliance_status": true
          }
    """.trimIndent()
}
