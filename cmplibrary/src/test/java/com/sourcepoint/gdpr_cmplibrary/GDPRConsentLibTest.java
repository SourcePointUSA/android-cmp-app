package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class GDPRConsentLibTest {

    private GDPRConsentLib gdprConsentLib;
    private SharedPreferences sharedPreferences;

    private static final String CONSENT_UUID_KEY = "sp.gdpr.consentUUID";
    private static final String META_DATA_KEY = "sp.gdpr.metaData";
    private static final String AUTH_ID_KEY = "sp.gdpr.authId";
    private static final String EU_CONSENT__KEY = "sp.gdpr.euconsent";
    private static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";

    private String response = "{\n" +
            "    \"uuid\": \"20966736-442f-4e44-b31e-a73fc6faa553\",\n" +
            "    \"userConsent\": {\n" +
            "        \"acceptedCategories\": [],\n" +
            "        \"acceptedVendors\": []\n" +
            "    },\n" +
            "    \"meta\": \"{\\\"mmsCookies\\\":[\\\"_sp_v1_uid=1:534:a438e82f-57c8-47b8-bdcb-3007bce66f84;\\\",\\\"_sp_v1_csv=1;\\\",\\\"_sp_v1_lt=1:msg|true:;\\\",\\\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbLKK83J0YlRSkVil4AlqmtrlXTgyqKBjDwQw6A2FqfyWADYf5_yVwAAAA%3D%3D;\\\",\\\"_sp_v1_opt=1:;\\\",\\\"_sp_v1_data=2:72862:1584349885:0:1:0:1:0:0:800a5c7f-01ee-4cab-81ba-a78ce78c94a2:94046;\\\"],\\\"messageId\\\":\\\"94046\\\"}\",\n" +
            "    \"style\": \"{\\\"showClose\\\":true,\\\"padding\\\":{\\\"paddingLeft\\\":20,\\\"paddingRight\\\":20,\\\"paddingTop\\\":20,\\\"paddingBottom\\\":20},\\\"width\\\":{\\\"type\\\":\\\"px\\\",\\\"value\\\":454}}\",\n" +
            "    \"msgJSON\": {\n" +
            "        \"title\": {\n" +
            "            \"text\": \"Message Title\",\n" +
            "            \"style\": {\n" +
            "                \"fontFamily\": \"Arial\",\n" +
            "                \"fontSize\": \"34px\",\n" +
            "                \"color\": \"#000000\",\n" +
            "                \"backgroundColor\": \"#ffffff\"\n" +
            "            },\n" +
            "            \"customFields\": {\n" +
            "                \"fooTitle\": \"barTitle\"\n" +
            "            }\n" +
            "        },\n" +
            "        \"body\": {\n" +
            "            \"text\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam euismod fermentum tortor, eget commodo neque ullamcorper a. Etiam hendrerit sem velit, faucibus viverra justo viverra nec. Sed eu nulla et eros finibus egestas ut et ipsum. Nunc hendrerit metus eget ultrices pellentesque. Duis eget augue elit. Pellentesque ac ipsum dignissim, egestas urna eu, aliquam nunc. Ut vel maximus tellus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vivamus ac ornare nulla. Vestibulum molestie orci nec sollicitudin suscipit. Nulla imperdiet euismod nisl, sit amet aliquam nibh fermentum et. Etiam molestie imperdiet tellus, nec fringilla enim condimentum eu. Nullam congue metus lacus, sit amet vehicula lacus maximus non. Aenean vel ipsum sit amet justo finibus malesuada et id ex.\\n\\nMaecenas sit amet urna a mauris eleifend vehicula sed et est. Sed efficitur fringilla congue. In vitae malesuada mauris. Nunc malesuada, mi quis rutrum efficitur, nibh odio maximus dui, id tempor tellus arcu at nulla. Pellentesque rhoncus urna lacus, ac vestibulum lacus ullamcorper quis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Donec tincidunt ut nisl vitae iaculis. Suspendisse sit amet vulputate erat, non tristique turpis.\\n\\nDonec eget fermentum tortor. Mauris malesuada commodo ante, quis condimentum sem faucibus id. Ut aliquam aliquam tempus. Donec commodo ac enim nec elementum. Duis vehicula nunc a nunc tempus lobortis. Fusce ut faucibus neque. Nulla consequat feugiat hendrerit. Nunc et molestie nibh. Ut vitae dictum odio. Duis a dolor in dolor dictum pulvinar sed id lorem. Ut ac ornare velit, porta semper lacus.\\n\\nNam eget ipsum eget nibh euismod vulputate. Aenean metus tellus, tristique fermentum dictum in, aliquet non ante. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Quisque mollis, leo ac eleifend suscipit, velit ante hendrerit tortor, eleifend laoreet erat lectus eget eros. Vestibulum rutrum, ex vel efficitur ultricies, sem justo molestie ante, nec tempor urna justo vitae erat. Quisque eleifend rutrum ullamcorper. Sed mauris erat, rutrum ut condimentum in, ullamcorper ac massa. Suspendisse aliquet est nisi. Sed facilisis tortor vitae sapien lobortis, maximus faucibus lorem mollis. Morbi ultrices, nisi sed efficitur molestie, nisi magna ornare justo, quis congue neque nulla vitae magna. Nunc volutpat commodo tempus. Nullam scelerisque mauris erat, et hendrerit nibh commodo nec. Morbi at purus lacinia, auctor dolor nec, fringilla nulla. Duis aliquam nisi eu metus rutrum vehicula. Aenean fringilla in nisi eu aliquam. Donec tempus vel sapien sed dapibus.\\n\\nNullam rhoncus fermentum libero nec scelerisque. Phasellus id odio pharetra, pellentesque velit vel, vulputate libero. Duis efficitur finibus suscipit. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nullam accumsan lorem est, suscipit porttitor tortor semper a. Praesent suscipit quam urna, in tristique magna dignissim at. Integer dictum, odio vitae commodo faucibus, risus tortor convallis nunc, vel aliquam metus neque sed ex. Nullam et eleifend odio, nec eleifend ex. Aenean urna turpis, blandit vel eleifend sed, eleifend nec urna.\",\n" +
            "            \"style\": {\n" +
            "                \"fontFamily\": \"Verdana\",\n" +
            "                \"fontSize\": \"14px\",\n" +
            "                \"color\": \"#303030\",\n" +
            "                \"backgroundColor\": \"#ffffff\"\n" +
            "            },\n" +
            "            \"customFields\": {\n" +
            "                \"fooBody\": \"barBody\"\n" +
            "            }\n" +
            "        },\n" +
            "        \"actions\": [\n" +
            "            {\n" +
            "                \"text\": \"I Accept\",\n" +
            "                \"style\": {\n" +
            "                    \"fontFamily\": \"Arial\",\n" +
            "                    \"fontSize\": \"16px\",\n" +
            "                    \"color\": \"#ffffff\",\n" +
            "                    \"backgroundColor\": \"#1890ff\"\n" +
            "                },\n" +
            "                \"customFields\": {\n" +
            "                    \"fooActionAccept\": \"barActionAccept\"\n" +
            "                },\n" +
            "                \"choiceType\": 11,\n" +
            "                \"choiceId\": 492690\n" +
            "            },\n" +
            "            {\n" +
            "                \"text\": \"I Reject\",\n" +
            "                \"style\": {\n" +
            "                    \"fontFamily\": \"Arial\",\n" +
            "                    \"fontSize\": \"16px\",\n" +
            "                    \"color\": \"#585858\",\n" +
            "                    \"backgroundColor\": \"#ebebeb\"\n" +
            "                },\n" +
            "                \"customFields\": {\n" +
            "                    \"fooActionReject\": \"barActionReject\"\n" +
            "                },\n" +
            "                \"choiceType\": 13,\n" +
            "                \"choiceId\": 492691\n" +
            "            },\n" +
            "            {\n" +
            "                \"text\": \"Show Options\",\n" +
            "                \"style\": {\n" +
            "                    \"fontFamily\": \"Arial\",\n" +
            "                    \"fontSize\": \"16px\",\n" +
            "                    \"color\": \"#1890ff\",\n" +
            "                    \"backgroundColor\": \"#ffffff\"\n" +
            "                },\n" +
            "                \"customFields\": {\n" +
            "                    \"fooActionShowOptions\": \"barActionShowOptions\"\n" +
            "                },\n" +
            "                \"choiceType\": 12,\n" +
            "                \"choiceId\": 492692\n" +
            "            },\n" +
            "            {\n" +
            "                \"text\": \"×\",\n" +
            "                \"style\": {\n" +
            "                    \"fontFamily\": \"Gill Sans Extrabold, sans-serif\",\n" +
            "                    \"fontSize\": \"24px\",\n" +
            "                    \"color\": \"#fc7e7e\",\n" +
            "                    \"backgroundColor\": \"#cecece\"\n" +
            "                },\n" +
            "                \"customFields\": {\n" +
            "                    \"fooActionDismiss\": \"barActionDismiss\"\n" +
            "                },\n" +
            "                \"choiceType\": 15,\n" +
            "                \"choiceId\": 492689\n" +
            "            }\n" +
            "        ],\n" +
            "        \"customFields\": {\n" +
            "            \"fooMessage\": \"barMessage\"\n" +
            "        }\n" +
            "    }\n" +
            "}";


    @Before
    public void setUp() throws Exception {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());
        StoreClient storeClient = new StoreClient(sharedPreferences);

        JSONObject jsonResult = new JSONObject(response);
        ConsentLibBuilder consentLibBuilder = new ConsentLibBuilder(123, "example.com", 321, "abcd", mock(Activity.class), storeClient);
        consentLibBuilder.setOnConsentUIReady(v -> { });
        consentLibBuilder.setOnError(e -> {});
        gdprConsentLib = consentLibBuilder.build();
        gdprConsentLib.userConsent = new GDPRUserConsent(jsonResult.getJSONObject("userConsent"));
        gdprConsentLib.webView = mock(ConsentWebView.class, CALLS_REAL_METHODS);


    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void clearAllData() {
        gdprConsentLib.clearAllData();
        assertFalse(sharedPreferences.contains(CONSENT_UUID_KEY));
        assertFalse(sharedPreferences.contains(META_DATA_KEY));
        assertFalse(sharedPreferences.contains(EU_CONSENT__KEY));
        assertFalse(sharedPreferences.contains(AUTH_ID_KEY));
        assertFalse(sharedPreferences.contains(IAB_CONSENT_CONSENT_STRING));
    }

    @Test
    public void onAction_MSG_ACCEPT() throws ConsentLibException {
        GDPRConsentLib spyLib = spy(gdprConsentLib);

        spyLib.onAction(GDPRConsentLib.ActionTypes.MSG_ACCEPT , 1);

        verify(spyLib, times(1)).onMsgAccepted(1);
    }


    @Test
    public void onAction_MSG_SHOW_OPTIONS() throws ConsentLibException{
        GDPRConsentLib spyLib = spy(gdprConsentLib);

        spyLib.onAction(GDPRConsentLib.ActionTypes.MSG_SHOW_OPTIONS , 1);

        verify(spyLib, times(1)).onMsgShowOptions();
    }

    @Test
    public void onAction_MSG_CANCEL() {
        GDPRConsentLib spyLib = spy(gdprConsentLib);

        spyLib.onAction(GDPRConsentLib.ActionTypes.MSG_CANCEL , 1);

        verify(spyLib, times(1)).onMsgCancel(1);
    }

    @Test
    public void onAction_MSG_REJECT() throws ConsentLibException{
        GDPRConsentLib spyLib = spy(gdprConsentLib);

        spyLib.onAction(GDPRConsentLib.ActionTypes.MSG_REJECT , 1);

        verify(spyLib, times(1)).onMsgRejected(1);
    }

    @Test
    public void onAction_PM_DISMISS() {
        GDPRConsentLib spyLib = spy(gdprConsentLib);

        spyLib.onAction(GDPRConsentLib.ActionTypes.PM_DISMISS , 1);

        verify(spyLib, times(1)).onPmDismiss();
    }

    @Test(expected = ConsentLibException.class)
    public void onMsgAccepted() throws ConsentLibException {
        GDPRConsentLib spyLib = spy(gdprConsentLib);
        spyLib.onMsgAccepted(1);
        verify(spyLib,times(1)).closeAllViews();
        verify(spyLib,times(1)).sendConsent(GDPRConsentLib.ActionTypes.MSG_ACCEPT , 1);

    }

    @Test(expected = ConsentLibException.class)
    public void onMsgRejected() throws  ConsentLibException{
        GDPRConsentLib spyLib = spy(gdprConsentLib);
        spyLib.onMsgRejected(1);
        verify(spyLib ,times(1)).closeAllViews();
        verify(spyLib ,times(1)).sendConsent(GDPRConsentLib.ActionTypes.MSG_REJECT, 1);
    }


    @Test
    public void closeAllViews() {
        GDPRConsentLib spyLib = spy(gdprConsentLib);
        spyLib.closeAllViews();
        verify(spyLib ,times(1)).closeView(gdprConsentLib.webView);
    }

    @Test
    public void run() {
        GDPRConsentLib spyLib = spy(gdprConsentLib);
        spyLib.run();
        verify(spyLib, times(1)).run();
    }

}