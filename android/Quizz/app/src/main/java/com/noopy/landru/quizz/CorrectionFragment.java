package com.noopy.landru.quizz;

import android.graphics.Paint;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cyrille on 10/02/15.
 */
public class CorrectionFragment extends Fragment {

    public Question correction;

    public CorrectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_correction, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Button validate = (Button)getView().findViewById(R.id.newQuestion);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity parent = (MainActivity)getActivity();
                parent.loadQuestion();
            }
        });

        Bundle bundle = this.getArguments();
        try {
            if ((bundle != null) && (bundle.containsKey("questionJson"))) {
                Log.i("Answer", "Answer must be loaded");
                String jsonStr = bundle.getString("questionJson");
                JSONObject json = new JSONObject(jsonStr);
                Question question = new Question(json);
                loadAnswer(question);
            } else if ((savedInstanceState!=null) && (savedInstanceState.containsKey("jsonSavedCorrection"))) {
                Log.i("Answer", "Restoring state");
                JSONObject json = new JSONObject(savedInstanceState.getString("jsonSavedCorrection"));
                this.correction = new Question(json);
                buildView();
            }
        } catch (JSONException err) {
            Log.w("Correction Fragment", err.getMessage());
        }

    }

    @Override
    public void onSaveInstanceState( Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("jsonSavedCorrection", correction.stringify());
    }

    public void loadAnswer(Question question) {
        Button validate = (Button)getView().findViewById(R.id.newQuestion);
        validate.setVisibility(View.INVISIBLE);
        TextView resultText = (TextView)getView().findViewById(R.id.result);
        resultText.setVisibility(View.INVISIBLE);
        ArrayList<HashMap> answerSet = new ArrayList<HashMap>();
        answerSet.add(question.toHashMap());
        HashMap<String, ArrayList> request = new HashMap<String, ArrayList>();
        request.put("answers", answerSet);
        Log.i("Answer", "Question sent " + question.toHashMap().toString());
        //Send the request
        ParseCloud.callFunctionInBackground("checkAnswers", request, new FunctionCallback<HashMap>() {
            public void done(HashMap result, ParseException e) {
                if (e == null) {
                    ArrayList data = (ArrayList)result.get("data");
                    MainActivity parent = (MainActivity)getActivity();
                    correction = new Question((HashMap)data.get(0));
                    buildView();
                }
            }
        });
    }

    public void drawGlobalResult(boolean result) {
        TextView resultText = (TextView)getView().findViewById(R.id.result);
        if (result == false) {
            resultText.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            resultText.setText(R.string.resultWrong);
        } else {
            resultText.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            resultText.setText(R.string.resultRight);
        }
        resultText.setVisibility(View.VISIBLE);
    }

    public void drawChoices(ArrayList<Choice> choices) {
        LinearLayout choicesSelect = (LinearLayout)getView().findViewById(R.id.choicesSelectCorrection);
        for (final Choice ch : choices) {
            CheckBox button = new CheckBox(getActivity());
            button.setText(ch.text);
            button.setChecked((ch.scoring > 0));
            button.setEnabled(false);
            choicesSelect.addView(button);
            if ((ch.answered==true) && (ch.check==false)) {
                button.setPaintFlags(button.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                button.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            if (ch.scoring>0) {
                button.setPaintFlags(button.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
            }

            if ((ch.check==true) && (ch.scoring>0)) {
                button.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }

    public void buildView() {
        LinearLayout choicesSelect = (LinearLayout)getView().findViewById(R.id.choicesSelectCorrection);
        choicesSelect.removeAllViews();
        Button validate = (Button)getView().findViewById(R.id.newQuestion);
        validate.setVisibility(View.VISIBLE);
        TextView questionView = (TextView)getView().findViewById(R.id.questionCorrection);
        questionView.setText(Html.fromHtml(correction.text));
        TextView explainationView = (TextView)getView().findViewById(R.id.explainationCorrection);
        explainationView.setText(Html.fromHtml(correction.explaination));
        Log.i("Explaination", correction.explaination);
        if ((correction.image != null) && (correction.image.length()>0)) {
            Log.i("Image", correction.image);
            ImageView imageView = (ImageView)getView().findViewById(R.id.imageCorrection);
            new DownloadImageTask(imageView).execute(correction.image);
        }

        drawGlobalResult(correction.check);

        drawChoices(correction.choices);

    }

}
