package com.example.cw2_fitnesstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("g53mdp", "MyBroadcastReceiver onReceive");
        Toast.makeText(context, "Location Permitted", Toast.LENGTH_LONG).show();
        //TODO get rid of this class

        // why would you want to do this?
        Intent i = new Intent(context, RunTracker.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}