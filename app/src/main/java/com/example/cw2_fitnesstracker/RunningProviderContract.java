package com.example.cw2_fitnesstracker;

import android.net.Uri;

public class RunningProviderContract {
    public static final String AUTHORITY = "com.example.runningtracker.MyContentProvider";
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");
    public static final Uri RUNS_URI = Uri.parse("content://"+AUTHORITY+"/runLogs");


    public static final String ID = "_id";
    public static final String DATE = "date";
    public static final String DURATION = "duration";
    public static final String DURATION_SECONDS = "seconds";
    public static final String DISTANCE = "distance";
    public static final String AVG_SPEED = "avg_speed";
    public static final String RATING = "rating";

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/MyContentProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/MyContentProvider.data.text";

}
