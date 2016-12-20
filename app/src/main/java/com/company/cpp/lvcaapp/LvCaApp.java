package com.company.cpp.lvcaapp;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by Owner on 12/1/2016.
 */

public class LvCaApp extends Application {
    private static final String TAG = LvCaApp.class.getSimpleName();

    public Context mContext;
    private static Cursor mCursor;

    public static void setCursor(Cursor cursor) {
        if(mCursor != null) {
            Log.v(TAG, "setCursor is closing existing cursor.");
            mCursor.close();
        }
        mCursor = cursor;
    }

    public static Cursor getCursor() {
        if (mCursor != null) return mCursor;
        else Log.v(TAG, "ERROR: The cursor was null!!!");
        return mCursor;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static void closeCursor() {
        if (mCursor != null) {
            Log.v(TAG, "closeCursor()");
            mCursor.close();
        }
    }
}
