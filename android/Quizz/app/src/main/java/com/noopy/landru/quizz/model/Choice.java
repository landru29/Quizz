package com.noopy.landru.quizz.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by cyrille on 10/02/15.
 */
public class Choice {
    public String objectId;
    public String text;
    public Integer scoring;

    public Choice() {
    }

    public Choice(HashMap data) {
        JSONObject json = new JSONObject(data);
        try {
            if (json.has("text")) {
                this.text = json.getString("text");
            }
            if (json.has("objectId")) {
                this.objectId = json.getString("objectId");
            }
            if (json.has("scoring")) {
                this.scoring = json.getInt("scoring");
            }
        } catch (JSONException err) {
            Log.e("Quiz", err.getMessage());
        }
    }
}
