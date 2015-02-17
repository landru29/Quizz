package fr.noopy.landru.quizz;


import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.noopy.landru.quizz.database.Database;
import fr.noopy.landru.quizz.database.StatisticRow;


public class MainActivity extends ActionBarActivity {

    public enum State {
        QUESTION,
        CORRECTION,
        STATISTICS
    };

    Database db;

    private State state=State.QUESTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Database(this);
        CorrectionFragment.DoNotSaveResult = false;

        if (savedInstanceState == null) {
            loadQuestion();
        }
    }

    /*private void test() {
        SimpleDateFormat parser=new SimpleDateFormat("dd/MM/yyyy");
        try {
            (new StatisticRow("123", parser.parse("25/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("25/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("25/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("25/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("25/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("25/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("25/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("25/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("25/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("20/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("10/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("10/01/2015"), 0, 1)).insert(db);
            (new StatisticRow("123", parser.parse("31/01/2015"), 0, 1)).insert(db);
            (new StatisticRow("123", parser.parse("31/01/2015"), 0, 1)).insert(db);
            (new StatisticRow("123", parser.parse("25/01/2015"), 0, 1)).insert(db);
            (new StatisticRow("123", parser.parse("20/01/2015"), 0, 1)).insert(db);
            (new StatisticRow("123", parser.parse("10/01/2015"), 0, 1)).insert(db);
            (new StatisticRow("123", parser.parse("10/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("31/01/2015"), 1, 0)).insert(db);
            (new StatisticRow("123", parser.parse("31/01/2015"), 1, 0)).insert(db);
        } catch (ParseException e) {

        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (state==State.CORRECTION) {
            loadQuestion();
        }
    }

    public void loadQuestion() {
        state = State.QUESTION;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new QuestionFragment())
                .commit();
    }

    public void loadCorrection(String questionData) {
        state = State.CORRECTION;
        Bundle bundle = new Bundle();
        bundle.putString("questionJson", questionData);
        CorrectionFragment fragment = new CorrectionFragment();
        fragment.setArguments(bundle);
        Log.i("Bundle", "passing arguments " + questionData);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void loadStatistics() {
        state = State.STATISTICS;
        StatisticsFragment myFragment = new StatisticsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, myFragment)
                .addToBackStack("")
                .commit();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                CorrectionFragment.DoNotSaveResult = true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stats:
                loadStatistics();
                return true;
            case R.id.action_quiz: {
                if (state != State.QUESTION) {
                    loadQuestion();
                    return true;
                }
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
