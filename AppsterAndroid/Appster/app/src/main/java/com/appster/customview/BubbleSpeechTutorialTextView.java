package com.appster.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.appster.R;
import com.apster.common.Utils;

/**
 * Created by linh on 06/09/2017.
 */

public class BubbleSpeechTutorialTextView extends CustomFontTextView {
    private int mArrowWidth = Utils.dpToPx(15);
    private int mArrowHeight = Utils.dpToPx(10);
    private int mRoundRadius = Utils.dpToPx(4);
    private int mBgColor = Color.parseColor("#1DD2DB");
    private RectF mRoundedBgRecF;
    private Paint mRoundedRecPaint;
    private Paint mTrianglePaint;

    private boolean isVerticalArrow = true;
    private boolean mAlreadyReMeasured;
    private boolean mIsStandAloneView;//whether this view is stand alone or bubble view

    private View mAnchorView;
    private int mBubbleMarginTopBottom = Utils.dpToPx(5);
    private int mBubbleMarginLeftRight = Utils.dpToPx(10);


    public BubbleSpeechTutorialTextView(Context context) {
        super(context);
        constructor(context, null);
    }

    public BubbleSpeechTutorialTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        constructor(context, attrs);
    }

    public BubbleSpeechTutorialTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructor(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mAlreadyReMeasured || mIsStandAloneView) return;
        if (isVerticalArrow){
            onVerticalMeasure();
        }else{
            onHorizontalMeasure();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isVerticalArrow) {
            mRoundedBgRecF.set(0, 0, w, h - mArrowHeight);
        }else{
            mRoundedBgRecF.set(mArrowWidth, 0, w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawRoundedRectangleBg(canvas);
        drawTriangleBg(canvas);
        super.onDraw(canvas);
    }

    public void setAnchorView(View anchorView) {
        mAnchorView = anchorView;
    }

    public void setBubbleMarginTopBottom(int bubbleMarginTopBottom) {
        mBubbleMarginTopBottom = bubbleMarginTopBottom;
    }

    public void setBubbleMarginLeftRight(int bubbleMarginLeftRight) {
        mBubbleMarginLeftRight = bubbleMarginLeftRight;
    }

    public void setVerticalArrow(boolean verticalArrow) {
        isVerticalArrow = verticalArrow;
        mAlreadyReMeasured = false;
        resetPadding();
        swapArrowDimension();
    }

    private void constructor(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BubbleSpeechTutorialTextView);
        mIsStandAloneView = a.getBoolean(R.styleable.BubbleSpeechTutorialTextView_isStandAloneView, false);
        a.recycle();

        mRoundedBgRecF = new RectF();
        mRoundedRecPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRoundedRecPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mRoundedRecPaint.setColor(mBgColor);
        mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTrianglePaint.setColor(mBgColor);
        setCustomFont(getContext(), getContext().getString(R.string.font_HelveticaNeueRegular));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        setGravity(Gravity.CENTER);
        int padding = Utils.dpToPx(15);
        int paddingBottom = Utils.dpToPx(12);
        setPadding(padding, padding, padding, paddingBottom);
        resetPadding();
        swapArrowDimension();
    }

    private void resetPadding(){
        int paddingStart = (isVerticalArrow) ? getPaddingStart() : getPaddingStart() + mArrowWidth;
        int paddingTop = getPaddingTop();
        int paddingEnd = getPaddingEnd();
        int paddingBottom = (isVerticalArrow) ? getPaddingBottom() + mArrowHeight : getPaddingBottom();
        setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
    }

    private void swapArrowDimension(){
        if (!isVerticalArrow){
            mArrowWidth += mArrowHeight;
            mArrowHeight = mArrowWidth - mArrowHeight;
            mArrowWidth = mArrowWidth - mArrowHeight;
        }
    }

    private void onVerticalMeasure(){
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        FrameLayout.LayoutParams lps = (FrameLayout.LayoutParams) getLayoutParams();
        int[] anchorLocation = new int[2];
        mAnchorView.getLocationInWindow(anchorLocation);

        //x-cord
        int bubbleCenterX = (int) (lps.leftMargin + measuredWidth * 0.5f);
        int anchorCenterX = (int) (anchorLocation[0] + mAnchorView.getWidth() * 0.5f);
        if (bubbleCenterX != anchorCenterX){
            lps.leftMargin += anchorCenterX - bubbleCenterX;
        }
        if (lps.leftMargin < mBubbleMarginLeftRight) lps.leftMargin = mBubbleMarginLeftRight;
        if (lps.leftMargin + measuredWidth + mBubbleMarginLeftRight > Utils.getScreenWidth()){
            lps.leftMargin = Utils.getScreenWidth() - measuredWidth - mBubbleMarginLeftRight;
        }


        //y-cord
        lps.topMargin = anchorLocation[1] - measuredHeight - mBubbleMarginTopBottom;
        if (lps.topMargin < mBubbleMarginTopBottom){
            lps.topMargin = mBubbleMarginTopBottom;
        }
        if (lps.topMargin > Utils.getScreenHeight() - mBubbleMarginTopBottom){
            lps.topMargin = Utils.getScreenHeight() - mBubbleMarginTopBottom;
        }

        mAlreadyReMeasured = true;
        requestLayout();
    }

    private void onHorizontalMeasure(){
        int measuredHeight = getMeasuredHeight();
        FrameLayout.LayoutParams lps = (FrameLayout.LayoutParams) getLayoutParams();
        int[] anchorLocation = new int[2];
        mAnchorView.getLocationInWindow(anchorLocation);

        //x-cord
        lps.leftMargin = anchorLocation[0] + mAnchorView.getWidth() + mBubbleMarginLeftRight;

        //y-cord
        int bubbleCenterY = (int) (lps.topMargin + measuredHeight * 0.5f);
        int anchorCenterY = (int) (anchorLocation[1] + mAnchorView.getWidth() * 0.5f);
        if (bubbleCenterY != anchorCenterY){
            lps.topMargin += anchorCenterY - bubbleCenterY;
        }
        if (lps.topMargin < mBubbleMarginTopBottom) lps.topMargin = mBubbleMarginTopBottom;
        if (lps.topMargin + measuredHeight + mBubbleMarginTopBottom> Utils.getScreenHeight()) lps.topMargin = Utils.getScreenHeight() - measuredHeight - mBubbleMarginTopBottom;

        mAlreadyReMeasured = true;
        requestLayout();
    }

    private void drawRoundedRectangleBg(Canvas canvas){
        canvas.drawRoundRect(mRoundedBgRecF, mRoundRadius, mRoundRadius, mRoundedRecPaint);
    }

    private void drawTriangleBg(Canvas canvas){
        PointF arrowPeakPoint = getArrowPeakPoint();
        PointF a, b , c;
        if (isVerticalArrow) {
            a = new PointF(arrowPeakPoint.x, mRoundedBgRecF.bottom + mArrowHeight);
            b = new PointF(arrowPeakPoint.x - mArrowWidth * 0.5f, mRoundedBgRecF.bottom);
            c = new PointF(arrowPeakPoint.x + mArrowWidth * 0.5f, mRoundedBgRecF.bottom);

        }else{
            a = new PointF(0, arrowPeakPoint.y);
            b = new PointF(mRoundedBgRecF.left, arrowPeakPoint.y - mArrowHeight * 0.5f);
            c = new PointF(mRoundedBgRecF.left, arrowPeakPoint.y + mArrowHeight * 0.5f);
        }

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.close();

        canvas.drawPath(path, mTrianglePaint);
    }

    private PointF getArrowPeakPoint(){
        if (mAnchorView == null){
            if (isVerticalArrow){
                return new PointF(mRoundedBgRecF.centerX(), mRoundedBgRecF.bottom);
            }else {
                return new PointF(0, mRoundedBgRecF.centerY());
            }
        }

        int[] anchorLocation = new int[2];
        mAnchorView.getLocationInWindow(anchorLocation);
        int[] location = new int[2];
        getLocationInWindow(location);
        if (isVerticalArrow) {
            float x = anchorLocation[0] - location[0] + mAnchorView.getWidth() * 0.5f;
            float y = anchorLocation[1] - location[1];
            if (y > getBottom()) y = getBottom();
            return new PointF(x, y);
        }else{
            float x = anchorLocation[0] - location[0] + mAnchorView.getWidth();
            float y = anchorLocation[1] - location[1] + mAnchorView.getHeight() * 0.5f;
            if (x < getLeft()) x = getLeft();
            return new PointF(x, y);
        }
    }

}
