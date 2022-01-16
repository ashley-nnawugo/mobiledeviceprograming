package com.example.cw2_fitnesstracker;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "runLogs";



    private ContentResolver contentResolver;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, DATABASE_NAME, factory, version);
        contentResolver = context.getContentResolver();
        Log.d("g53mdp", "DBHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("g53mdp", "onCreate");

        db.execSQL("CREATE TABLE runLogs (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "date VARCHAR(128) NOT NULL," +
                "duration VARCHAR(128) NOT NULL," +
                "seconds VARCHAR(128) NOT NULL," +
                "distance VARCHAR(128) NOT NULL," +
                "avg_speed VARCHAR(128) NOT NULL,"+
                "rating VARCHAR(128) NOT NULL"+
                ");");
/*
        db.execSQL("CREATE TABLE animals (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "name VARCHAR(128) NOT NULL," +
                "kind VARCHAR(128) NOT NULL," +
                "food VARCHAR(128) NOT NULL" +
                ");");

        db.execSQL("INSERT INTO people (name, email, food) VALUES ('martin', 'mdf@cs.nott.ac.uk', 'cheese');");
        db.execSQL("INSERT INTO people (name, email, food) VALUES ('donald', 'donald@gmail.com', 'burgers');");


 */
       db.execSQL("INSERT INTO runLogs (date, duration, seconds, distance, avg_speed, rating) VALUES ('Sat Dec 25 21:37:43 GMT 2021', '10:22', 37320, 10000, 4.7,'great');");



    }

    public void insertList(int distance, int seconds, String time ,float speed, Date date, String rating) {
        ContentValues new_values = new ContentValues();

        //might need to add id value
        new_values.put(RunningProviderContract.DATE, date.toString());
        new_values.put(RunningProviderContract.DURATION,time);
        new_values.put(RunningProviderContract.DURATION_SECONDS, seconds);
        new_values.put(RunningProviderContract.DISTANCE,distance);
        new_values.put(RunningProviderContract.AVG_SPEED, speed);
        new_values.put(RunningProviderContract.RATING, rating);

        contentResolver.insert(RunningProviderContract.RUNS_URI, new_values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS runLogs");
       /*
        db.execSQL("DROP TABLE IF EXISTS animals");
        db.execSQL("DROP TABLE IF EXISTS food");
        */
        onCreate(db);
    }
}