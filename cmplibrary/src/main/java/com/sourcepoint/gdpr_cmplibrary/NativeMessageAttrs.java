package com.sourcepoint.gdpr_cmplibrary;

import android.graphics.Color;

import com.sourcepoint.gdpr_cmplibrary.exception.Logger;
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

    public NativeMessageAttrs(JSONObject msgJSON, Logger logger) throws ConsentLibException {
        title = new Attribute(CustomJsonParser.getJson("title", msgJSON, logger), logger);
        body = new Attribute(CustomJsonParser.getJson("body", msgJSON, logger), logger);
        actions = getActions(getJArray("actions", msgJSON, logger), logger);
        customFields = getHashMap(CustomJsonParser.getJson("customFields", msgJSON, logger), logger);
    }


    public class Attribute {

        public final String text;
        public final Style style;
        public final HashMap<String, String> customFields;

        Attribute(JSONObject j, Logger logger) throws ConsentLibException {
            text = getString("text", j, logger);
            style = new Style(CustomJsonParser.getJson("style", j, logger), logger);
            customFields =  getHashMap(CustomJsonParser.getJson("customFields", j, logger), logger);
        }
    }

    public class Style {

        public final String  fontFamily;
        public final int fontSize;
        public final int color;
        public final int backgroundColor;

        Style(JSONObject styleJSON, Logger logger) throws ConsentLibException {
            fontFamily = getString("fontFamily", styleJSON, logger);
            fontSize = getInt("fontSize", styleJSON, logger);
            color = Color.parseColor(getSixDigitHexValue(getString("color", styleJSON, logger)));
            backgroundColor = Color.parseColor(getSixDigitHexValue(getString("backgroundColor", styleJSON, logger)));
        }

        private String getSixDigitHexValue(String colorString){
            if (colorString.length() == 4)
                return colorString.replaceAll("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])", "#$1$1$2$2$3$3");
            return colorString;
        }
    }

    public class Action extends Attribute {
        Action(JSONObject actionJSON, Logger logger) throws ConsentLibException {
            super(actionJSON, logger);
            choiceId = getInt("choiceId", actionJSON, logger);
            choiceType = getInt("choiceType", actionJSON, logger);
        }
        public final int choiceType;
        public final int choiceId;
    }

    private ArrayList<Action> getActions(JSONArray jArray, Logger logger) throws ConsentLibException {
        ArrayList arr = new ArrayList<>();
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                arr.add(new Action(getJson(i, jArray, logger), logger));
            }
        }
        return arr;
    }
}
