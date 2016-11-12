package com.company.cpp.lvcaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

class DbSqlliteAdapter {
    private static final String TAG = DbSqlliteAdapter.class.getSimpleName();

    private static DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;

    private static final HashSet<Integer> positionSet = new HashSet<>();

    private static final String DATABASE_NAME = "adbname";
    private static final String SQLITE_TABLE = "atablename";
    private static final int DATABASE_VERSION = 4;

    private final Context mContext;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "NAME TEXT, " +
                    "SECOND_LINE, " +
                    "HIDDEN, " +
                    "DESCRIPTION" +
                    ");";

    static Model loadModel(Model model) {
        Model dbModel = DbSqlliteAdapter.getById(model.getId() + "");
        Log.v(TAG, "looked up " + model.getId() + " and it found " + dbModel.toString());
        model.setId(dbModel.getId());
        model.setName(dbModel.getName());
        model.setSecond_line(dbModel.getSecond_line());
        model.setDescription(dbModel.getDescription());
        model.setHidden(dbModel.getHidden());
        return model;
    }

    // INNER CLASS DatabaseHelper
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static DatabaseHelper mDatabaseHelper;

        // SINGLETON PATTERN
        private DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

        static synchronized DatabaseHelper getInstance(Context context) {
            if (mDatabaseHelper == null) {
                mDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
            }
            return mDatabaseHelper;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }

    }

    DbSqlliteAdapter(Context context) {
        this.mContext = context;
    }

    private static ContentValues fillModelValues(Model model) {
        ContentValues values = new ContentValues();
        values.put("_id", model.getId());
        values.put("NAME",model.getName());
        values.put("SECOND_LINE", model.getSecond_line());
        values.put("HIDDEN",  model.getHidden());
        values.put("DESCRIPTION", model.getDescription());
        return values;
    }

    private void insert(Model model){
        ContentValues values = fillModelValues(model);
        values.remove("_id"); // automatically assigned by the database
        Log.v(TAG, "Inserting values: " + model);
        mDb.insertOrThrow(SQLITE_TABLE, null, values);
    }

    static void update(Model model) {
        ContentValues values = fillModelValues(model);
        Log.v(TAG, "Working on model that looks like this: " + model);
        Log.v(TAG, "Updating record " + values.get(Model.ID) + " in the database.");
        mDb.update(SQLITE_TABLE, values, Model.ID + "=?", new String[] {"" + values.get(Model.ID)});
        Model resultInDb = getById("" + values.get(Model.ID));
        Log.v(TAG, "after update, resultInDb: " + resultInDb);
    }

    static void update(Model model, int position) {
        positionSet.add(position);
        update(model);
    }

    static Integer[] getChangedPositions() {
        Integer[] positions = positionSet.toArray(new Integer[0]);
        positionSet.clear();
        return positions;
    }

    ArrayList<Model> loadModelFromDatabase() {
        ArrayList<Model> listModel = new ArrayList<>();

        Cursor cursor = fetchAll(null); // null is all columns

        if (cursor != null) {
            do {
                Model model = new Model();
                model.setId(cursor.getLong(cursor.getColumnIndex(Model.ID)));
                model.setName(cursor.getString(cursor.getColumnIndex(Model.NAME)));
                model.setSecond_line(cursor.getString(cursor.getColumnIndex(Model.SECOND_LINE)));
                model.setHidden(cursor.getString(cursor.getColumnIndex(Model.HIDDEN)));
                model.setDescription(cursor.getString(cursor.getColumnIndex(Model.DESCRIPTION)));
                model.setName(cursor.getString(cursor.getColumnIndex(Model.NAME)));
                listModel.add(model);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return listModel;
    }

    public DbSqlliteAdapter open() throws SQLException {
        mDbHelper = DatabaseHelper.getInstance(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }
    private Cursor fetchAll(String[] fields) {
        Cursor mCursor = mDb.query(SQLITE_TABLE, fields, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    private static Model getById(String id) {
        Cursor mCursor = mDb.query(SQLITE_TABLE, null, "_id=?", new String[]{id}, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return new Model(mCursor);
    }

    void insertSome() {
        String sampleData = "[{\"name\":\"Entry 1\",\"second_line\":\"Second Line 1\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 2\",\"second_line\":\"Second Line 2\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 3\",\"second_line\":\"Second Line 3\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 4\",\"second_line\":\"Second Line 4\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"EntryH 5\",\"second_line\":\"Second Line 5\",\"hidden\":\"T\"},{\"name\":\"Entry 6\",\"second_line\":\"Second Line 6\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 7\",\"second_line\":\"Second Line 7\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 8\",\"second_line\":\"Second Line 8\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 9\",\"second_line\":\"Second Line 9\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"EntryH 10\",\"second_line\":\"Second Line 10\",\"hidden\":\"T\"},{\"name\":\"Entry 11\",\"second_line\":\"Second Line 11\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 12\",\"second_line\":\"Second Line 12\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 13\",\"second_line\":\"Second Line 13\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 14\",\"second_line\":\"Second Line 14\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"EntryH 15\",\"second_line\":\"Second Line 15\",\"hidden\":\"T\"},{\"name\":\"Entry 16\",\"second_line\":\"Second Line 16\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 17\",\"second_line\":\"Second Line 17\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 18\",\"second_line\":\"Second Line 18\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 19\",\"second_line\":\"Second Line 19\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"EntryH 20\",\"second_line\":\"Second Line 20\",\"hidden\":\"T\"},{\"name\":\"Entry 21\",\"second_line\":\"Second Line 21\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 22\",\"second_line\":\"Second Line 22\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"},{\"name\":\"Entry 23\",\"second_line\":\"Second Line 23\",\"hidden\":\"F\",\"description\":\"This is a description for the item.\"}]";
        String[] items = sampleData.split("\\},\\{");
        for(String item: items){
            Model model = new Model();
            model.load(item);
            if(model.getName().contains("Hide")){
                model.setHidden("T");
            }
            insert(model);
        }
    }

    boolean deleteAll() {
        int doneDelete = mDb.delete(SQLITE_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }
}









































