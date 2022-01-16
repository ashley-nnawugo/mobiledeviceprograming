package com.example.cw2_fitnesstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.database.ContentObserver;
import android.database.Cursor;
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
    //MainActivity mainActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_user);
        dbHelper = new DBHelper(this, "runLogs",null, 9);

        //String key;
        //mainActivity.getIntent().getData();


        queryRuns();

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

    @Override
    protected void onDestroy(){
        //dbHelper.close();
        Log.d("Destroyed", "End of lifecycle");
        super.onDestroy();
    }
}