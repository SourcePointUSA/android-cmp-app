package com.sourcepoint.cmplibrary.core.layout;

import android.graphics.Color;
import com.sourcepoint.cmplibrary.exception.GenericSDKException;
import com.sourcepoint.cmplibrary.exception.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NativeMessageAttrs {

    public final Attribute title;
    public final Attribute body;
    public final ArrayList<Action> actions;
    public final HashMap<String, String> customFields;

    public NativeMessageAttrs(JSONObject msgJSON, Logger logger) {
        title = new Attribute(getJson("title", msgJSON, logger), logger);
        body = new Attribute(getJson("body", msgJSON, logger), logger);
        actions = new ArrayList<>();//getActions(getJArray("actions", msgJSON, logger), logger);
        customFields = new HashMap<>(); //getHashMap(getJson("customFields", msgJSON, logger), logger);
    }


    public class Attribute {

        public final String text;
        public final Style style;
        public final HashMap<String, String> customFields;

        Attribute(JSONObject j, Logger logger)  {
            text = "";//getString("text", j, logger);
            style = new Style(getJson("style", j, logger), logger);
            customFields =  new HashMap<>();//getHashMap(getJson("customFields", j, logger), logger);
        }
    }

    public class Style {

        public final String  fontFamily;
        public final int fontSize;
        public final int color;
        public final int backgroundColor;

        Style(JSONObject styleJSON, Logger logger)  {
            fontFamily = "";//getString("fontFamily", styleJSON, logger);
            fontSize = 1;//getInt("fontSize", styleJSON, logger);
            color = Color.BLUE;////Color.parseColor(getSixDigitHexValue(getString("color", styleJSON, logger)));
            backgroundColor = Color.BLUE;//Color.parseColor(getSixDigitHexValue(getString("backgroundColor", styleJSON, logger)));
        }

        private String getSixDigitHexValue(String colorString){
            if (colorString.length() == 4)
                return colorString.replaceAll("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])", "#$1$1$2$2$3$3");
            return colorString;
        }
    }

    public class Action extends Attribute {
        Action(JSONObject actionJSON, Logger logger)  {
            super(actionJSON, logger);
            choiceId = 1;//getInt("choiceId", actionJSON, logger);
            choiceType = 1;//getInt("choiceType", actionJSON, logger);
        }
        public final int choiceType;
        public final int choiceId;
    }

    private ArrayList<Action> getActions(JSONArray jArray, Logger logger)  {
        ArrayList arr = new ArrayList<>();
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                //arr.add(new Action(getJson(i, jArray, logger), logger));
            }
        }
        return arr;
    }

    JSONObject getJson(String key, JSONObject j, Logger logger)  {
        try {
            return j.getJSONObject(key);
        } catch (JSONException e) {
            logger.error(new GenericSDKException(e, key + " missing from JSONObject"));
            throw new RuntimeException( key + " missing from JSONObject");
        }
    }



    JSONObject getJson(String strJson, Logger logger)  {
        try {
            return new JSONObject(strJson);
        } catch (JSONException e) {
            logger.error(new GenericSDKException(e, "Not possible to convert String to Json"));
            throw new RuntimeException("Not possible to convert String to Json");
        }
    }
}
