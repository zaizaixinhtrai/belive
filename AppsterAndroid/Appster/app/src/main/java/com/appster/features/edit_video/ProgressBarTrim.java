package com.appster.features.edit_video;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.appster.R;

/**
 * Created by Ngoc on 7/5/2017.
 */

public class ProgressBarTrim extends AlertDialog {

    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;

    protected ProgressBarTrim(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mScreenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mScreenWidth, mScreenHeight);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewDialog = inflater.inflate(R.layout.compose_layout, null);
        setContentView(viewDialog, layoutParams);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }
}
