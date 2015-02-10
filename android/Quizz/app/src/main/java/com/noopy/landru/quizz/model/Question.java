package com.noopy.landru.quizz.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cyrille on 10/02/15.
 */
public class Question {
    public String text;
    public String image;
    public String objectId;
    public String tags;
    public ArrayList<Choice> choices;
    public boolean multiAnswer;

    public Question() {
        this.choices = new ArrayList<Choice>();
    }

    public Question(HashMap data) {
        JSONObject json = new JSONObject(data);
        try {
            if (json.has("text")) {
                this.text = json.getString("text");
            }
            if (json.has("image")) {
                this.image = json.getString("image");
            }
            if (json.has("objectId")) {
                this.objectId = json.getString("objectId");
            }
            if (json.has("tags")) {
                this.tags = json.getString("tags");
            }
            if (json.has("multiAnswer")) {
                this.multiAnswer = json.getBoolean("multiAnswer");
            }
            ArrayList choicesData = (ArrayList)data.get("choices");
            this.choices = new ArrayList<Choice>();
            for (int i=0; i<choicesData.size(); i++) {
                this.choices.add(new Choice((HashMap)choicesData.get(i)));
            }
        } catch (JSONException err) {
            Log.e("Quiz", err.getMessage());
        }
    }

}
