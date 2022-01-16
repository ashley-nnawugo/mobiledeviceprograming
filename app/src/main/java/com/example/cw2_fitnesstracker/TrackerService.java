package com.example.cw2_fitnesstracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.time.Duration;
import java.util.TimerTask;

public class TrackerService extends Service {
    public TrackerService() {
    }
    protected Counter counter;
    public static float total_distance = 0;
    public static int total_seconds = 0;
    public static String time_format;
    public static Location original_location = new Location("A");
    public static Location new_location = new Location("B");
    public static Location location_store = new Location("C");




    protected class Counter extends Thread implements Runnable{
        public boolean direction = true;
        public int count = 0;
        public boolean running = true;
        public Location starting_location =  new Location("Start");
        int speed;

        public Counter() {
            this.start();
        }
        public void run() {

            while(this.running) {
                float distance = 0;

                try {Thread.sleep(1000);} catch(Exception e) {return;}

                 MyLocationListener myLocationListener = new MyLocationListener();
                 myLocationListener.onLocationChanged(original_location);

                //this is to time lag the location
                //so that a difference in previous distance can be recorded
                //equivalent to a 5 second lag
                if(count % 5 == 0){
                    location_store = original_location;
                }

                //used to calculate the distance travelled, works by calculating difference between
                //the previous locations (location_store) and the new location
                if(location_store != new_location ) {
                    Log.d("location_old", String.valueOf(location_store));
                    Log.d("location_new", String.valueOf(new_location));
                    if(location_store.distanceTo(new_location) < 60000)
                        distance = (location_store.distanceTo(new_location))/2;
                    total_distance += distance;
                }

                //Log.d("location_new", String.valueOf(new_location));

                Log.d("distance", String.valueOf(distance));

                if(direction)
                    count++;
                else
                    count--;

                total_seconds = count;

                //setting time format to standardisation
                time_format = timeStandardisation(count);

                speed = average_speed();
                Log.d("speed", String.valueOf(speed));
                //myLocationListener.onLocationChanged(new_location);
                //Log.d("location_new", String.valueOf(new_location));

                //send back speed, count (seconds) and total distance
                doCallbacks(speed, count , Math.round(total_distance), new_location);
                timeStandardisation(count);
                //Log.d("g53mdp", "Service counter " + count);


            }

           // Log.d("g53mdp", "Service counter thread exiting");
        }
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            //Log.d("comp3018", location.getLatitude() + " " + location.getLongitude());
            //stores location in original and new location
            original_location = location;
            new_location = location;
           // Log.d("location", original_location.toString());

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("comp3018", "onStatusChanged: " + provider + " " + status); }
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("comp3018", "onProviderEnabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("comp3018", "onProviderDisabled: " + provider);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

       // throw new UnsupportedOperationException("Not yet implemented");
        return new MyBinder();
    }

    RemoteCallbackList<MyBinder> remoteCallbackList = new RemoteCallbackList<MyBinder>();

    public class MyBinder extends Binder implements IInterface {

        //pausing the run
        void pauseRun(){
            TrackerService.this.counter.running = false;
        }

        //playing the run
        void restartRun(){
           // TrackerService.this.counter.running = true;
            counter = new Counter();
            //counter.start();
            //boolean x = getStatus();
           // Log.d("Service Status", String.valueOf(x));

        }

        //getting run status
        /*
        boolean getStatus() {
            boolean status = Boolean.parseBoolean(null);
            if(TrackerService.this.counter.running != Boolean.parseBoolean(null)) {
                status = TrackerService.this.counter.running;
            }

            return status;
        }

         */

        void registerCallback(ICallback callback){
            this.callback = callback;
            remoteCallbackList.register(MyBinder.this);

        }

        void unregisteredCallback(ICallback callback){
            remoteCallbackList.unregister(MyBinder.this);
        }


        ICallback callback;
        @Override
        public IBinder asBinder() {
            return this;
        }
    }


    private void doCallbacks(int speed, int duration, int distance, Location new_location){
        final int n = remoteCallbackList.beginBroadcast();
        for(int i = 0; i < n; i++){
            // TODO CHANGE VARIABLES
            remoteCallbackList.getBroadcastItem(i).callback.runCounter(speed,duration, distance);

        }
        remoteCallbackList.finishBroadcast();
    }

    public void onCreate(){
        super.onCreate();

        //this allows the run tracker to be reran after the first run has ended
        if(!MainActivity.start_run)
            counter = new Counter();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);

        }

        Intent Notif_intent = new Intent(TrackerService.this, MainActivity.class);
        Notif_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, Notif_intent,0);


        //TODO see if you can make it replay live data
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANEL_ID);
        mBuilder.setSmallIcon(android.R.drawable.ic_media_play);
        mBuilder.setContentTitle("Fitness Tracker");
        mBuilder.setContentText("Time:" + time_format +" distance: " + String.valueOf(total_distance));
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(NOTIFICATION_ID,mBuilder.build());

    }

    private final int NOTIFICATION_ID = 001; //unique number for each notification
    private final String CHANEL_ID = "100";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener();
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5, // minimum time interval between updates
                5, locationListener);// minimum distance between updates, in metres locationListener);
            Log.d("service","binded");
        }
        catch(SecurityException e) {
            Log.d("ts", e.toString());
        }


        //Location myLocation = new Location();
        //float distance =
        return super.onStartCommand(intent, flags, startId);
    }
    // function is used to calculate average speed by dividing total distance by time
    public int average_speed(){
        int average_speed = (int) (total_distance / total_seconds);
        return average_speed;
    }

    //TODO move to RunTracker.java
    //this code formats time into minutes and seconds
    public String timeStandardisation(int seconds){
        int hours = seconds /3600;
        int minutes = (seconds % 3600)/60;
        int second = seconds % 60;

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, second);
        return timeFormatted;
    }
}