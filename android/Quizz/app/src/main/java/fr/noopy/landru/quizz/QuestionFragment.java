package fr.noopy.landru.quizz;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import fr.noopy.landru.quizz.model.Choice;
import fr.noopy.landru.quizz.model.Question;
import fr.noopy.landru.quizz.tools.DownloadImageTask;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
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
        try {
            if ((savedInstanceState != null) && (savedInstanceState.containsKey("jsonSavedQuestion"))) {
                JSONObject json = new JSONObject(savedInstanceState.getString("jsonSavedQuestion"));
                if (json != null) {
                    this.question = new Question(json);
                } else {
                    question=null;
                }
            } else {
                question=null;
            }
        } catch (JSONException err) {
            Log.w("Restaure Question", err.getMessage());
        }
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
                if (question != null) {
                    parent.loadCorrection(question.stringify());
                }
            }
        });

        WebView questionHtmlView = (WebView)getView().findViewById(R.id.questionHtml);
        questionHtmlView.setBackgroundColor(Color.TRANSPARENT);

        if (question == null) {
            getRandomQuestion();
        } else {
            buildView();
        }
    }

    @Override
    public void onSaveInstanceState( Bundle outState) {
        super.onSaveInstanceState(outState);
        if (question != null) {
            outState.putString("jsonSavedQuestion", question.stringify());
        }
    }

    private void getRandomQuestion() {
        question = null;
        final Button validate = (Button)getView().findViewById(R.id.validate);
        validate.setVisibility(View.INVISIBLE);
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
                getView().findViewById(R.id.loadingQuestion).setVisibility(View.GONE);
            }
        });
    }

    public void drawChoices(ArrayList<Choice> choices) {
        LinearLayout choicesSelect = (LinearLayout)getView().findViewById(R.id.choicesSelect);
        RadioGroup choicesRadio = (RadioGroup)getView().findViewById(R.id.choicesRadio);
        for (final Choice ch : choices) {
            if (question.multiAnswer == false) {
                RadioButton button = new RadioButton(getActivity());
                button.setText(ch.text);
                button.setTextColor(Color.BLACK);
                button.setChecked(ch.answered);
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
                button.setTextColor(Color.BLACK);
                button.setChecked(ch.answered);
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

    public void buildView() {
        LinearLayout choicesSelect = (LinearLayout)getView().findViewById(R.id.choicesSelect);
        RadioGroup choicesRadio = (RadioGroup)getView().findViewById(R.id.choicesRadio);

        Button validate = (Button)getView().findViewById(R.id.validate);
        validate.setVisibility(View.VISIBLE);

        // Reset choices
        choicesRadio.clearCheck();
        choicesSelect.removeAllViews();

        // build the question
        WebView questionHtmlView = (WebView)getView().findViewById(R.id.questionHtml);
        questionHtmlView.loadDataWithBaseURL(null, question.text, "text/html", "UTF-8", null);

        if ((question.image != null) && (question.image.length()>0)) {
            ImageView imageView = (ImageView)getView().findViewById(R.id.image);
            new DownloadImageTask(imageView).execute(question.image);
        }

        drawChoices(question.choices);

        getView().findViewById(R.id.loadingQuestion).setVisibility(View.GONE);

    }

}
