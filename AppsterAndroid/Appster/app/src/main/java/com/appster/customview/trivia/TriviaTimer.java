package com.appster.customview.trivia;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.appster.R;
import com.apster.common.Utils;

import static com.appster.customview.CustomFontUtils.selectTypeface;

/**
 * Created by thanhbc on 2/21/18.
 */

public class TriviaTimer extends View {
    private Paint paint;
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectF;

    // The point from where the color-fill animation will start.
    private static final int startingPointInDegrees = 270; // 12 o'clock
    // The point up-till which user wants the circle to be pre-filled.
    private float degreesUpTillPreFill = 0;
    private int mViewWidth;
    private int mViewHeight;

    private int radius = Utils.dpToPx(50);
    private String mText="";
    public TriviaTimer(Context context, AttributeSet attrs) {
        super(context, attrs);

        int strokeWidth = (int) Utils.dpToPx(3);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);

        // Define the size of the circle
        rectF = new RectF(strokeWidth, strokeWidth,
                Utils.dpToPx(35) + strokeWidth,
                Utils.dpToPx(35) + strokeWidth);
        textPaint.setTextSize(Utils.spToPx(21)); //Math.min(mViewWidth, mViewHeight) / 2f
        textPaint.setTextAlign(Paint.Align.CENTER);
//        paint.setStrokeWidth(0);
        textPaint.setColor(Color.parseColor("#68dbe0"));
        Typeface font = selectTypeface(context,context.getString(R.string.font_helveticaneuebold));
        Typeface bold = Typeface.create(font, Typeface.NORMAL);
        textPaint.setTypeface(bold);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Grey Circle - This circle will be there by default.
        paint.setColor(Color.parseColor("#6c9bff"));
        canvas.drawCircle(rectF.centerX(), rectF.centerY(),
                radius, paint);

        // Green Arc (Arc with max 360 angle) - This circle will be created as
        // time progresses.
        paint.setColor(Color.parseColor("#68dbe0"));
        canvas.drawArc(rectF, startingPointInDegrees, degreesUpTillPreFill,
                false, paint);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        mViewWidth = getWidth();
        mViewHeight = getHeight();


        // Center text
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;

        canvas.drawText(mText, xPos, yPos, textPaint);
    }

    public float getDegreesUpTillPreFill() {
        return degreesUpTillPreFill;
    }

    public void setDegreesUpTillPreFill(float degreesUpTillPreFill) {
        this.degreesUpTillPreFill = degreesUpTillPreFill;
    }

    public void setText(String text) {
        mText = text;
    }

}