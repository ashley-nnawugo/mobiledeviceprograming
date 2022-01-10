package com.example.cw2_fitnesstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
/*
    TODO create a map to track service
    TODO create a counter to count time in a service
    TODO create interface which shows service
    TODO map which shows run
 */

public class RunTracker extends AppCompatActivity {

    private TrackerService.MyBinder myService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_tracker);
        //creates and binds services
        Intent intent = new Intent(RunTracker.this, TrackerService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

    }
    //instantiating the service connection
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            myService = (TrackerService.MyBinder) service;
            myService.registerCallback(callback);

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
            String time_string;
            float kilometres_ph;
            kilometres_ph = ms_to_kmh(speed);
            TextView time_view = (TextView) findViewById(R.id.textView);
            time_string = timeStandardisation(duration);
            time_view.setText(String.format("Time:"+time_string + "\n" +"Total distance: %d M" + "\nSpeed: %.2f km/h", distance,kilometres_ph));

            //TextView distance_view = (TextView) findViewById(R.id.distance);

            //distance_view.setText(distance_string);


        }
    };
    //TODO fix so it actually continues the run
    //Continues the run
    public void onContinue(View v){
        if(myService != null)
            myService.playRun();
    }

    //pause the run
    public void onPause(View v){
        if(myService != null)
            myService.pauseRun();
    }

    //standardises the count into minutes and seconds
    public String timeStandardisation(int seconds){
        int minutes = (seconds % 3600)/60;
        int second = seconds % 60;

        String timeFormatted = String.format("%02d:%02d", minutes,second);
        //Log.d("time", timeFormatted);
        return timeFormatted;
    }

    //this changes metres per second to kilometres per hour
    public float ms_to_kmh(int speed){
        float kmh = 0;
        float conversion = (60 * 60);
        float thousand = 1000;
        float float_speed = speed;
        kmh = float_speed * conversion/thousand;
        return kmh;
    }
}