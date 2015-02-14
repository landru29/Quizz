package fr.noopy.landru.quizz.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;

import fr.noopy.landru.quizz.tools.MarkdownProcessor;

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
    public boolean check;
    public String explaination;


    public void finalize() {
        choices.clear();
    }

    public Question() {
        this.choices = new ArrayList<Choice>();
    }

    /**
     * Build question from JSON
     * @param data json representation of the question
     */
    public Question(JSONObject data) {
        try {
            if (data.has("objectId")) {
                this.objectId = data.getString("objectId");
            }
            if (data.has("text")) {
                this.text = data.getString("text");
            }
            if (data.has("image")) {
                this.image = data.getString("image");
            }
            if (data.has("tags")) {
                this.tags = data.getString("tags");
            }
            if (data.has("explaination")) {
                this.explaination = data.getString("explaination");
            }
            if (data.has("check")) {
                this.check = data.getBoolean("check");
            }
            if (data.has("multiAnswer")) {
                this.multiAnswer = data.getBoolean("multiAnswer");
            }
            if (data.has("choices")) {
                JSONArray choicesData = data.getJSONArray("choices");
                this.choices = new ArrayList<Choice>();
                for (int i=0; i<choicesData.length(); i++) {
                    choices.add(new Choice(choicesData.getJSONObject(i)));
                }
            }
        } catch (JSONException err) {
            Log.w("Question", err.getMessage());
        }
    }

    /**
     * Build a question from http response
     * @param data http response
     */
    public Question(HashMap data) {
        this();
        MarkdownProcessor processor = new MarkdownProcessor();
        JSONObject json = new JSONObject(data);
        try {
            if (json.has("text")) {
                this.text = processor.toHtml(json.getString("text"));
            }
            if (json.has("image")) {
                this.image = json.getString("image");
                if (this.image.compareTo("null")==0) {
                    this.image = null;
                }
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
            if (json.has("check")) {
                this.check = json.getBoolean("check");
            }
            if (json.has("explaination")) {
                this.explaination = processor.toHtml(json.getString("explaination"));
            }
            ArrayList choicesData = (ArrayList)data.get("choices");
            for (int i=0; i<choicesData.size(); i++) {
                this.choices.add(new Choice((HashMap)choicesData.get(i)));
            }
        } catch (JSONException err) {
            Log.e("Quiz", err.getMessage());
        }
    }

    /**
     * Transform the question in a string representation (json)
     * @return string of the JSON representation
     */
    public String stringify() {
        return this.toJson().toString();
    }

    /**
     * Convert Question into JSON
     * @return JSON representation of the choice
     */
    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        try {
            result.accumulate("objectId", this.objectId);
            result.accumulate("image", this.image);
            result.accumulate("text", this.text);
            result.accumulate("check", this.check);
            result.accumulate("tags", this.tags);
            result.accumulate("multiAnswer", this.multiAnswer);
            result.accumulate("explaination", this.explaination);
            JSONArray jsonChoices = new JSONArray();
            for (Choice ch : this.choices) {
                jsonChoices.put(ch.toJson());
            }
            result.accumulate("choices", jsonChoices);
        } catch (JSONException err) {
            Log.w("Choice", err.getMessage());
        }
        return result;
    }

    /**
     * Prepare data to pass to the http request
     * @return data to pass to the http request
     */
    public HashMap toHashMap() {
        HashMap questionData = new HashMap();
        questionData.put("questionId", this.objectId);
        // build the answer array
        ArrayList<String> answer = new ArrayList<String>();
        for(Choice ch : this.choices) {
            if (ch.answered==true) {
                answer.add(ch.objectId);
            }
        }
        questionData.put("answer", answer);
        return questionData;
    }

}
