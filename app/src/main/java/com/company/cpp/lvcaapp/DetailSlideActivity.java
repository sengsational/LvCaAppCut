package com.company.cpp.lvcaapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;

public class DetailSlideActivity extends FragmentActivity {
    private static final String TAG = DetailSlideActivity.class.getSimpleName();

    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_LIST_MODEL = "extra_list_model";

    private static ArrayList<Model> listModel = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listModel = (ArrayList<Model>) getIntent().getExtras().get(EXTRA_LIST_MODEL);

        Log.v(TAG,"here's position in the slide activity:" + getIntent().getExtras().get(EXTRA_POSITION));
        int position = (Integer) getIntent().getExtras().get(EXTRA_POSITION);

        Log.v(TAG, "onCreate().  position: " + position);

        setContentView(R.layout.activity_screen_slide);
        ViewPager mPager = (ViewPager)findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new DetailSlidePageAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position);
    }

    private static class DetailSlidePageAdapter extends FragmentStatePagerAdapter {

        int count;

        DetailSlidePageAdapter(FragmentManager fm){
            super(fm);
            this.count = listModel.size();
        }

        @Override
        public int getCount() {
            return this.count;
        }

        @Override
        public Fragment getItem(int position) {
            Log.v(TAG, "getItem with passed in position " + position);
            return DetailSlideFragment.newInstance(position, listModel);
        }
    }
}
