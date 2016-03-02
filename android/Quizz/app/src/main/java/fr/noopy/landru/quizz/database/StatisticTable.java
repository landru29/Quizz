package fr.noopy.landru.quizz.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
                        StatisticTable.dateCol + " DESC", // g. order by
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

    public static HashMap<String, Integer> getGlobalStat(Database db) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        int ok=0;
        int ko=0;
        ArrayList<StatisticRow> data = readAll(db);
        for(StatisticRow row:data) {
            ok += row.ok;
            ko += row.ko;
        }
        result.put("ok", ok);
        result.put("ko", ko);
        return result;
    }

    private static int getWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.WEEK_OF_YEAR) + 100 * cal.get(Calendar.YEAR);
    }

    public static ArrayList<BarEntry> getEvolutionStat(Database db, int weeks) {
        ArrayList<BarEntry> result = new ArrayList<BarEntry>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -weeks*7);
        ArrayList<StatisticRow> data = readByDate(db, calendar.getTime(), new Date());
        int originweek = getWeek(calendar.getTime());

        // Proceed only if we have some data
        if (data.size()>0) {
            HashMap<Integer, Integer> ok = new HashMap<Integer, Integer> ();
            HashMap<Integer, Integer> ko = new HashMap<Integer, Integer> ();
            int maxWeek = 0;
            // Main loop
            for(StatisticRow row:data) {
                // get the currentweek
                int currentWeek = getWeek(row.date);
                // get the maximum week
                maxWeek = Math.max(maxWeek, currentWeek);
                //create a week entry in OK list
                if (!ok.containsKey(currentWeek)) {
                    ok.put(currentWeek, 0);
                }
                //create a week entry in KO list
                if (!ko.containsKey(currentWeek)) {
                    ko.put(currentWeek, 0);
                }
                // increment OK and KO
                ok.put(currentWeek, ok.get(currentWeek)+row.ok);
                ko.put(currentWeek, ko.get(currentWeek)+row.ko);
            }
            // compute percents
            int j=0;
            for(int i=originweek;i<=maxWeek; i++) {
                float okValue = ok.containsKey(i) ? (float)ok.get(i) : 0f;
                float koValue = ko.containsKey(i) ? (float)ko.get(i) : 0f;
                Log.i("APPENDING", okValue + " - " + koValue);
                result.add(new BarEntry(new float[]{okValue, koValue}, j++));
            }
        }

        return result;
    }

}
