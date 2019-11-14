package com.appster.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.FragmentManager;

import com.appster.R;
import com.appster.features.stream.ComposeWaterMarkText;
import com.appster.models.StreamTitleSticker;
import com.apster.common.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by linh on 25/05/2017.
 */

public class StickyPadFrameLayout extends FrameLayout {
    final static String TAG = "StickyPadFrameLayout";
    final static int MAX_LIVE_TITLE_TEXT_SIZE = Utils.spToPx(21);
    final static int MIN_LIVE_TITLE_TEXT_SIZE = Utils.spToPx(12);

    @Bind(R.id.txt_live_title)
    TextView mTxtLiveTitle;

    String mLiveTitle;
    int mLiveTitleColor = Color.WHITE;

    TextPaint mLiveTitleTextPaint;

    OnStickyPositionChangedListener mOnStickyPositionChangedListener;

    boolean mLiveTitleDraggable;
    boolean mFirstTimeEdit = true;
    Point mLiveTitlePosition;

    private FragmentManager mFragmentManager;

    public StickyPadFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public StickyPadFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StickyPadFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StickyPadFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    public void setOnStickyPositionChangedListener(OnStickyPositionChangedListener onStickyPositionChangedListener) {
        mOnStickyPositionChangedListener = onStickyPositionChangedListener;
        mLiveTitleDraggable = true;
    }

    private void init(){
        View root = LayoutInflater.from(getContext()).inflate(R.layout.frame_layout_sticky_pad, this, true);
        ButterKnife.bind(this, root);
        setupDragAndDropArea();
        mLiveTitleTextPaint = mTxtLiveTitle.getPaint();
        mLiveTitlePosition = new Point();
    }

    private void setupDragAndDropArea(){
        mTxtLiveTitle.setOnTouchListener(new View.OnTouchListener() {
            FrameLayout.LayoutParams lParams =(FrameLayout.LayoutParams) mTxtLiveTitle.getLayoutParams();
            private int deltaX;
            private int deltaY;
            private long downTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!mLiveTitleDraggable) {
                    return false;
                }

                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        deltaX = x - lParams.leftMargin;
                        deltaY = y - lParams.topMargin;
                        downTime = System.currentTimeMillis();
                        Log.d(TAG, "MotionEvent.ACTION_DOWN");
                        Log.d(TAG, "x_cord " + X);
                        Log.d(TAG, "y_cord " + Y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int marginX = x - deltaX;
                        int marginY = y - deltaY;
                        onLiveTitleDrag(v, marginX, marginY);
                        Log.d(TAG, "MotionEvent.ACTION_MOVE");
                        Log.d(TAG, "x_cord " + X);
                        Log.d(TAG, "y_cord " + Y);
                        Log.d(TAG, "marginX " + marginX);
                        Log.d(TAG, "marginY " + marginY);
                        break;

                    case MotionEvent.ACTION_UP:
                        long upTime = System.currentTimeMillis();
                        if (upTime - downTime < 200){
                            onStreamTitleClicked();
                        }else {
                            onLiveTitleDrop(v);
                        }
                        break;
                }
                return true;

            }
        });
    }

    void onStreamTitleClicked(){
        showLiveTitleEditorDialog();
    }

    void onLiveTitleDrag(View draggedView, int newX, int newY){
        Timber.d("onLiveTitleDrag x %d", newX);
        Timber.d("onLiveTitleDrag y %d", newY);
        changePosition(draggedView, newX, newY);
    }

    void onLiveTitleDrop(View draggedView){
        int parentWidthExcludePadding = getParentWidthExcludePadding();
        int parentHeightExcludePadding = getParentHeightExcludePadding();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) draggedView.getLayoutParams();
//        draggedView.setBackgroundResource(android.R.color.transparent);
        if (mOnStickyPositionChangedListener != null){
            float percentX = (float)layoutParams.leftMargin / parentWidthExcludePadding;
            float percentY = (float)layoutParams.topMargin / parentHeightExcludePadding;
            mOnStickyPositionChangedListener.onDrop(mTxtLiveTitle.getText().toString(), percentX, percentY, mLiveTitleColor);
        }
    }

    public void showLiveTitleEditorDialog(){
        if (mFragmentManager == null){
            return;
        }
        ComposeWaterMarkText dialog = ComposeWaterMarkText.newInstance(mLiveTitle, mLiveTitleColor);
        dialog.setListener(new ComposeWaterMarkText.OnComposeWaterMarkTextListener() {
            @Override
            public void onOk(String str, int color) {
                int parentWidthExcludePadding = getParentWidthExcludePadding();
                int parentHeightExcludePadding = getParentHeightExcludePadding();
                int top, left;
                if (mFirstTimeEdit){
                    //make the title central in the screen
                    //lay the title at the center
                    Rect bound = new Rect();
                    mLiveTitleTextPaint.setTextSize(MAX_LIVE_TITLE_TEXT_SIZE);
                    mLiveTitleTextPaint.getTextBounds(str, 0, str.length(), bound);
                    left = (int) (parentWidthExcludePadding * 0.5f - bound.width() * 0.5f);
                    top = (int) (parentHeightExcludePadding * 0.5f - bound.height() * 0.5f);
                    mFirstTimeEdit = false;
                }else{
                    FrameLayout.LayoutParams layoutParams = (LayoutParams) mTxtLiveTitle.getLayoutParams();
                    top = layoutParams.topMargin;
                    left = layoutParams.leftMargin;
                }


                mLiveTitlePosition = onLiveTitleReceived(str, left, top, color);
                if (mOnStickyPositionChangedListener != null){
                    float percentX = (float)mLiveTitlePosition.x / parentWidthExcludePadding;
                    float percentY = (float)mLiveTitlePosition.y / parentHeightExcludePadding;
                    mOnStickyPositionChangedListener.onDrop(mTxtLiveTitle.getText().toString(), percentX, percentY, color);
                }
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show(mFragmentManager, ComposeWaterMarkText.class.getName());
    }

    public void onLiveTitleReceived(@Nullable StreamTitleSticker streamTitleSticker){
        if(streamTitleSticker==null) return;
        onLiveTitleReceived(streamTitleSticker.mStreamTitleStickerContent, streamTitleSticker.mStreamTitleStickerX,
                streamTitleSticker.mStreamTitleStickerY, streamTitleSticker.mStreamTitleColorCode);
        mFirstTimeEdit = false;
    }

    public Point onLiveTitleReceived(String title, float percentX, float percentY, String color){
        int iColor;
        try {
            iColor = Color.parseColor(color);
        }catch ( Exception e){
            iColor = getResources().getColor(R.color.stream_title_color_default);
            Timber.e(e);
        }
        return onLiveTitleReceived(title, percentX, percentY, iColor);
    }

    public Point onLiveTitleReceived(String title, float percentX, float percentY, int color){
        int parentWidthExcludePadding = getParentWidthExcludePadding();
        int parentHeightExcludePadding = getParentHeightExcludePadding();
        int left = (int) (percentX * parentWidthExcludePadding);
        int top = (int) (percentY * parentHeightExcludePadding);
        return onLiveTitleReceived(title, left, top, color);
    }

    Point onLiveTitleReceived(String title, int left, int top, int color){
        int parentWidthExcludePadding = getParentWidthExcludePadding();
        Rect bound = new Rect();
        mLiveTitleTextPaint = mTxtLiveTitle.getPaint();
        mLiveTitleTextPaint.setTextSize(MAX_LIVE_TITLE_TEXT_SIZE);
        mLiveTitleTextPaint.getTextBounds(title, 0, title.length(), bound);

        int bestTextSize = MAX_LIVE_TITLE_TEXT_SIZE;
        while (bound.width() > parentWidthExcludePadding){
            bestTextSize--;
            mLiveTitleTextPaint.setTextSize(bestTextSize);
            mLiveTitleTextPaint.getTextBounds(title, 0, title.length(), bound);
            if (bestTextSize == MIN_LIVE_TITLE_TEXT_SIZE){
                break;
            }
        }
        Timber.d("live title text size %d", bestTextSize);

        if (left + bound.width() > parentWidthExcludePadding){
            left = parentWidthExcludePadding - bound.width();
        }

        Point position = changePosition(mTxtLiveTitle, left, top);
        mTxtLiveTitle.setText(title);
        mTxtLiveTitle.setTextColor(color);
        mLiveTitle = title;
        mLiveTitleColor = color;
        return position;
    }

    private Point changePosition(View view, int marginLeft, int marginTop){
        int width = view.getWidth();
        int height = view.getHeight();
        int parentWidthExcludePadding = getWidth() - getPaddingLeft() - getPaddingRight();
        int parentHeightExcludePadding = getHeight() - getPaddingTop() - getPaddingBottom();
        int newMarginX = marginLeft;
        int newMarginY = marginTop;
        if (newMarginX < 0){
            newMarginX = 0;
        }
        if (newMarginX + width > parentWidthExcludePadding){
            newMarginX = parentWidthExcludePadding - width;
        }
        if (newMarginY < 0){
            newMarginY = 0;
        }
        if (newMarginY + height > parentHeightExcludePadding){
            newMarginY = parentHeightExcludePadding - height;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.setMargins(newMarginX, newMarginY, 0, 0);
        view.requestLayout();
        return new Point(newMarginX, newMarginY);
    }

    int getParentWidthExcludePadding(){
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    int getParentHeightExcludePadding(){
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public interface OnStickyPositionChangedListener {
        /**
         * @param percentX percent left margin compared to parent's width
         * @param percentY percent top margin compared to parent's width
         * @param color
         */
        void onDrop(String text, float percentX, float percentY, int color);
    }
}
