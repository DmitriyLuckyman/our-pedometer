package ru.spbau.ourpedometer.persistens;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteStatsCollector extends FlushingCollector {
    private static final String TABLE_NAME = "tbl_accel";
    private final SQLiteDatabase database;

    public SQLiteStatsCollector(String databaseName, int bufferCapacity) {
        super (bufferCapacity);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + databaseName);
        database = SQLiteDatabase.openOrCreateDatabase(file, null);

        String createTableString = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + " (_id integer primary key autoincrement, "
                + "x real, y real, z real, "
                + "time integer);";

        database.execSQL(createTableString);
    }

    private void saveStatisticsBean(StatisticsBean statistics) {
        ContentValues values = new ContentValues();
        values.put("x", statistics.x());
        values.put("y", statistics.y());
        values.put("z", statistics.z());
        values.put("time", statistics.time());
        database.insertOrThrow(TABLE_NAME, null, values);
    }


    private static StatisticsBean getStatisticBeanFromCursor(Cursor cur) {
        return new StatisticsBean(cur.getFloat(1), cur.getFloat(2), cur.getFloat(3), cur.getLong(4));
    }

    @Override
    protected void flush(List<StatisticsBean> buffer)
    {
        for (StatisticsBean statisticsBean : buffer)
            saveStatisticsBean(statisticsBean);
    }

    @Override
    protected void closeStorage() {
        database.close();
    }

    @Override
    public Iterable<StatisticsBean> getStatsByDateRangeFromStorage(Date startTime, Date stopTime) {
        String selection = "time > " + startTime.getTime() + " AND time < " + stopTime.getTime();
        Cursor cur = database.query(TABLE_NAME, null, selection, null, null, null, null);

        ArrayList<StatisticsBean> result = new ArrayList<StatisticsBean>(cur.getCount());
        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                result.add(getStatisticBeanFromCursor(cur));
                cur.moveToNext();
            }
        }
        
        cur.close();

        return result;
    }
}
