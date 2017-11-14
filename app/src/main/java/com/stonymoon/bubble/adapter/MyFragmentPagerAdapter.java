package com.stonymoon.bubble.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private List<String> titles;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> mFragments, List<String> titles) {
        super(fm);
        this.mFragments = mFragments;
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int position) {//必须实现
        return mFragments.get(position);
    }

    @Override
    public int getCount() {//必须实现
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) { //选择性实现
        return titles.get(position);
    }


}
