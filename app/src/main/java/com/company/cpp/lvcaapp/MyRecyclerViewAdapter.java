package com.company.cpp.lvcaapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = MyRecyclerViewAdapter.class.getSimpleName();

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView nameTextView;
        TextView secondLineTextView;
        TextView dbItemTextView;
        TextView hiddenTextView;
        TextView descriptionTextView;

        ViewHolder(View itemView) {
            super(itemView);

            iconImageView = (ImageView) itemView.findViewById(R.id.bIcon);
            nameTextView = (TextView) itemView.findViewById(R.id.bName);
            secondLineTextView = (TextView) itemView.findViewById(R.id.bSecondLine);
            dbItemTextView = (TextView) itemView.findViewById(R.id.bDbItem);
            hiddenTextView = (TextView) itemView.findViewById(R.id.bHidden);
            descriptionTextView = (TextView) itemView.findViewById(R.id.bDescription);
        }
    }
    private List<Model> mModelList;
    private Context mContext;

    MyRecyclerViewAdapter(Context context, List<Model> modelList) {
        mContext = context;
        mModelList = new ArrayList<>(modelList);
    }

    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View modelView = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(modelView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Model model = mModelList.get(position);

        viewHolder.nameTextView.setText(model.getName());
        viewHolder.secondLineTextView.setText(model.getSecond_line());
        viewHolder.dbItemTextView.setText(model.getId() + "");
        viewHolder.hiddenTextView.setText(model.getHidden());
        viewHolder.descriptionTextView.setText(model.getDescription());

        if ("F".equals(model.getHidden())) {
            viewHolder.secondLineTextView.setVisibility(View.VISIBLE);
            viewHolder.iconImageView.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.secondLineTextView.setVisibility(View.INVISIBLE);
            viewHolder.iconImageView.setVisibility(View.VISIBLE);
        }

        // DEFINE ACTIVITY THAT HAPPENS WHEN ITEM IS CLICKED
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "setOnClickListener fired with view " + view); // view is RelativeLayout from list_item.xml

                int position = mModelList.indexOf(model);
                Log.v(TAG, "position was : "  + position);

                Intent intent = new Intent(mContext, DetailSlideActivity.class);
                intent.putExtra(DetailSlideActivity.EXTRA_LIST_MODEL, (Serializable)mModelList);
                intent.putExtra(DetailSlideActivity.EXTRA_POSITION, position);
                ((Activity)mContext).startActivityForResult(intent, RecyclerSqlListActivity.DETAIL_REQUEST);
            }
        });

        // If the item is long-clicked, we want to change the icon in the model and in the database
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.v(TAG, "setOnLongClickListener fired with view " + view); // view is RelativeLayout from list_item.xml
                Log.v(TAG, "setOnLongClickListener getTag method gave us position: " + view.getTag());

                int position = mModelList.indexOf(model);
                Log.v(TAG, "position was : "  + position);

                String hidden = model.getHidden();
                Log.v(TAG, "hidden string was : "  + hidden);


                if ("F".equals(hidden)) {
                    model.setHidden("T");
                    DbSqlliteAdapter.update(model);
                    view.findViewById(R.id.bIcon).setVisibility(View.INVISIBLE);
                } else {
                    model.setHidden("F");
                    view.findViewById(R.id.bIcon).setVisibility(View.VISIBLE);
                }
                Log.v(TAG, "updating the database");
                DbSqlliteAdapter.update(model);
                Log.v(TAG, "notifyItemChanged being called");
                notifyItemChanged(position);

                /*
                //Log.v(TAG, "the view had ")
                //Log.v(TAG, "the class had a listener reference: " + listener);

                TextView dbItemTextView = (TextView)view.findViewById(R.id.bDbItem);

                Log.v(TAG, "bDbItem text view had (from textview)" + dbItemTextView.getText());

                //String bdbItem = viewHolder.bDbItem.getText().toString();
                // Log.v(TAG, "bDbItem text view had   (from holder)" + bdbItem);


                // POSITION DOESN"T WORK
                //TextView dbItemTextViewx = (TextView)view.findViewById(R.id.bDbItem);
                //int position =  Integer.getInteger(dbItemTextView.getText().toString());
                //String hidden = mModelList.get(position).getHidden();
                //listener.itemToChange(position, holder);
                */

                //boolean longClickConsumed = false;  // more will happen :)
                boolean longClickConsumed = true;  // no more will happen :)
                return longClickConsumed;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }
}



