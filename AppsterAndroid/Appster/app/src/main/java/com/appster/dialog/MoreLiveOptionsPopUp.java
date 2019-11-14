package com.appster.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.appster.R;
import com.apster.common.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by linh on 04/07/2017.
 */

public class MoreLiveOptionsPopUp extends PopupWindow {
    @Bind(R.id.onOffBeauty)
    ImageButton onOffBeauty;
    @Bind(R.id.onOffFlash)
    ImageButton onOffFlash;

    private boolean isFlashOpened;
    private boolean isBeautyOpened;
    private MoreLiveOptionsPopUpListener mListener;

    public static MoreLiveOptionsPopUp newInstance(Context context) {
        View root = LayoutInflater.from(context).inflate(R.layout.popup_window_live_more_options, null, false);
        return new MoreLiveOptionsPopUp(root, Utils.dpToPx(125), Utils.dpToPx(60));
    }

    public MoreLiveOptionsPopUp(View contentView, int width, int height) {
        super(contentView, width, height);
        ButterKnife.bind(this, contentView);
//        setFlashOpened(isFlashOpened);
        setBeautyOpened(isBeautyOpened);
        setupCallback();
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setFlashOpened(boolean flashOn) {
        isFlashOpened = flashOn;
        onOffFlash.setImageResource(flashOn ? R.drawable.icon_flash_on : R.drawable.icon_flash_off);
    }

    public void setBeautyOpened(boolean beautyOpened) {
        isBeautyOpened = beautyOpened;
        onOffBeauty.setImageResource(beautyOpened ? R.drawable.icon_beauty_on : R.drawable.icon_beauty_off);
    }

    public void setShouldEnableFlashButton(boolean shouldEnableFlashButton) {
        onOffFlash.setAlpha(shouldEnableFlashButton ? 1f : 0.5f);
    }

    public void setListener(MoreLiveOptionsPopUpListener listener) {
        mListener = listener;
    }

    void setupCallback() {
        onOffBeauty.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onBeautyOptionSelected();
            }
        });
        onOffFlash.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFlashOptionSelected();
            }
        });
    }

    public void unbind() {
        ButterKnife.unbind(this);
    }

    public interface MoreLiveOptionsPopUpListener {
        void onFlashOptionSelected();

        void onBeautyOptionSelected();
    }
}
