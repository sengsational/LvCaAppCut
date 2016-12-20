package com.company.cpp.lvcaapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class DetailCursorSlideActivity extends FragmentActivity {
    private static final String TAG = DetailCursorSlideActivity.class.getSimpleName();
    public static final String EXTRA_POSITION = "extra_position";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Cursor version running onCreate()");
        int position = -1;
        Object positionExtra = getIntent().getExtras().get(EXTRA_POSITION);
        if (positionExtra != null){
            position = (Integer)positionExtra;
        }
        Log.v(TAG, "onCreate().  position: " + position);
        LvCaApp.getCursor().moveToPosition(position);

        setContentView(R.layout.activity_screen_slide);
        ViewPager mPager = (ViewPager)findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new DetailCursorSlidePageAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position);
    }

    private static class DetailCursorSlidePageAdapter extends FragmentStatePagerAdapter {

        int count;

        DetailCursorSlidePageAdapter(FragmentManager fm){
            super(fm);
            this.count = LvCaApp.getCursor().getCount();
        }

        @Override
        public int getCount() {
            return this.count;
        }

        @Override
        public Fragment getItem(int position) {
            Log.v(TAG, "getItem with passed in position " + position);
            return DetailCursorSlideFragment.newInstance(position);
        }
    }
}
