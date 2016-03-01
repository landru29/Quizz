package fr.noopy.landru.quizz;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        this.level = settings.getString("level", "0");
        changeIcon();

        if (settings.getInt("lastVersion", 0) < 4) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("lastVersion", 4);
            editor.commit();
            new AlertDialog.Builder(this)
                .setTitle(R.string.intro_title)
                .setMessage(getText(R.string.intro_message).toString())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        }

        ImageView img = (ImageView)findViewById(R.id.level_icon);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PrefActivity.class);
                startActivity(intent);
            }
        });

        db = new Database(this);
        CorrectionFragment.DoNotSaveResult = false;

        if (savedInstanceState == null) {
            loadQuestion();
        }
    }

    private void changeIcon() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        ImageView img = (ImageView)findViewById(R.id.level_icon);
        TextView caption = (TextView)findViewById(R.id.level_caption);
        caption.setTextColor(Color.BLACK);

        String thisLevel = settings.getString("level", "0");
        String[] levels = getResources().getStringArray(R.array.levels_available);
        String[] levelCaptions = getResources().getStringArray(R.array.levels_alias_available);
        for (int i=0; i< levels.length; i++) {
            if (levels[i].compareTo(thisLevel) == 0) {
                TextView value = (TextView)findViewById(R.id.level_value);
                value.setText("(" + levelCaptions[i] + ")");
                value.setTextColor(Color.BLACK);
            }
        }

        switch (settings.getString("level", "0")) {
            case "0":
                img.setImageResource(R.drawable.baby);
                break;
            case "10":
                img.setImageResource(R.drawable.expert);
                break;
            default:
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
        /*if (state==State.CORRECTION) {
            loadQuestion();
        }*/
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getString("level", "0") != this.level) {
            this.level = settings.getString("level", "0");
            changeIcon();
            loadQuestion();
        }
    }

    public void loadQuestion() {
        state = State.QUESTION;
        Bundle bundle = new Bundle();
        bundle.putString("level", level);
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
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
            case R.id.action_pref: {
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
