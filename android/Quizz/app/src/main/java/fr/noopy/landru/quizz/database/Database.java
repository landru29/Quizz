package fr.noopy.landru.quizz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cyrille on 14/02/15.
 */
public class Database extends SQLiteOpenHelper {

    private static String name = "quiz";
    private static int version = 1;


    public Database(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase  db) {
        StatisticTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        StatisticTable.onUpgrade(db, oldVersion, newVersion);
    }

}
