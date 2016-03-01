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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

/**
 * Created by cyrille on 10/02/15.
 */
public class QuestionFragment extends Fragment {

    public Question question;
    private int level;

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
                } else {
                    parent.loadQuestion();
                }
            }
        });

        WebView questionHtmlView = (WebView)getView().findViewById(R.id.questionHtml);
        questionHtmlView.setBackgroundColor(Color.TRANSPARENT);

        WebView webVersionHtmlView = (WebView)getView().findViewById(R.id.webVersion);
        webVersionHtmlView.setBackgroundColor(Color.TRANSPARENT);
        String link = getString(R.string.web_version_message) + " <a href=\"" + getString(R.string.web_version_url) + "\">" + getString(R.string.web_version_url) + "</a>";
        webVersionHtmlView.loadDataWithBaseURL(null, link, "text/html", "UTF-8", null);
        Log.i("LINK", link);

        Bundle bundle = this.getArguments();
        if ((bundle != null) && (bundle.containsKey("level"))) {
            level = Integer.parseInt(bundle.getString("level"));
        } else {
            level = 0;
        }

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
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("count", "1");
        params.put("level", level);
        final Button validate = (Button)getView().findViewById(R.id.validate);
        validate.setVisibility(View.INVISIBLE);
        // disable Validate button
        validate.setEnabled(false);

        // prepare api request
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("count", 1);
        data.put("level", level);

        // Perform the api request
        Log.i("NOOPY", "Requesting");
        client.get("http://api.noopy.fr/api/public/quizz", params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Log.i("RESULT", res);
                        try {
                            JSONObject json = new JSONObject(res);
                            if ((json != null) && (json.has("data"))) {
                                JSONArray questionData = json.getJSONArray("data");
                                question = new Question(questionData.getJSONObject(0));
                            }
                        } catch (JSONException err) {
                            Log.w("Read Question", err.getMessage());
                        }
                        buildView();
                        validate.setEnabled(true);
                        getView().findViewById(R.id.loadingQuestion).setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.i("ERROR", res);
                    }
                }
        );
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
        WebView questionHtmlView = (WebView) getView().findViewById(R.id.questionHtml);
        if (question != null) {
            LinearLayout choicesSelect = (LinearLayout) getView().findViewById(R.id.choicesSelect);
            RadioGroup choicesRadio = (RadioGroup) getView().findViewById(R.id.choicesRadio);

            // Reset choices
            choicesRadio.clearCheck();
            choicesSelect.removeAllViews();

            // build the question
            questionHtmlView.loadDataWithBaseURL(null, question.textHtml, "text/html", "UTF-8", null);

            if ((question.image != null) && (question.image.length() > 0)) {
                ImageView imageView = (ImageView) getView().findViewById(R.id.image);
                new DownloadImageTask(imageView).execute(question.image);
            }

            drawChoices(question.choices);

            getView().findViewById(R.id.loadingQuestion).setVisibility(View.GONE);
        } else {
            questionHtmlView.loadDataWithBaseURL(null, "<p>" + getText(R.string.api_error).toString() + "</p>", "text/html", "UTF-8", null);
        }
        Button validate = (Button) getView().findViewById(R.id.validate);
        validate.setVisibility(View.VISIBLE);
    }

}
