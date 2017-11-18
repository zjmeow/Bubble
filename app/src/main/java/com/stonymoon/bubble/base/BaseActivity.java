package com.stonymoon.bubble.base;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.stonymoon.bubble.R;

public abstract class BaseActivity extends AppCompatActivity {
    Toolbar toolbar;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        toolbar = (Toolbar) findViewById(R.id.toolbar_base);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

    }


    protected void setToolbarColor(int color) {
        toolbar.setBackgroundColor(color);
    }

    protected void setToolbarTitle(String text) {
        getSupportActionBar().setTitle(text);
    }

    protected void setToolbarTextColor(int color) {
        toolbar.setTitleTextColor(color);
    }

    protected abstract int getLayoutId();


}
