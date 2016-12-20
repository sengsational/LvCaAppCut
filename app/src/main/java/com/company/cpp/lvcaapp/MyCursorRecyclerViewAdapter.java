package com.company.cpp.lvcaapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class MyCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter<MyCursorRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = MyCursorRecyclerViewAdapter.class.getSimpleName();
    private Context mContext;

    public MyCursorRecyclerViewAdapter(Context context, Cursor c) {
        super(c);
        mContext = context;
    } // Constructor

    @Override
    public MyCursorRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.v(TAG, "onCreateViewHolder");
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View modelView = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(modelView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final Model model = new Model(cursor);
        //getCursor();
        final int layoutPosition = viewHolder.getLayoutPosition();
        //Log.v(TAG, "onBindViewHolder " + layoutPosition);

        viewHolder.nameTextView.setText(model.getName());
        viewHolder.secondLineTextView.setText(model.getSecond_line());
        viewHolder.dbItemTextView.setText(model.getId() + "");
        viewHolder.hiddenTextView.setText(model.getHidden());
        viewHolder.descriptionTextView.setText(model.getDescription());

        // Alter view depending on database contents
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

                Intent intent = new Intent(mContext, DetailCursorSlideActivity.class);
                intent.putExtra(DetailCursorSlideActivity.EXTRA_POSITION, layoutPosition);
                ((Activity)mContext).startActivityForResult(intent, RecyclerSqlListActivity.DETAIL_REQUEST);
            }
        });

        // If the item is long-clicked, we want to change the icon in the model and in the database
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                String hidden = model.getHidden();
                Log.v(TAG, "hidden string was : "  + hidden);

                if ("F".equals(hidden)) {
                    model.setHidden("T");
                    view.findViewById(R.id.bIcon).setVisibility(View.INVISIBLE);
                } else {
                    model.setHidden("F");
                    view.findViewById(R.id.bIcon).setVisibility(View.VISIBLE);
                }
                boolean updateDatabase = true;
                if (updateDatabase) {
                    Log.v(TAG, "updating the database");
                    DbSqlliteAdapter.update(model);
                    LvCaApp.setCursor(DbSqlliteAdapter.fetchAll(null)); // DOING A QUERY TO GET A FRESH CURSOR WITH CORRECT (UPDATED) DATA
                    changeCursor(LvCaApp.getCursor());
                } else {
                    Log.v(TAG, "not doing my own database update");
                }
                Log.v(TAG, "notifyItemChanged being called and hasObservers is " + hasObservers());
                //int position = mModelList.indexOf(model);
                //Log.v(TAG, "position was : "  + position);
                Log.v(TAG, "layoutPosition was : " + layoutPosition);

                notifyItemChanged(layoutPosition); // RecyclerView.notifyItemChanged();


                boolean longClickConsumed = true;  // no more will happen
                return longClickConsumed;
            }
        });
    }

    @Override
    public int getItemCount() {
        //Log.v(TAG, "getItemCount() returning " + super.getItemCount() + " mRecordCount " + mRecordCount);
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder  {
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
}




