package com.company.cpp.lvcaapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailCursorSlideFragment extends Fragment {
    private static final String TAG = DetailCursorSlideFragment.class.getSimpleName();

    private static final String ARG_POSITION = "position";
    private static MyCursorRecyclerViewAdapter mCursorRecyclerViewAdapter;  // Couldn't figure out how NOT to have a static variable in this Android context class

    public static DetailCursorSlideFragment create(Cursor cursor) {
        DetailCursorSlideFragment fragment = new DetailCursorSlideFragment();
        return fragment;
    }

    public static DetailCursorSlideFragment newInstance(int position) {
        DetailCursorSlideFragment fragment = new DetailCursorSlideFragment();
        Bundle args = new Bundle();
        Log.v(TAG, "newInstance(" + position +")");
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailCursorSlideFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (mCursorRecyclerViewAdapter == null) {
            mCursorRecyclerViewAdapter = new MyCursorRecyclerViewAdapter(getContext(), LvCaApp.getCursor()); //Pull cursor from the application class
            mCursorRecyclerViewAdapter.hasStableIds();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_detail, container, false);

        final int position = getArguments().getInt(ARG_POSITION);

        Log.v(TAG, "onCreateView position: " + position);
        LvCaApp.getCursor().moveToPosition(position); // This must be here
        final Model modelItem = new Model(LvCaApp.getCursor());

        ((TextView)rootView.findViewById(R.id.detailname)).setText(modelItem.getName());
        ((TextView)rootView.findViewById(R.id.description)).setText(modelItem.getDescription());
        ((TextView)rootView.findViewById(R.id.style)).setText(modelItem.getSecond_line());
        ((TextView)rootView.findViewById(R.id.city)).setText("Charlotte, NC");

        String hidden = modelItem.getHidden();
        ImageView iconView = (ImageView)rootView.findViewById(R.id.highlighted);
        if("T".equals(hidden)){
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.GONE);
        }


        // >>>>>>>>>>>>>>IF THE USER LONG-CLICKS, UPDATE THE DISPLAY THAT THEY SEE, UPDATE MODEL AND DB, THEN NOTIFY CHANGE SO VIEW CAN BE UPDATED<<<<<<
        TextView description = (TextView)rootView.findViewById(R.id.description);
        description.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ImageView iconView = (ImageView)((RelativeLayout)view.getParent()).findViewById(R.id.highlighted);
                boolean wasVisibleIcon = iconView.getVisibility() == View.VISIBLE;

                String modelHighlighted = modelItem.getHidden();
                Log.v(TAG, "iconView was visible? " + (wasVisibleIcon) + " modelHighlighted letter: " + modelHighlighted);

                if("F".equals(modelHighlighted)) { // there was no icon showing, so show it
                    iconView.setVisibility(View.VISIBLE);
                    modelItem.setHidden("T");
                } else {
                    iconView.setVisibility(View.GONE);
                    modelItem.setHidden("F");
                }
                Log.v(TAG, "updating the database from the changed model with position " + position);
                DbSqlliteAdapter.update(modelItem);

                // LvCaApp.setCursor(mCursor);
                // Without this 'changeCursor', if the user makes a change and swipes away, the back, the cursor is re-read and the flag disappears
                // Since this has it's own cursor, there is no need to do anything to make it realize there were changes!!!
                LvCaApp.setCursor(DbSqlliteAdapter.fetchAll(null)); // DOING A QUERY TO GET A FRESH CURSOR WITH CORRECT (UPDATED) DATA
                mCursorRecyclerViewAdapter.changeCursor(LvCaApp.getCursor());

                boolean longClickConsumed = true;
                return longClickConsumed;
            }
        });
        return rootView;
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
