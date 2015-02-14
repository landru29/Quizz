package fr.noopy.landru.quizz.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cyrille on 14/02/15.
 */
public class StatisticTable {

    public static String tableName = "statistic";

    public static String dateCol = "date";
    public static String okCol = "ok";
    public static String koCol = "ko";
    public static String questionCol = "questionId";

    public static void onCreate(SQLiteDatabase db) {
        String createStr = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            dateCol + " INTEGER," +
            okCol + " INTEGER," +
            koCol + " INTEGER," +
            questionCol + " VARCHAR(10)" +
            ")";
        db.execSQL(createStr);
    }
    
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static ArrayList<StatisticRow> readByDate(Database db, Date dateFrom, Date dateTo) {
        ArrayList<StatisticRow> result = new ArrayList<StatisticRow>();

        String[] columns = new String[] {StatisticTable.dateCol, StatisticTable.okCol, StatisticTable.koCol, StatisticTable.questionCol};

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(dateFrom);
        startDate.set(Calendar.MILLISECOND, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.HOUR_OF_DAY, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(dateTo);
        endDate.set(Calendar.MILLISECOND, 999);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.HOUR_OF_DAY, 23);

        Cursor cursor =
                db.getReadableDatabase().query(tableName, // a. table
                        columns, // b. column names
                        " " + dateCol + " > ? AND " + dateCol + " < ?", // c. selections
                        new String[]{String.valueOf(startDate.getTime().getTime()), String.valueOf(endDate.getTime().getTime())}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null){
            if (cursor.moveToFirst()) {
                do {
                    result.add(
                            new StatisticRow(
                                    cursor.getString(3),
                                    new Date(Long.parseLong(cursor.getString(0), 10)),
                                    Integer.parseInt(cursor.getString(1)),
                                    Integer.parseInt(cursor.getString(2))
                            )
                    );
                } while (cursor.moveToNext());
            }
        }

        return result;
    }

    public static ArrayList<StatisticRow> readByDate(Database db, Date date) {
        return readByDate(db, date, date);
    }

    public static ArrayList<StatisticRow> readAll(Database db) {
        ArrayList<StatisticRow> result = new ArrayList<StatisticRow>();
        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT  * FROM " + tableName, null);
        if (cursor != null){
            if (cursor.moveToFirst()) {
                do {
                    result.add(
                            new StatisticRow(
                                    cursor.getString(3),
                                    new Date(Long.parseLong(cursor.getString(0), 10)),
                                    Integer.parseInt(cursor.getString(1)),
                                    Integer.parseInt(cursor.getString(2))
                            )
                    );
                } while (cursor.moveToNext());
            }
        }
        return result;
    }

}
