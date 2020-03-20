package com.sourcepoint.gdpr_cmplibrary;

import android.graphics.Color;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getHashMap;
import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getJson;
import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getString;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class NativeMessageAttrsTest {


    private NativeMessageAttrs nativeMessageAttrs;
    private String jsonString = "{\"title\":{\"text\":\"Message Title\",\"style\":{\"fontFamily\":\"Arial\",\"fontSize\":\"34px\",\"color\":\"#000000\",\"backgroundColor\":\"#ffffff\"},\"customFields\":{\"fooTitle\":\"barTitle\"}},\"body\":{\"text\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam euismod fermentum tortor, eget commodo neque ullamcorper a. Etiam hendrerit sem velit, faucibus viverra justo viverra nec. Sed eu nulla et eros finibus egestas ut et ipsum. Nunc hendrerit metus eget ultrices pellentesque. Duis eget augue elit. Pellentesque ac ipsum dignissim, egestas urna eu, aliquam nunc. Ut vel maximus tellus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vivamus ac ornare nulla. Vestibulum molestie orci nec sollicitudin suscipit. Nulla imperdiet euismod nisl, sit amet aliquam nibh fermentum et. Etiam molestie imperdiet tellus, nec fringilla enim condimentum eu. Nullam congue metus lacus, sit amet vehicula lacus maximus non. Aenean vel ipsum sit amet justo finibus malesuada et id ex.\\n\\nMaecenas sit amet urna a mauris eleifend vehicula sed et est. Sed efficitur fringilla congue. In vitae malesuada mauris. Nunc malesuada, mi quis rutrum efficitur, nibh odio maximus dui, id tempor tellus arcu at nulla. Pellentesque rhoncus urna lacus, ac vestibulum lacus ullamcorper quis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Donec tincidunt ut nisl vitae iaculis. Suspendisse sit amet vulputate erat, non tristique turpis.\\n\\nDonec eget fermentum tortor. Mauris malesuada commodo ante, quis condimentum sem faucibus id. Ut aliquam aliquam tempus. Donec commodo ac enim nec elementum. Duis vehicula nunc a nunc tempus lobortis. Fusce ut faucibus neque. Nulla consequat feugiat hendrerit. Nunc et molestie nibh. Ut vitae dictum odio. Duis a dolor in dolor dictum pulvinar sed id lorem. Ut ac ornare velit, porta semper lacus.\\n\\nNam eget ipsum eget nibh euismod vulputate. Aenean metus tellus, tristique fermentum dictum in, aliquet non ante. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Quisque mollis, leo ac eleifend suscipit, velit ante hendrerit tortor, eleifend laoreet erat lectus eget eros. Vestibulum rutrum, ex vel efficitur ultricies, sem justo molestie ante, nec tempor urna justo vitae erat. Quisque eleifend rutrum ullamcorper. Sed mauris erat, rutrum ut condimentum in, ullamcorper ac massa. Suspendisse aliquet est nisi. Sed facilisis tortor vitae sapien lobortis, maximus faucibus lorem mollis. Morbi ultrices, nisi sed efficitur molestie, nisi magna ornare justo, quis congue neque nulla vitae magna. Nunc volutpat commodo tempus. Nullam scelerisque mauris erat, et hendrerit nibh commodo nec. Morbi at purus lacinia, auctor dolor nec, fringilla nulla. Duis aliquam nisi eu metus rutrum vehicula. Aenean fringilla in nisi eu aliquam. Donec tempus vel sapien sed dapibus.\\n\\nNullam rhoncus fermentum libero nec scelerisque. Phasellus id odio pharetra, pellentesque velit vel, vulputate libero. Duis efficitur finibus suscipit. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nullam accumsan lorem est, suscipit porttitor tortor semper a. Praesent suscipit quam urna, in tristique magna dignissim at. Integer dictum, odio vitae commodo faucibus, risus tortor convallis nunc, vel aliquam metus neque sed ex. Nullam et eleifend odio, nec eleifend ex. Aenean urna turpis, blandit vel eleifend sed, eleifend nec urna.\",\"style\":{\"fontFamily\":\"Verdana\",\"fontSize\":\"14px\",\"color\":\"#303030\",\"backgroundColor\":\"#ffffff\"},\"customFields\":{\"fooBody\":\"barBody\"}},\"actions\":[{\"text\":\"I Accept\",\"style\":{\"fontFamily\":\"Arial\",\"fontSize\":\"16px\",\"color\":\"#ffffff\",\"backgroundColor\":\"#1890ff\"},\"customFields\":{\"fooActionAccept\":\"barActionAccept\"},\"choiceType\":11,\"choiceId\":492690},{\"text\":\"I Reject\",\"style\":{\"fontFamily\":\"Arial\",\"fontSize\":\"16px\",\"color\":\"#585858\",\"backgroundColor\":\"#ebebeb\"},\"customFields\":{\"fooActionReject\":\"barActionReject\"},\"choiceType\":13,\"choiceId\":492691},{\"text\":\"Show Options\",\"style\":{\"fontFamily\":\"Arial\",\"fontSize\":\"16px\",\"color\":\"#1890ff\",\"backgroundColor\":\"#ffffff\"},\"customFields\":{\"fooActionShowOptions\":\"barActionShowOptions\"},\"choiceType\":12,\"choiceId\":492692},{\"text\":\"×\",\"style\":{\"fontFamily\":\"Gill Sans Extrabold, sans-serif\",\"fontSize\":\"24px\",\"color\":\"#fc7e7e\",\"backgroundColor\":\"#cecece\"},\"customFields\":{\"fooActionDismiss\":\"barActionDismiss\"},\"choiceType\":15,\"choiceId\":492689}],\"customFields\":{\"fooMessage\":\"barMessage\"}}";
    private JSONObject msgJSON ;

    @Before
    public void setUp() throws Exception{
        msgJSON = new JSONObject(jsonString);
        nativeMessageAttrs = new NativeMessageAttrs(msgJSON);

    }

    @Test
    public void titleFieldTextTest() throws Exception{
        JSONObject jsonObject = getJson("title",msgJSON);
        assertEquals  (nativeMessageAttrs.title.text , getString("text", jsonObject));
    }

    @Test
    public void titleFieldStyleTest() throws Exception{
        JSONObject jsonObject = getJson("title",msgJSON);
        JSONObject styleJson = getJson("style", jsonObject);
        assertEquals  (nativeMessageAttrs.title.style.fontFamily , getString("fontFamily", styleJson));
        assertEquals  (nativeMessageAttrs.title.style.fontSize , fontSizeFromPX(getString("fontSize", styleJson)));
        assertEquals  (nativeMessageAttrs.title.style.color , Color.parseColor(getString("color", styleJson)));
        assertEquals  (nativeMessageAttrs.title.style.backgroundColor , Color.parseColor(getString("backgroundColor", styleJson)));
    }

    private int fontSizeFromPX(String px){
        return Integer.parseInt(px.substring(0,px.length()-2));
    }

    @Test
    public void customFieldsFieldTest() throws Exception{
        assertEquals(nativeMessageAttrs.customFields , getHashMap(CustomJsonParser.getJson("customFields", msgJSON)));
    }
}
