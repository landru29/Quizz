package com.noopy.landru.quizz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cyrille on 10/02/15.
 */
public class QuestionFragment extends Fragment {

    public Question question;

    public QuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button validate = (Button)getView().findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity parent = (MainActivity)getActivity();
                parent.loadCorrection(question.stringify());
            }
        });
        getRandomQuestion();
    }

    private void setWhiteBg() {
        View someView = getView().findViewById(R.id.question);
        // Find the root view
        View root = someView.getRootView();
        // Set the color
        root.setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    private void getRandomQuestion() {
        setWhiteBg();
        question = null;
        final Button validate = (Button)getView().findViewById(R.id.validate);
        // disable Validate button
        validate.setEnabled(false);
        // prepare Parse.com request
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("count", 1);
        // Perform the Parse.com request
        ParseCloud.callFunctionInBackground("randomQuestions", data, new FunctionCallback<HashMap>() {
            public void done(HashMap result, ParseException e) {
                if (e == null) {
                    ArrayList data = (ArrayList)result.get("data");
                    question = new Question((HashMap)data.get(0));
                    buildView();
                }
                validate.setEnabled(true);
            }
        });
    }

    public void buildView() {
        final LinearLayout choicesSelect = (LinearLayout)getView().findViewById(R.id.choicesSelect);
        final RadioGroup choicesRadio = (RadioGroup)getView().findViewById(R.id.choicesRadio);
        // Reset choices
        choicesRadio.clearCheck();
        choicesSelect.removeAllViews();
        // build the question
        TextView questionView = (TextView)getView().findViewById(R.id.question);
        questionView.setText(Html.fromHtml(question.text));
        if ((question.image != null) && (question.image.length()>0)) {
            ImageView imageView = (ImageView)getView().findViewById(R.id.image);
            new DownloadImageTask(imageView).execute(question.image);
        }
        for (final Choice ch : question.choices) {
            if (question.multiAnswer == false) {
                RadioButton button = new RadioButton(getActivity());
                button.setText(ch.text);
                choicesRadio.addView(button);
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ch.answered = isChecked;
                    }
                });
            } else {
                CheckBox button = new CheckBox(getActivity());
                button.setText(ch.text);
                choicesSelect.addView(button);
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ch.answered = isChecked;
                    }
                });
            }
        }
    }

}
