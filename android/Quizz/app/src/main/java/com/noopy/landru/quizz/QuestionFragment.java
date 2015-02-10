package com.noopy.landru.quizz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.noopy.landru.quizz.model.Choice;
import com.noopy.landru.quizz.model.Question;
import com.noopy.landru.quizz.tools.DownloadImageTask;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cyrille on 10/02/15.
 */
public class QuestionFragment extends Fragment {
    public QuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);
        getRandomQuestion();
        return rootView;
    }

    private void getRandomQuestion() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("count", 1);
        ParseCloud.callFunctionInBackground("randomQuestions", data, new FunctionCallback<HashMap>() {
            public void done(HashMap result, ParseException e) {
                if (e == null) {
                    ArrayList data = (ArrayList)result.get("data");
                    Question question = new Question((HashMap)data.get(0));
                    TextView questionView = (TextView)getView().findViewById(R.id.question);
                    questionView.setText(question.text);
                    if (question.image != null) {
                        ImageView imageView = (ImageView)getView().findViewById(R.id.image);
                        new DownloadImageTask(imageView).execute(question.image);
                    }
                    LinearLayout choicesSelect = (LinearLayout)getView().findViewById(R.id.choicesSelect);
                    RadioGroup choicesRadio = (RadioGroup)getView().findViewById(R.id.choicesRadio);
                    for (Choice ch : question.choices) {
                        if (question.multiAnswer == false) {
                            RadioButton button = new RadioButton(getActivity());
                            button.setText(ch.text);
                            choicesRadio.addView(button);
                        } else {
                            CheckBox button = new CheckBox(getActivity());
                            button.setText(ch.text);
                            choicesSelect.addView(button);
                        }
                    }
                }
            }
        });
    }

}
