package fr.noopy.landru.quizz.model;

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
    public boolean check;
    public boolean answered;


    public void finalize() {

    }

    public Choice() {
        this.answered = false;
    }

    /**
     * Build a choice from json
     * @param data json representation of the choice
     */
    public Choice(JSONObject data) {
        try {
            if (data.has("objectId")) {
                this.objectId = data.getString("objectId");
            }
            if (data.has("text")) {
                this.text = data.getString("text");
            }
            if (data.has("scoring")) {
                this.scoring = data.getInt("scoring");
            }
            if (data.has("check")) {
                this.check = data.getBoolean("check");
            }
            if (data.has("answered")) {
                this.answered = data.getBoolean("answered");
            }
        } catch (JSONException err) {
            Log.w("Choice", err.getMessage());
        }
    }

    /**
     * Build a choice from http response
     * @param data data from http response
     */
    public Choice(HashMap data) {
        this();
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
            if (json.has("check")) {
                this.check = json.getBoolean("check");
            }
            if (json.has("answered")) {
                this.answered = json.getBoolean("answered");
            }
        } catch (JSONException err) {
            Log.e("Quiz", err.getMessage());
        }
    }

    /**
     * Transform the choice in a string representation (json)
     * @return string of the JSON representation
     */
    public String stringify() {
        return this.toJson().toString();
    }

    /**
     * Convert Choice into JSON
     * @return JSON representation of the choice
     */
    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        try {
            result.accumulate("objectId", this.objectId);
            result.accumulate("text", this.text);
            result.accumulate("scoring", this.scoring);
            result.accumulate("check", this.check);
            result.accumulate("answered", this.answered);
        } catch (JSONException err) {
            Log.w("Choice", err.getMessage());
        }
        return result;
    }
}
