package com.sourcepoint.gdpr_cmplibrary;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getHashMap;
import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getInt;
import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getJArray;
import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getJson;
import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getString;


public class NativeMessageAttrs {

    public final Attribute title;
    public final Attribute body;
    public final ArrayList<Action> actions;
    public final HashMap<String, String> customFields;

    public NativeMessageAttrs(JSONObject msgJSON) throws ConsentLibException {
        title = new Attribute(CustomJsonParser.getJson("title", msgJSON));
        body = new Attribute(CustomJsonParser.getJson("body", msgJSON));
        actions = getActions(getJArray("actions", msgJSON));
        customFields = getHashMap(CustomJsonParser.getJson("customFields", msgJSON));
    }


    public class Attribute {

        public final String text;
        public final Style style;
        public final HashMap<String, String> customFields;

        Attribute(JSONObject j) throws ConsentLibException {
            text = getString("text", j);
            style = new Style(CustomJsonParser.getJson("style", j));
            customFields =  getHashMap(CustomJsonParser.getJson("customFields", j));
        }
    }

    public class Style {

        public final String  fontFamily;
        public final int fontSize;
        public final int color;
        public final int backgroundColor;

        Style(JSONObject styleJSON) throws ConsentLibException {
            fontFamily = getString("fontFamily", styleJSON);
            fontSize = fontSizeFromPX(getString("fontSize", styleJSON));
            color = Color.parseColor(getString("color", styleJSON));
            backgroundColor = Color.parseColor(getString("backgroundColor", styleJSON));

        }

        private int fontSizeFromPX(String px){
            return Integer.parseInt(px.substring(0,px.length()-2));
        }
    }

    public class Action extends Attribute {
        Action(JSONObject actionJSON) throws ConsentLibException {
            super(actionJSON);
            choiceId = getInt("choiceId", actionJSON);
            choiceType = getInt("choiceType", actionJSON);
        }
        public final int choiceType;
        public final int choiceId;
    }

    private ArrayList<Action> getActions(JSONArray jArray) throws ConsentLibException {
        ArrayList arr = new ArrayList<>();
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                arr.add(new Action(getJson(i, jArray)));
            }
        }
        return arr;
    }
}
