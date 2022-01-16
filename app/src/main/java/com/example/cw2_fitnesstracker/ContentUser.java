package com.example.cw2_fitnesstracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContentUser extends AppCompatActivity {

    SimpleCursorAdapter dataAdapter;
    Handler h = new Handler();
    DBHelper dbHelper;


    //TODO raw query for best time, and stuff

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_user);
        dbHelper = new DBHelper(this, "runLogs",null, 9);

        String key;
        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");

        if(key.equals("all"))
            queryRuns();
        if(key.equals("best"))
            queryBestTime();
        if(key.equals("distance"))
            queryLongestDistance();
        if(key.equals("far"))
            queryLastRun();

        getContentResolver().
                registerContentObserver(
                        RunningProviderContract.ALL_URI,
                        true,
                        new ChangeObserver(h));


    }

    class ChangeObserver extends ContentObserver {

        public ChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            queryRuns();

        }
    }

    //this class links the Uris and database to the view
    public void queryRuns() {

        String[] projection = new String[] {
                RunningProviderContract.ID,
                RunningProviderContract.DATE,
                RunningProviderContract.DURATION,
                RunningProviderContract.DURATION_SECONDS,
                RunningProviderContract.DISTANCE,
                RunningProviderContract.AVG_SPEED,
                RunningProviderContract.RATING
        };

        String[] colsToDisplay = new String[] {
                RunningProviderContract.ID,
                RunningProviderContract.DATE,
                RunningProviderContract.DURATION,
                RunningProviderContract.DURATION_SECONDS,
                RunningProviderContract.DISTANCE,
                RunningProviderContract.AVG_SPEED,
                RunningProviderContract.RATING
        };

        int[] colResIds = new int[] {
                R.id._id,
                R.id.date,
                R.id.duration,
                R.id.duration_s,
                R.id.db_dis,
                R.id.avg_speed,
                R.id.rating,
        };

        Cursor cursor = getContentResolver().query(RunningProviderContract.RUNS_URI, projection, null, null, null);

        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_view_db_item,
                cursor,
                colsToDisplay,
                colResIds,
                0);


        ListView listView = (ListView) findViewById(R.id.runs_list);
        listView.setAdapter(dataAdapter);
    }

    @SuppressLint("Range")
    public void queryBestTime() {

        String[] projection = new String[] {
                RunningProviderContract.ID,
                RunningProviderContract.DATE,
                RunningProviderContract.DURATION,
                RunningProviderContract.DURATION_SECONDS,
                RunningProviderContract.DISTANCE,
                RunningProviderContract.AVG_SPEED,
                RunningProviderContract.RATING
        };

        String[] colsToDisplay = new String[] {
                RunningProviderContract.ID,
                RunningProviderContract.DATE,
                RunningProviderContract.DURATION,
                RunningProviderContract.DURATION_SECONDS,
                RunningProviderContract.DISTANCE,
                RunningProviderContract.AVG_SPEED,
                RunningProviderContract.RATING
        };

        int[] colResIds = new int[] {
                R.id._id,
                R.id.date,
                R.id.duration,
                R.id.duration_s,
                R.id.db_dis,
                R.id.avg_speed,
                R.id.rating,
        };

        Cursor cursor = getContentResolver().query(RunningProviderContract.RUNS_URI, projection, null, null, "avg_speed DESC");

        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_view_db_item,
                cursor,
                colsToDisplay,
                colResIds,
                0);


        ListView listView = (ListView) findViewById(R.id.runs_list);
        listView.setAdapter(dataAdapter);

        //this sorts database into descending order with fastest time at the top
        try{
            String best = "SELECT * FROM  RunLogs ORDER BY avg_speed DESC LIMIT 1";
            Cursor c = dbHelper.getReadableDatabase().rawQuery(best,null);
            c.moveToFirst();

            //data extraction
            String durationTime = c.getString(c.getColumnIndex("duration"));
            String date = c.getString(c.getColumnIndex("date"));
            String distance = c.getString(c.getColumnIndex("distance"));
            String speed = c.getString(c.getColumnIndex("avg_speed"));

            //popup to show last run
            AlertDialog alertDialog = new AlertDialog.Builder(ContentUser.this).create();
            alertDialog.setIcon(android.R.drawable.ic_menu_compass);
            alertDialog.setTitle("Best time");
            alertDialog.setMessage("Date: " + date +"\n" + "Time: " + durationTime + "\n"+ "Speed: " + speed+" km/h" + "\n"+ "Distance: " + distance +" Metres" );
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public void queryLongestDistance() {

        String[] projection = new String[] {
                RunningProviderContract.ID,
                RunningProviderContract.DATE,
                RunningProviderContract.DURATION,
                RunningProviderContract.DURATION_SECONDS,
                RunningProviderContract.DISTANCE,
                RunningProviderContract.AVG_SPEED,
                RunningProviderContract.RATING
        };

        String[] colsToDisplay = new String[] {
                RunningProviderContract.ID,
                RunningProviderContract.DATE,
                RunningProviderContract.DURATION,
                RunningProviderContract.DURATION_SECONDS,
                RunningProviderContract.DISTANCE,
                RunningProviderContract.AVG_SPEED,
                RunningProviderContract.RATING
        };

        int[] colResIds = new int[] {
                R.id._id,
                R.id.date,
                R.id.duration,
                R.id.duration_s,
                R.id.db_dis,
                R.id.avg_speed,
                R.id.rating,
        };

        Cursor cursor = getContentResolver().query(RunningProviderContract.RUNS_URI, projection, null, null, "distance DESC");

        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_view_db_item,
                cursor,
                colsToDisplay,
                colResIds,
                0);


        ListView listView = (ListView) findViewById(R.id.runs_list);
        listView.setAdapter(dataAdapter);

        //this sorts database into descending order with largest distance at the top
        try{
            //Sql query to sort
            String longest = "SELECT * FROM  RunLogs ORDER BY distance DESC LIMIT 1";
            Cursor c = dbHelper.getReadableDatabase().rawQuery(longest,null);
            c.moveToFirst();
            //data extraction
            String durationTime = c.getString(c.getColumnIndex("duration"));
            String date = c.getString(c.getColumnIndex("date"));
            String distance = c.getString(c.getColumnIndex("distance"));
            String speed = c.getString(c.getColumnIndex("avg_speed"));
            //Log.d("speed", speed);

            //creates a popup which shows longest distance travelled by the user
            AlertDialog alertDialog = new AlertDialog.Builder(ContentUser.this).create();
            alertDialog.setIcon(android.R.drawable.ic_menu_mylocation);
            alertDialog.setTitle("Longest Distance");
            alertDialog.setMessage("Date: " + date +"\n" + "Time: " + durationTime + "\n"+ "Speed: " + speed+" km/h" + "\n"+ "Distance: " + distance +" Metres" );
            alertDialog.show();
            //Log.d("validity", String.valueOf(x));

            //Log.d("best", c.getString(c.getColumnIndex("duration")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public void queryLastRun() {

        String[] projection = new String[] {
                RunningProviderContract.ID,
                RunningProviderContract.DATE,
                RunningProviderContract.DURATION,
                RunningProviderContract.DURATION_SECONDS,
                RunningProviderContract.DISTANCE,
                RunningProviderContract.AVG_SPEED,
                RunningProviderContract.RATING
        };

        String[] colsToDisplay = new String[] {
                RunningProviderContract.ID,
                RunningProviderContract.DATE,
                RunningProviderContract.DURATION,
                RunningProviderContract.DURATION_SECONDS,
                RunningProviderContract.DISTANCE,
                RunningProviderContract.AVG_SPEED,
                RunningProviderContract.RATING
        };

        int[] colResIds = new int[] {
                R.id._id,
                R.id.date,
                R.id.duration,
                R.id.duration_s,
                R.id.db_dis,
                R.id.avg_speed,
                R.id.rating,
        };

        Cursor cursor = getContentResolver().query(RunningProviderContract.RUNS_URI, projection, null, null, "_ID DESC");

        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_view_db_item,
                cursor,
                colsToDisplay,
                colResIds,
                0);


        ListView listView = (ListView) findViewById(R.id.runs_list);
        listView.setAdapter(dataAdapter);

        //this sorts database into from last run Id and descending
        try{
            //Sql query to sort
            String longest = "SELECT * FROM  RunLogs ORDER BY _ID DESC LIMIT 1";
            Cursor c = dbHelper.getReadableDatabase().rawQuery(longest,null);
            c.moveToFirst();
            //data extraction
            String durationTime = c.getString(c.getColumnIndex("duration"));
            String date = c.getString(c.getColumnIndex("date"));
            String distance = c.getString(c.getColumnIndex("distance"));
            //Log.d("date", date);
            //c.moveToFirst();
            String speed = c.getString(c.getColumnIndex("avg_speed"));
            //Log.d("speed", speed);

            //creates a popup which shows last run by the user
            AlertDialog alertDialog = new AlertDialog.Builder(ContentUser.this).create();
            alertDialog.setIcon(android.R.drawable.ic_menu_mylocation);
            alertDialog.setTitle("Last Run");
            alertDialog.setMessage("Date: " + date +"\n" + "Time: " + durationTime + "\n"+ "Speed: " + speed+" km/h" + "\n"+ "Distance: " + distance +" Metres" );
           ;
            alertDialog.show();
            //Log.d("validity", String.valueOf(x));

            //Log.d("best", c.getString(c.getColumnIndex("duration")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy(){
        //dbHelper.close();
        Log.d("Destroyed", "End of lifecycle");
        super.onDestroy();
    }
}