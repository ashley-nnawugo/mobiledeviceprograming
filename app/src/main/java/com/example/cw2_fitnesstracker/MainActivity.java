package com.example.cw2_fitnesstracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ListView detailList;
    static final int REQUEST_CODE = 1;
    private final int MY_REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String for list of details of run
        String[] details = new String[]{
                "How far you have run today", "How much you have improved?", "Your best time"
        };
        //finding view of the details list
        detailList = (ListView) findViewById(R.id.details);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item, android.R.id.text1, details);
        detailList.setAdapter(adapter);
        detailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String detailPhrase = (String) detailList.getItemAtPosition(position);
            /* TODO implement best run, how much one has improved, how far one has ran
                TODO maybe in a new activity.
             */
            }

        });
    }

    //asks for permission, starts new activity and starts run count.
    public void onRun(View v) {

        //checks if permission is granted, if it has, this starts a new activity
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("g53mdp", "granted");
            Toast.makeText(this, "Using Location", Toast.LENGTH_SHORT).show();
            startRun();
        }
        //if the user hasn't been granted and it is asked again, this will explain to the user why the permission is needed
        else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION )) {

            Log.d("g53mdp", "explanation required");
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("explanation - this permission is required for this app to function");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", (dialog, which) ->
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION_PERMISSION
                    ));
            alertDialog.show();
        }
        //This asks for permission
       else if((ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION_PERMISSION
            );
            Log.d("g53mdp", "explanation not needed");
        }




    }
    public void startRun(){
        Intent intent = new Intent(MainActivity.this, RunTracker.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

   /* public void registerBroadcastReceiver() {
        Toast.makeText(this, "Location enabled", Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter("android.intent.action.GET_CONTENT");
        filter.setPriority(2);
        registerReceiver(new MyBroadcastReceiver(), filter);
        startRun();
    }

    */

    public void onRequestPermissionResult(int requestCode, String permission[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    Log.d("g53mdp", "permission granted");
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    Log.d("g53mdp", "permission denied");
                }
            }
        }
    }
}