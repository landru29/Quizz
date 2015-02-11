package com.noopy.landru.quizz.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.markdownj.MarkdownProcessor;

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
    public boolean check;
    public String explaination;


    public void finalize() {
        choices.clear();
    }

    public Question() {
        this.choices = new ArrayList<Choice>();
    }

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

    public Question(HashMap data) {
        MarkdownProcessor markdown = new MarkdownProcessor();
        JSONObject json = new JSONObject(data);
        try {
            if (json.has("text")) {
                this.text = markdown.markdown(json.getString("text"));
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
                this.explaination = markdown.markdown(json.getString("explaination"));
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
