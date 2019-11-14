package com.appster.dialog;

import android.view.View;
import android.view.Window;

/**
 * Created by linh on 13/05/2017.
 */

public abstract class FullScreenDialogFragment extends NoTitleDialogFragment {
    private boolean shouldShowStatusBar;

    public void setShouldShowStatusBar(boolean shouldShowStatusBar) {
        this.shouldShowStatusBar = shouldShowStatusBar;
    }

    @Override
    protected void setWindowMode(Window window) {
        final View decorView = window.getDecorView();
        if (shouldShowStatusBar){
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
