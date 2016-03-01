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
    public String textHtml;
    public String image;
    public String id;
    public String tags;
    public ArrayList<Choice> choices;
    public boolean multiAnswer;
    public String explaination;
    public String explainationHtml;


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
                this.id = data.getString("id");
            }
            if (data.has("text")) {
                this.text = data.getString("text");
            }
            if (data.has("imageUrl")) {
                this.image = data.getString("imageUrl");
            }
            if (data.has("tags")) {
                this.tags = data.getString("tags");
            }
            if (data.has("explaination")) {
                this.explaination = data.getString("explaination");
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
            markdown();
        } catch (JSONException err) {
            Log.w("Question", err.getMessage());
        }
    }

    /**
     * Apply markdown on the question
     */
    private void markdown() {
        MarkdownProcessor processor = new MarkdownProcessor();
        this.explainationHtml = processor.toHtml(this.explaination);
        this.textHtml = processor.toHtml(this.text);
    }

    /**
     * Check if the question was correctly answered
     * @return result of the question
     */
    public boolean check() {
        boolean result = true;
        for (final Choice ch : choices) {
            result = result && ((ch.scoring>0) == ch.answered);
        }
        return result;
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
            result.accumulate("id", this.id);
            result.accumulate("imageUrl", this.image);
            result.accumulate("text", this.text);
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

}
