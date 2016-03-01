package fr.noopy.landru.quizz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.noopy.landru.quizz.database.StatisticRow;
import fr.noopy.landru.quizz.model.Choice;
import fr.noopy.landru.quizz.model.Question;
import fr.noopy.landru.quizz.tools.DownloadImageTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by cyrille on 10/02/15.
 */
public class CorrectionFragment extends Fragment {

    public static boolean DoNotSaveResult;

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

        TextView resultText = (TextView)getView().findViewById(R.id.next);
        resultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity parent = (MainActivity)getActivity();
                parent.loadQuestion();
            }
        });

        WebView explainationHtmlView = (WebView)getView().findViewById(R.id.explainationCorrectionHtml);
        explainationHtmlView.setBackgroundColor(Color.TRANSPARENT);
        // Make links open in default browser
        explainationHtmlView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("URL", url);
                if (url != null && url.startsWith("http://")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }
        });

        WebView questionHtmlView = (WebView)getView().findViewById(R.id.questionCorrectionHtml);
        questionHtmlView.setBackgroundColor(Color.TRANSPARENT);

        WebView webVersionHtmlView = (WebView)getView().findViewById(R.id.webVersion);
        webVersionHtmlView.setBackgroundColor(Color.TRANSPARENT);
        String link = getString(R.string.web_version_message) + " <a href=\"" + getString(R.string.web_version_url) + "\">" + getString(R.string.web_version_url) + "</a>";
        webVersionHtmlView.loadDataWithBaseURL(null, link, "text/html", "UTF-8", null);
        Log.i("LINK", link);

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
        if (correction!= null) {
            outState.putString("jsonSavedCorrection", correction.stringify());
        }
    }

    public void loadAnswer(Question question) {
        Log.i("LOAD QUESTION", question.stringify());
        TextView resultText = (TextView)getView().findViewById(R.id.result);
        resultText.setVisibility(View.INVISIBLE);
        TextView next = (TextView)getView().findViewById(R.id.next);
        next.setVisibility(View.INVISIBLE);
        correction = question;
        if (CorrectionFragment.DoNotSaveResult == false) {
            saveResult();
            Log.i("Stats", "I'm saving the results");
        } else {
            Log.i("Stats", "I'm not saving the results");
        }
        CorrectionFragment.DoNotSaveResult = false;
        getView().findViewById(R.id.loadingCorrection).setVisibility(View.GONE);
        buildView();
    }

    public void drawGlobalResult(boolean result) {
        TextView resultText = (TextView)getView().findViewById(R.id.result);
        TextView next = (TextView)getView().findViewById(R.id.next);
        if (result == false) {
            resultText.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            next.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            resultText.setText(R.string.resultWrong);
        } else {
            resultText.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            next.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            resultText.setText(R.string.resultRight);
        }
        resultText.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
    }

    public void drawChoices(ArrayList<Choice> choices) {
        LinearLayout choicesSelect = (LinearLayout)getView().findViewById(R.id.choicesSelectCorrection);
        for (final Choice ch : choices) {
            CheckBox button = new CheckBox(getActivity());
            button.setText(ch.text);
            button.setChecked((ch.scoring > 0));
            button.setEnabled(false);
            button.setTextColor(Color.BLACK);
            choicesSelect.addView(button);

            Log.i("ANSWER", ""+ ch.scoring);
            Log.i("ANSERED", ch.answered ? "true": "false");
            if ((ch.answered==true) && (ch.scoring<=0)) {
                button.setPaintFlags(button.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                button.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            if ((ch.answered==false) && (ch.scoring>0)) {
                button.setPaintFlags(button.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
            }

            if ((ch.answered==true) && (ch.scoring>0)) {
                button.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }

    public void buildView() {
        LinearLayout choicesSelect = (LinearLayout)getView().findViewById(R.id.choicesSelectCorrection);
        choicesSelect.removeAllViews();

        WebView questionHtmlView = (WebView)getView().findViewById(R.id.questionCorrectionHtml);
        questionHtmlView.loadDataWithBaseURL(null, correction.textHtml, "text/html", "UTF-8", null);

        WebView explainationHtmlView = (WebView)getView().findViewById(R.id.explainationCorrectionHtml);
        explainationHtmlView.loadDataWithBaseURL(null, correction.explainationHtml, "text/html", "UTF-8", null);

        if ((correction.image != null) && (correction.image.length()>0)) {
            ImageView imageView = (ImageView)getView().findViewById(R.id.imageCorrection);
            new DownloadImageTask(imageView).execute(correction.image);
        }

        drawGlobalResult(correction.check());

        drawChoices(correction.choices);

    }

    public void saveResult() {
        StatisticRow dataRow = new StatisticRow(correction.id, correction.check());
        MainActivity parent = (MainActivity)getActivity();
        dataRow.insert(parent.db);
    }

}
