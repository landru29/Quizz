package fr.noopy.landru.quizz.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.noopy.landru.quizz.model.Choice;

/**
 * Created by cyrille on 14/02/15.
 */
public class StatisticRow {

    public String questionId;
    public Date date;
    public int ok;
    public int ko;

    public StatisticRow(String questionId, Date date, int ok, int ko) {
        this.questionId = questionId;
        this.date = date;
        this.ok = ok;
        this.ko = ko;
    }

    public StatisticRow() {
        this(null, null, 0, 0);
    }

    public StatisticRow(Date date) {
        this(null, date, 0, 0);
    }

    public StatisticRow(String questionId) {
        this(questionId, 0, 0);
    }

    public StatisticRow(String questionId, int ok, int ko) {
        this(questionId, new Date(), ok, ko);
    }

    public StatisticRow(String questionId, boolean isOk) {
        this(questionId);
        if (isOk) {
            this.ok = 1;
        } else {
            this.ko = 1;
        }
    }

    private ContentValues getValues() {
        ContentValues values = new ContentValues();

        if (date != null) {
            values.put(StatisticTable.dateCol, date.getTime());
        } else {
            values.put(StatisticTable.dateCol, (new Date()).getTime());
        }
        values.put(StatisticTable.questionCol, (questionId != null ? questionId : ""));
        values.put(StatisticTable.okCol, ok);
        values.put(StatisticTable.koCol, ko);
        return values;
    }

    public void insert(Database db) {
        db.getWritableDatabase().insert(StatisticTable.tableName, null, getValues());
    }

    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        try {
            result.accumulate("questionId", this.questionId);
            result.accumulate("date", this.date.toString());
            result.accumulate("ok", this.ok);
            result.accumulate("ko", this.ko);
        } catch (JSONException err) {
            Log.w("Statistic", err.getMessage());
        }
        return result;
    }

    public String stringify() {
        return this.toJson().toString();
    }

}
