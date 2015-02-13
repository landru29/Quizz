package com.noopy.landru.quizz;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;



public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            loadQuestion();
        }
    }


    public void loadQuestion() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new QuestionFragment())
                .commit();
    }

    public void loadCorrection(String questionData) {
        Bundle bundle = new Bundle();
        bundle.putString("questionJson", questionData);
        CorrectionFragment fragment = new CorrectionFragment();
        fragment.setArguments(bundle);
        Log.i("Bundle", "passing arguments " + questionData);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
