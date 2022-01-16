package com.example.cw2_fitnesstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.Touch;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/*
    TODO create a map to track service
    TODO map which shows run
 */

public class RunTracker extends AppCompatActivity {

    private TrackerService.MyBinder myService = null;

    public static int total_seconds = 0;
    public static String total_time;
    public static int total_distances = 0;
    public static float avg_speed = 0;
    public static Boolean status;

    GoogleMap map;
    SupportMapFragment supportMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_tracker);
        //creates and binds services
        Intent intent = new Intent(RunTracker.this, TrackerService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.d("RunTracker", "onCreate");



    }
    //instantiating the service connection
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            myService = (TrackerService.MyBinder) service;
            myService.registerCallback(callback);
            //status = myService.getStatus();
            Log.d("status", String.valueOf(status));
            if(MainActivity.start_run == true)
                myService.restartRun();

        }
        @Override
        public void onServiceDisconnected(ComponentName name){
            myService.unregisteredCallback(callback);
            myService = null;
        }

    };

    ICallback callback = new ICallback(){

        @Override
        public void runCounter(int speed, int duration, int distance) {
            //this displays the time onto the screen
            total_distances = distance;
            total_seconds = duration;
            avg_speed = speed;

            String time_string;
            float kilometres_ph;
            kilometres_ph = ms_to_kmh(speed);
            TextView time_view = (TextView) findViewById(R.id.textView);
            time_string = timeStandardisation(duration);
            time_view.setText(String.format("Time:"+time_string + "\n" +"Total distance: %d M" + "\nSpeed: %.2f km/h", distance,kilometres_ph));
            total_time = time_string;



            //TextView distance_view = (TextView) findViewById(R.id.distance);
            //distance_view.setText(distance_string);
        }
    };
    /*
    //Continues the run
    public void onContinue(View v){
        if(myService != null) {
            myService.restartRun();
            //status = myService.getStatus();

        }
    }



    //pause the run
    public void onPause(View v){
        if(myService != null)
            myService.pauseRun();
    }



     */
    //TODO ADD HOVER OVER MESSAGE
    //Ends run and saves data to the database
    public void onEnd(View v){
        String rating = "Comment here";

        if(myService != null){
            myService.pauseRun();

            DBHelper dbHelper;
            dbHelper = new DBHelper(this, "runLogs",null, 9);


            //allowing user to comment on the run
            EditText comment = (EditText) findViewById(R.id.comment_run);
            if(!comment.getText().toString().equals("Comment here")) {
                rating = (String) comment.getText().toString();
                Log.d("rating", rating);
            }

            Date currentTime = Calendar.getInstance().getTime();
            Log.d("date", String.valueOf(currentTime));
            Log.d("time", String.valueOf(total_time));
            Log.d("distance", String.valueOf(total_distances));
            Log.d("avg_speed",String.valueOf(avg_speed));
            Log.d("seconds", String.valueOf(total_seconds));


            //saving the run variables
            if(!rating.equals("Comment here")) {
                SQLiteDatabase db;
                //dbHelper.onCreate(db);
                dbHelper.insertList(total_distances, total_seconds, total_time, avg_speed, currentTime, rating);
                Toast.makeText(this, "Saved run", Toast.LENGTH_SHORT).show();
            }
            //this sets run to false after end of activity.
            MainActivity.start_run = false;
            //ends activity after button is clicked
            finish();
        }
    }


    //standardises the count into minutes and seconds
    public String timeStandardisation(int seconds){
        int hours = seconds /3600;
        int minutes = (seconds % 3600)/60;
        int second = seconds % 60;

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, second);
        //Log.d("time", timeFormatted);
        return timeFormatted;
    }

    //this changes metres per second to kilometres per hour
    // converting to float to get more accurate speed
    public float ms_to_kmh(int speed){
        float kmh = 0;
        float conversion = (60 * 60);
        float thousand = 1000;
        float float_speed = speed;
        kmh = float_speed * conversion/thousand;
        return kmh;
    }
}