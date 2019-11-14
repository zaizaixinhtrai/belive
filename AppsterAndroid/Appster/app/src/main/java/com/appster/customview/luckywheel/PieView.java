package com.appster.customview.luckywheel;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.collection.ArrayMap;

import com.appster.utility.RxUtils;
import com.appster.utility.glide.GlideApp;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static android.graphics.Bitmap.createScaledBitmap;
import static android.graphics.Typeface.create;
import static android.graphics.Typeface.createFromAsset;
import static com.apster.common.Utils.dpToPx;

/**
 * Created by thanhbc on 2/10/17.
 */

public class PieView extends View {
    private RectF mRangeRectF = new RectF();
    private int mDiameter;

    private Paint mArcPaint;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private Paint mTextPaintSecondText;

    private volatile float mStartAngle = 0;
    private int mCenter;
    private int mPadding;
    int mTargetIndex;
    private int mRoundOfNumber = 4;
    boolean isRunning = false;
    private double mSpeed = 0;
    private int defaultBackgroundColor = -1;
    private Drawable drawableCenterImage;
    private int textColor = 0xffffffff;

    private List<LuckyItem> mLuckyItemList;
    private ArrayMap<Integer, Bitmap> mBitMapList;

    PieRotateListener mPieRotateListener;
    private CompositeSubscription mCompositeSubscription;

    public interface PieRotateListener {
        void rotateDone(int index);
    }

    public PieView(Context context) {
        super(context);
    }

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setPieRotateListener(PieRotateListener listener) {
        this.mPieRotateListener = listener;
    }

    private void init() {
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        Typeface plain = createFromAsset(getContext().getAssets(), "fonts/opensanssemibold.ttf");
        Typeface boldText = create(plain, Typeface.NORMAL);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setDither(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setTypeface(boldText);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
                getResources().getDisplayMetrics()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextPaint.setLetterSpacing(0.15f);
        }

        mTextPaintSecondText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaintSecondText.setDither(true);

        mTextPaintSecondText.setColor(textColor);
        mTextPaintSecondText.setTypeface(boldText);
        mTextPaintSecondText.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                getResources().getDisplayMetrics()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextPaintSecondText.setLetterSpacing(0.15f);
        }
        mRangeRectF = new RectF(mPadding, mPadding, mPadding + mDiameter, mPadding + mDiameter);
    }

    int i = 0;
    boolean mIsLoadedImages = false;

    float mTempBitmapAngle = 0;

    public synchronized void setData(List<LuckyItem> luckyItemList) {
        this.mLuckyItemList = luckyItemList;
        mBitMapList = new ArrayMap<>();
        mCompositeSubscription.add(Observable.from(mLuckyItemList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .filter(luckyItem -> !TextUtils.isEmpty(luckyItem.iconUrl))
                .flatMap(luckyItem -> Observable.fromCallable(() -> GlideApp.with(getContext()).asBitmap().load(luckyItem.iconUrl).submit().get()))
                .map(bitmap -> getResizedBitmap(bitmap, dpToPx(24), dpToPx(24)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                            Timber.e("load completed -> %d", i);
                            mBitMapList.put(i, bitmap);
                            i++;
                        }, error -> Timber.e(error.getMessage())
                        , () -> {
                            mIsLoadedImages = true;
                            i = 0;
                            mTempBitmapAngle = 0;
                            invalidate();
                        }));
    }

    private float getAngle() {
        float sweepAngle = 360 / mLuckyItemList.size();
        return mTempBitmapAngle += sweepAngle;
    }

//    public void setDummyData(List<LuckyItem> luckyItemList) {
//        this.mLuckyItemList = luckyItemList;
//        mBitMapList = setupImage(luckyItemList);
//        invalidate();
//    }
//
//    private ArrayMap<Integer, Bitmap> setupImage(List<LuckyItem> luckyItemList) {
//        ArrayMap<Integer, Bitmap> arrayMap = new ArrayMap<>();
//        for (int i = 0; i < luckyItemList.size(); i++) {
//            arrayMap.put(i, BitmapFactory.decodeResource(getResources(), luckyItemList.get(i).icon));
//        }
//        mIsLoadedImages = true;
//        return arrayMap;
//    }

    Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }

    // call in order to free memory of Bitmap objects
    public void clear() {
        setRotation(0);
        if (mLuckyItemList != null) {
            mLuckyItemList.clear();
        }

        if(mBitMapList==null){
            return;
        }
        // get HashMap entry iterator
        for (Map.Entry<Integer, Bitmap> pair : mBitMapList.entrySet()) {
            // get entry pair
            // get Bitmap object
            Bitmap image = pair.getValue();
            // recycle if...
            if (image != null && !image.isRecycled()) {
                image.recycle();
                image = null;
            }
        }

        mBitMapList.clear();

    }

    public void setPieBackgroundColor(int color) {
        defaultBackgroundColor = color;
        invalidate();
    }

    public void setPieCenterImage(Drawable drawable) {
        drawableCenterImage = drawable;

        invalidate();
    }

    public void setPieTextColor(int color) {
        textColor = color;
        invalidate();
    }

    private void drawPieBackgroundWithBitmap(Canvas canvas, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, null, new Rect(mPadding / 2, mPadding / 2,
                getMeasuredWidth() - mPadding / 2, getMeasuredHeight() - mPadding / 2), null);
    }

    /**
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mLuckyItemList == null || mLuckyItemList.size()==0) {
            return;
        }
        init();

        drawBackgroundColor(canvas, defaultBackgroundColor);

        float startAngle = mStartAngle;
        float sweepAngle = 360 / mLuckyItemList.size();

        for (int i = 0; i < mLuckyItemList.size(); i++) {
            mArcPaint.setColor(mLuckyItemList.get(i).color);
            canvas.drawArc(mRangeRectF, startAngle, sweepAngle, true, mArcPaint);
            String[] s = mLuckyItemList.get(i).text.split(",");
            if (s.length == 2) {
                drawText(canvas, startAngle, sweepAngle, s[0]);
                drawSecondText(canvas, startAngle, sweepAngle, s[1]);
            } else {
                drawText(canvas, startAngle, sweepAngle, s[0]);
            }
            if (mIsLoadedImages && mBitMapList != null && mBitMapList.get(i) != null) drawImage(canvas, startAngle, mBitMapList.get(i));

            startAngle += sweepAngle;
        }

        drawCenterImage(canvas, drawableCenterImage);
    }

    private void drawBackgroundColor(Canvas canvas, int color) {
        if (color == -1)
            return;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(color);
        canvas.drawCircle(mCenter, mCenter, mCenter, mBackgroundPaint);
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());

        mPadding = getPaddingLeft() == 0 ? 10 : getPaddingLeft();
        mDiameter = width - mPadding * 2;

        mCenter = width / 2;

        setMeasuredDimension(width, width);
    }

    /**
     * @param canvas
     * @param startAngle
     * @param bitmap
     */
    private void drawImage(Canvas canvas, float startAngle, Bitmap bitmap) {
        int imgWidth = mDiameter / (mLuckyItemList.size() < 6 ? 6 : mLuckyItemList.size());

        float angle = (float) ((startAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);

        int x = (int) (mCenter + mDiameter / 4 * Math.cos(angle));
        int y = (int) (mCenter + mDiameter / 4 * Math.sin(angle));
//        Timber.e("x pos -> %d",x);
//        Timber.e("y pos -> %d",x);
//        Timber.e("left %d",x - imgWidth / 2);
//        Timber.e("top %d",y - imgWidth / 2);
//        Timber.e("right %d",x + imgWidth / 2);
//        Timber.e("bottom %d",y + imgWidth / 2);
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        canvas.drawBitmap(bitmap, null, rect, null);
    }

    private void drawCenterImage(Canvas canvas, Drawable drawable) {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable);
        Bitmap bitmap = LuckyWheelUtils.drawableToBitmap(drawable);
        bitmap = createScaledBitmap(bitmap, 90, 90, false);
        canvas.drawBitmap(bitmap, getMeasuredWidth() / 2 - bitmap.getWidth() / 2, getMeasuredHeight() / 2 - bitmap.getHeight() / 2, null);
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    private void drawText(Canvas canvas, float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();
        path.addArc(mRangeRectF, tmpAngle, sweepAngle);

        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mDiameter * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

        int vOffset = mDiameter / 2 / 8;

        canvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    private void drawSecondText(Canvas canvas, float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();
        path.addArc(mRangeRectF, tmpAngle, sweepAngle);

        float textWidth = mTextPaintSecondText.measureText(mStr);
        int hOffset = (int) (mDiameter * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

        int vOffset = mDiameter / 2 / 4;

        canvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaintSecondText);
    }

    /**
     * @param numberOfRound
     */
    public void setRound(int numberOfRound) {
        mRoundOfNumber = numberOfRound;
    }

    /**
     * @return
     */
    private float getAngleOfIndexTarget() {
        int tempIndex = mTargetIndex + 1;
        return (360 / mLuckyItemList.size()) * tempIndex;
    }
    /**
     * @param index
     */
    public void rotateTo(int index) {
        if (isRunning) {
            return;
        }
        mTargetIndex = index;
        if(mLuckyItemList==null || mLuckyItemList.size()==0) return;

        setRotation(0);
        float targetAngle = 360 * mRoundOfNumber + 270 - getAngleOfIndexTarget() + (360 / mLuckyItemList.size()) / 2;
        setRotation(0);
//        float angle = 360 / mLuckyItemList.size();
//        float angleOfTargetIndex = getAngleOfIndexTarget();
//        float from = 270 - angleOfTargetIndex;
//        float end = from + angle;
//        float targetFrom = mRoundOfNumber * 360 + from;
//        float targetEnd = mRoundOfNumber * 360 + end;
//        float targetAngle = targetFrom + angle / 2;
//        float v1 = (float) ((-1 + Math.sqrt(1 + mLuckyItemList.size() * targetFrom)) / 2);
//        float v2 = (float) ((-1 + Math.sqrt(1 + mLuckyItemList.size() * targetEnd)) / 2);
//
//        mSpeed = v1 + Math.random() * (v2 - v1);
        animate()
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(mRoundOfNumber * 1000 + 900L)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isRunning = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isRunning = false;
                        if (mPieRotateListener != null) {
                            mPieRotateListener.rotateDone(mTargetIndex);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                })
                .rotation(targetAngle)
                .start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }
}
