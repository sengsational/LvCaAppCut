package com.company.cpp.lvcaapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailSlideFragment extends Fragment {
    private static final String TAG = DetailSlideFragment.class.getSimpleName();

    private static final String ARG_POSITION = "position";
    private static final String ARG_LIST_MODEL = "listModel";

    public static DetailSlideFragment newInstance(int position, ArrayList<Model> listModel) {
        DetailSlideFragment fragment = new DetailSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable(ARG_LIST_MODEL, listModel);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailSlideFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate() ARG_POSITION position: " + getArguments().getInt(ARG_POSITION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // >>>>>>>>>>>SET THE CONTENT (TEXT AND VISIBILITY) THAT THE USER WILL SEE FROM THE SERIALIZED VERSION PASSED-IN<<<<<<<<

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_detail, container, false);

        ArrayList<Model> listModelSerializedCopy = (ArrayList<Model>)getArguments().getSerializable(ARG_LIST_MODEL);
        final int position = getArguments().getInt(ARG_POSITION);

        final Model modelItemSerializedCopy = listModelSerializedCopy.get(position);

        ((TextView)rootView.findViewById(R.id.detailname)).setText(modelItemSerializedCopy.getName());
        ((TextView)rootView.findViewById(R.id.description)).setText(modelItemSerializedCopy.getDescription());
        ((TextView)rootView.findViewById(R.id.style)).setText(modelItemSerializedCopy.getSecond_line());
        ((TextView)rootView.findViewById(R.id.city)).setText("Charlotte, NC");

        String hidden = modelItemSerializedCopy.getHidden();
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
                String modelHighlighted = modelItemSerializedCopy.getHidden();
                Log.v(TAG, "iconView was visible? " + (wasVisibleIcon) + " modelHighlighted letter: " + modelHighlighted);

                if("F".equals(modelHighlighted)) { // there was no icon showing, so show it
                    iconView.setVisibility(View.VISIBLE);
                    modelItemSerializedCopy.setHidden("T");
                } else {
                    iconView.setVisibility(View.GONE);
                    modelItemSerializedCopy.setHidden("F");
                }
                Log.v(TAG, "updating the database from the changed serialized item");
                DbSqlliteAdapter.update(modelItemSerializedCopy, position);

                //RecyclerSqlListActivity.recyclerViewAdapter.notifyItemChanged(position);
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
