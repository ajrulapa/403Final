package com.example.user.a403final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by User on 12/4/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 7;
    public static final int NUM_COLUMNS = 13;
    public static final String DATABASE_NAME = "Courses.db";
    public static final String TABLE_NAME = "courses_table";
    public static final String COL_1 = "COURSE_ID";
    public static final String COL_2 = "COURSE_PREFIX";
    public static final String COL_3 = "COURSE_NUM";
    public static final String COL_4 = "COURSE_DESC";
    public static final String COL_5 = "COURSE_TITLE";
    public static final String COL_6 = "TERM";
    public static final String COL_7 = "COURSE_CREDITS";
    public static final String COL_8 = "BUILDING";
    public static final String COL_9 = "ROOM_NUMBER";
    public static final String COL_10 = "START_TIME";
    public static final String COL_11 = "END_TIME";
    public static final String COL_12 = "DAYS";
    public static final String COL_13 = "INSTRUCTOR_NAME";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    public static final String CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_2+" TEXT, "+COL_3+" TEXT, "+COL_4+" TEXT, "+COL_5+" TEXT, "+COL_6+" INTEGER, "
           +COL_7+" TEXT, " +COL_8+" TEXT, " +COL_9+" TEXT, " +COL_10+" TEXT, " +COL_11+" TEXT, " +COL_12+" TEXT, " +COL_13+" TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        Log.d("Db operations","DB Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void restartDb(DatabaseHelper dop) {
        SQLiteDatabase sq = dop.getReadableDatabase();

        sq.execSQL(DROP_TABLE);

        sq.execSQL(CREATE_QUERY);
    }

    public void insertData(String prefix, String courseNumber, String desc, String title, String term, int credits,
        String building, String roomNum, String startTime, String endTime, String days, String instructorName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Add the params to the ContentValues to be added to the db table row
        cv.put(COL_2, prefix);
        cv.put(COL_3, courseNumber);
        cv.put(COL_4, desc);
        cv.put(COL_5, title);
        cv.put(COL_6, term);
        cv.put(COL_7, credits);
        cv.put(COL_8, building);
        cv.put(COL_9, roomNum);
        cv.put(COL_10, startTime);
        cv.put(COL_11, endTime);
        cv.put(COL_12, days);
        cv.put(COL_13, instructorName);

        // db.insert() will return -1 if it false to add the table row
        long result = db.insert(TABLE_NAME, null, cv);

        if(result < 0)
            Log.d("DB operations", "Not Inserted");
    }

    public Cursor retrieveData(DatabaseHelper dop) {
        SQLiteDatabase sq = dop.getReadableDatabase();
        String[] columns = {COL_1,COL_2,COL_3,COL_4,COL_5,COL_6,COL_7,COL_8,COL_9,COL_10,COL_11,COL_12,COL_13};
        Cursor cr = sq.query(TABLE_NAME, columns, null,null,null,null,null);
        return cr;
    }

    public Cursor retrieveCourseData(DatabaseHelper dop, String tableId) {
        // Create db object to query
        SQLiteDatabase sq = dop.getReadableDatabase();

        // Create an array of params to pass to the query
        String[] params1 = new String[]{tableId};

        // Create the query
        String subQuery1 = "SELECT "+COL_2+" FROM "+TABLE_NAME+" WHERE "+COL_1+" = ?";
        Log.d("","query = "+subQuery1);
        Log.d("","courseId = "+params1);
        // Query SQLite
        Cursor cursorPrefix = sq.rawQuery(subQuery1, params1);
        cursorPrefix.moveToFirst();

        // Get resulting prefix
        String prefix = cursorPrefix.getString(0);

        // Create the query
        String subQuery2 = "SELECT "+COL_3+" FROM "+TABLE_NAME+" WHERE "+COL_1+" = ?";

        // Query SQLite
        Cursor cursorCn = sq.rawQuery(subQuery2, params1);
        cursorCn.moveToFirst();

        // Get resulting prefix
        String cn = cursorCn.getString(0);

        // Get the params from the previous queries
        String[] params2 = new String[]{prefix, cn};

        // main query
        String query = "SELECT * FROM " + TABLE_NAME+" WHERE "+COL_2+" =? AND "+COL_3+"=? ORDER BY "+COL_6;

        // Return all rows with a given prefix+coursenumber combination
        Cursor cr = sq.rawQuery(query, params2);

        cr.moveToFirst();

        // Return the cursor
        return cr;
    }
}
