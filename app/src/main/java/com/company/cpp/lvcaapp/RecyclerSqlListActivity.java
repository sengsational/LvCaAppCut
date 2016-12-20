package com.company.cpp.lvcaapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class RecyclerSqlListActivity extends AppCompatActivity {
    private static final String TAG = RecyclerSqlListActivity.class.getSimpleName();

    public static final int DETAIL_REQUEST = 0;

    public DbSqlliteAdapter repository;
    public MyCursorRecyclerViewAdapter cursorRecyclerViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate(), savedInstance state null " + (savedInstanceState == null));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            /******* Create a new database adapter **************/
            repository = new DbSqlliteAdapter(this); // Should be the only call to the constructor in the application
            repository.open(); // Should be the only call to open() in the application
            if (DbSqlliteAdapter.getRecordCount() < 5) {
                //repository.deleteAll(); // Delete if you want to start clean
                repository.insertSome(2);
            }

            Log.v(TAG, "onCreate() is pulling records from the database.");
            Cursor aCursor = DbSqlliteAdapter.fetchAll(null); // This would normally have selection criteria
            LvCaApp.setCursor(aCursor);  // Save a reference to the cursor for other activities
            cursorRecyclerViewAdapter = new MyCursorRecyclerViewAdapter(this, aCursor); // Create an adapter for this activity
            cursorRecyclerViewAdapter.hasStableIds();
            recyclerView.setAdapter(cursorRecyclerViewAdapter); // Assign the adapter to the view
            recyclerView.hasFixedSize();

        } catch (Exception e) {
            Log.v(TAG, "Exception in onCreate " + e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == RecyclerSqlListActivity.DETAIL_REQUEST){
            Log.v(TAG, "onActivityResult fired <<<<<<<<<< resultCode:" + resultCode);
            Log.v(TAG, "onActivityResult running!!");
            cursorRecyclerViewAdapter.changeCursor(LvCaApp.getCursor());
        }
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        super.onDestroy();
        if (repository != null) repository.closeDbHelper();
        LvCaApp.closeCursor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume().");
        if (cursorRecyclerViewAdapter == null) {
            Log.v(TAG, "onResume() found no cursorRecylcerViewAdapter!");
        }
    }
}
