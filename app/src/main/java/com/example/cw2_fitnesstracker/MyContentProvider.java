package com.example.cw2_fitnesstracker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
    public MyContentProvider() {
    }

    private DBHelper dbHelper = null;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RunningProviderContract.AUTHORITY, "runLogs" ,1);
        uriMatcher.addURI(RunningProviderContract.AUTHORITY, "runLogs/#" ,2);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        String contentType;

        if (uri.getLastPathSegment() == null) {
            contentType = RunningProviderContract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = RunningProviderContract.CONTENT_TYPE_SINGLE;
        }
        return contentType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;

        switch(uriMatcher.match(uri)) {
            case 1:
                tableName = "runLogs";
                break;
            case 2:
                tableName = "runLogs";
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        long id = db.insert(tableName, null, values);
        db.close();
        Uri nu = ContentUris.withAppendedId(uri, id);

        Log.d("g53mdp", nu.toString());

        getContext().getContentResolver().notifyChange(nu, null);

        return nu;
    }

    @Override
    public boolean onCreate() {
        this.dbHelper = new DBHelper(this.getContext(), "runLogs", null, 9);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch(uriMatcher.match(uri)){
            case 2:
                selection = "_ID = "  + uri.getLastPathSegment();
            case 1:
                return db.query("runLogs", projection, selection, selectionArgs, null, null, sortOrder);
            default:
                return null;
        }
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}