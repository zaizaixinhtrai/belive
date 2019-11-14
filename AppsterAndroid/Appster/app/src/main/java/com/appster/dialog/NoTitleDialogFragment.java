package com.appster.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.appster.R;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by linh on 10/11/2017.
 */

public abstract class NoTitleDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null){
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//this line must be set first
            setWindowMode(window);
            window.setBackgroundDrawable(getWindowBackground());
            if (isDimDialog()){
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setDimAmount(dimAmount());
            }
            window.getAttributes().windowAnimations = getWindowAnimation();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            Timber.e("-- SYSTEM_UI_FLAG_IMMERSIVE_STICKY");
        }
        Timber.d("onCreateDialog");
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(getRootLayoutResource(), container, false);
        ButterKnife.bind(this, root);
        Timber.d("onCreateView");
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart");
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    protected int getWindowAnimation(){
        return R.style.DialogZoomAnimation;
    }

    protected Drawable getWindowBackground(){
        return new ColorDrawable(ContextCompat.getColor(getContext(),R.color.trans_parent));
    }

    protected void setWindowMode(Window window){
        final View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }
    protected @LayoutRes
    abstract int getRootLayoutResource();
    protected abstract boolean isDimDialog();
    protected float dimAmount(){
        return 0.6f;
    }
}
