package com.stonymoon.bubble.view;

import android.content.Context;
import android.graphics.Color;

import com.stonymoon.bubble.application.MyApplication;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyDialog {
    private SweetAlertDialog pDialog;

    public MyDialog(Context context) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.BLUE);
    }

    public void showProgress(String title) {
        pDialog.setTitleText(title);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void success(String title) {
        pDialog.setTitleText(title);
        pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        //pDialog.show();
    }

    public void fail(String title) {
        pDialog.setTitleText(title);
        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        //pDialog.show();
    }

    public SweetAlertDialog getInstance() {
        return pDialog;
    }


}
