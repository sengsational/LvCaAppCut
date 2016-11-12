package com.company.cpp.lvcaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class RecyclerSqlListActivity extends AppCompatActivity {
    private static final String TAG = RecyclerSqlListActivity.class.getSimpleName();

    public static final int DETAIL_REQUEST = 0;

    public DbSqlliteAdapter repository;
    public MyRecyclerViewAdapter recyclerViewAdapter;

    private ArrayList<Model> modelList;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            repository = new DbSqlliteAdapter(this);
            repository.open();
            //repository.deleteAll();
            //repository.insertSome();

            modelList = repository.loadModelFromDatabase();// <<< This is select * from table into the modelList
            Log.v(TAG, "The modelList has " + modelList.size() + " entries.");

            recyclerViewAdapter = new MyRecyclerViewAdapter(this, modelList);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.hasFixedSize();

        } catch (Exception e) {
            Log.v(TAG, "Exception in onCreate " + e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == RecyclerSqlListActivity.DETAIL_REQUEST){
            Log.v(TAG, "onActivityResult fired <<<<<<<<<< resultCode:" + resultCode);
            //String[] changedItems = intent.getStringArrayExtra(RecyclerSqlListActivity.DETAIL_RESULTS); // We could return things we learned, such as which items were altered.  Or we could just update everything
            //modelList = repository.loadModelFromDatabase();// <<< This is select * from table into the modelList
            Integer[] changedPositions = DbSqlliteAdapter.getChangedPositions();
            for (Integer changedPosition : changedPositions) {
                Model aModel = modelList.get(changedPosition);
                DbSqlliteAdapter.loadModel(aModel);
                recyclerViewAdapter.notifyItemChanged(changedPosition);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (repository != null) repository.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume().");
        if (recyclerViewAdapter == null) {
            Log.v(TAG, "onResume() found no recyclerViewAdapter!");
        }
    }
}
