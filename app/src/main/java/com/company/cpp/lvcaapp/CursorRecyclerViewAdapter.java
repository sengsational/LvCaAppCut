package com.company.cpp.lvcaapp;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private static final String TAG = CursorRecyclerViewAdapter.class.getSimpleName();

    private boolean mDataValid;
    private Cursor mCursor;
    private int mRowIDColumn;
    private DataSetObserver mDataSetObserver;

    /* */
    CursorRecyclerViewAdapter(Cursor cursor) {
        Log.v(TAG, "CursorRecyclerViewAdapter constructor with cursor " + cursor);
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIDColumn = (cursor != null) ? cursor.getColumnIndexOrThrow("_id") : -1;
        mDataSetObserver = new RecyclerViewDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        setHasStableIds(true);
    }

    /* */
    @Override
    public final void onBindViewHolder (VH holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(holder, mCursor);
    }

    /* */
    public abstract void onBindViewHolder(VH holder, Cursor cursor);

    /* */
    public Cursor getCursor() {
        return mCursor;
    }

    /* */
    @Override
    public int getItemCount () {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /* */
    @Override
    public long getItemId (int position) {
        if(hasStableIds() && mDataValid && mCursor != null){
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return RecyclerView.NO_ID;
            }
        } else {
            return RecyclerView.NO_ID;
        }
    }

    /* */
    public Cursor changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
        return cursor;
    }

    /* */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if(mDataSetObserver != null) {
                newCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    private class RecyclerViewDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            Log.v(TAG, "RecyclerViewDataSetObserver.onChanged()");
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            Log.v(TAG, "RecyclerViewDataSetObserver.onInvalidated()");
            mDataValid = false;
            notifyItemRangeRemoved(0, getItemCount());
            // notifyDataSetChanged();
        }
    }
}