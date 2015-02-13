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
        String jsonStr = bundle.getString("questionJson");
        try {
            JSONObject json = new JSONObject(jsonStr);
            Question question = new Question(json);
            loadAnswer(question);
        } catch (JSONException err) {
            Log.w("Correction Fragment", err.getMessage());
        }

    }

    public void loadAnswer(Question question) {
        ArrayList<HashMap> answerSet = new ArrayList<HashMap>();
        answerSet.add(question.toHashMap());
        HashMap<String, ArrayList> request = new HashMap<String, ArrayList>();
        request.put("answers", answerSet);
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

    public void buildView() {
        TextView questionView = (TextView)getView().findViewById(R.id.questionCorrection);
        questionView.setText(Html.fromHtml(correction.text));
        TextView explainationView = (TextView)getView().findViewById(R.id.explainationCorrection);
        final LinearLayout choicesSelect = (LinearLayout)getView().findViewById(R.id.choicesSelectCorrection);
        explainationView.setText(Html.fromHtml(correction.explaination));
        if ((correction.image != null) && (correction.image.length()>0)) {
            Log.i("Image", correction.image);
            ImageView imageView = (ImageView)getView().findViewById(R.id.imageCorrection);
            new DownloadImageTask(imageView).execute(correction.image);
        }

        View someView = getView().findViewById(R.id.questionCorrection);
        View root = someView.getRootView();
        if (correction.check == false) {
            root.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            root.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }

        for (final Choice ch : correction.choices) {
            LinearLayout ligne = new LinearLayout(getActivity());
            ligne.setPadding(70, 0, 0, 0);
            ligne.setOrientation(LinearLayout.HORIZONTAL);

            TextView item = new TextView(getActivity());
            item.setText(ch.text);

            ImageView icon = new ImageView(getActivity());
            icon.setImageResource(R.drawable.check_empty);
            if (ch.scoring>0) {
                icon.setImageResource(R.drawable.check_right);
            }
            if ((ch.scoring<=0) && (ch.answered==true)) {
                icon.setImageResource(R.drawable.check_wrong);
            }

            ligne.addView(icon);
            ligne.addView(item);
            choicesSelect.addView(ligne);
            icon.getLayoutParams().height = 16;
            icon.getLayoutParams().width = 16;
        }
    }

}
