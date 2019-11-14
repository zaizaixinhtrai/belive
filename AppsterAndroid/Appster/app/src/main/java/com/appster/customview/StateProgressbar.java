package com.appster.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.appster.R;
import com.apster.common.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * Created by linh on 2/19/2017.
 */

public class StateProgressbar extends View {
//    private static final String TAG = StateProgressbar.class.getName();
    private final int DEFAULT_ALPHA_FADE_IN = 20;

//    private Drawable bgRoundabout;
    private Drawable icGem;
    private Drawable icHeadProgress;
    private Drawable icHeart;
    private Drawable icArrow;
    private Drawable icGlowBall;
    private Drawable bgGlowReachMax;
    private Drawable bgSparkle;
    private Paint gradientProgressPaint;
    private TextPaint numberIndicatorPaint;
    private Paint indicatorBorderPaint;
    private Paint progressBorderPaint;
    private Paint progressGlowPaint;
    private Paint rewardBoxPaint;
    private RectF maxProgressRecF;
    private RectF progressRecF;
    private Rect arrowRec;
    private Rect heartRec;
    private Rect numberIndicatorBound;
    private RectF indicatorBorderRec;
    private RectF progressBorderRec;
    private RectF progressGlowRecF;

    private List<Integer> levels;
    private int maxProgress; //
    private int progress;
    private int maxProgressHeight;
    private int minProgressHeight;
    private int progressPadding;
    private float progressRatio; //16.5f
    private int progressWidth;//26dp

    private int indicatorWidth;
    private int gemWidth;
    private int gemHeight;
    private int arrowWith;
    private int arrowHeight;
    private int arrowMargin;
    private int heartWidth;
    private int heartHeight;
    private int heartMargin;
    private int indicatorPadding;
    private int progressRadius;
    private int glowHeight;
    private int glowWidth;
    private int sparkleWidth;
    private int sparkleHeight;
    private int borderStroke;

    private int reachedLevelAtProgress;
    /** the remain point after reaching a level
     * egg. the level set at 500 point. but the progress has 520 point
     * the the {@link #reachedLevelAtProgress} = 20 point*/
    private int leftOverProgress;
    private boolean reachedMaxProgress;
    private boolean hasAnimationMaxProgress;
    /** start from 1 */
    private int animationLevelUnlockedAtLevel;
    private boolean isAnimating;
//    private int progressBackupDuringAnimation;

    private int numberIndicatorTextSize;

    private LevelReachCallback callback;
    private OnOpenLevelAnimation onOpenLevelAnimationCallback;

    //#region setters and getters ==================================================================
    public void addLevel(int value){
        levels.add(value);
        Collections.sort(levels, (o1, o2) -> {
            if (o1 < o2){
                return -1;
            }else if (o1.intValue() == o2.intValue()){
                return 0;
            }else {
                return 1;
            }
        });
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) throws Exception {
        if (isAnimating){
            //if during animation then backup the progress until animation's end
//            progressBackupDuringAnimation = progress;
//            leftOverProgress = 0;
            throw new Exception("can't set progress during animating");
        }

        int value;
        for (int i=0 ; i < levels.size(); i++){
            value = levels.get(i);
            if (reachedLevelAtProgress < value && progress >= value){
                reachedLevelAtProgress = value;
                isAnimating = true;
                leftOverProgress = progress - value;
                progress = value;
                if (i == levels.size() -1){
                    reachedMaxProgress = true;
                }
                break;
            }
        }
        this.progress = progress;
        invalidate();
    }

    public boolean isAnimating(){
        return isAnimating;
    }

    /**
     * set the level that was over
     * @param level start from 1 to n
     * @throws Exception whether input level is not found or the progress at that level is <= 0
     */
    public void setLevelUnlocked(int level) throws Exception {
        if (levels.get(level) < 1 || level > levels.size()){
            throw new Exception("Level not found");
        }
        reachedLevelAtProgress = levels.get(level-1) + 1;
    }

    public void setReachLevelCallback(LevelReachCallback callback) {
        this.callback = callback;
    }

    //#region constructors =========================================================================
    public StateProgressbar(Context context) {
        super(context);
        constructor(context, null, 0);
    }

    public StateProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructor(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StateProgressbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        constructor(context, attrs, defStyleAttr);
    }
    //#endregion constructor

    //#region inherited methods ====================================================================
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getImprovedDefaultWidth(widthMeasureSpec), getImprovedDefaultHeight(heightMeasureSpec));
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int left = getPaddingLeft() + indicatorWidth;
        int top = getPaddingTop() + minProgressHeight;
        int right = left + progressWidth;
        int bottom = getHeight() - getPaddingBottom() - progressPadding;

        progressBorderRec = new RectF(left, top, right, bottom);

        //gradient progress
        int startX = left + progressPadding;
        int startY = top + progressPadding;
        int endX = right - progressPadding;
        int endY = bottom - progressPadding;

        endY -= minProgressHeight;
        maxProgressRecF = new RectF(startX, startY, endX, endY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

//        bgRoundabout.draw(canvas);
        onDrawProgressBorder(canvas);

        onDrawLevelIndicators(canvas);

        //drawing progressbar
        onDrawProgress(canvas);

        onDrawProgressIndicator(canvas);

        if (reachedMaxProgress && !hasAnimationMaxProgress){
            animateMaxProgress(canvas);
        }
    }
    //#endregion inherited methods

    protected void constructor(Context context, AttributeSet attrs, int defStyleAttr) {

        maxProgress = 1000;
        progress = 0;
        progressRatio = 16.5f;
        progressPadding = dpToPx(3);
        progressWidth = dpToPx(23);
        arrowMargin = dpToPx(3);
        heartMargin = dpToPx(4);
        indicatorPadding = dpToPx(6);
        numberIndicatorTextSize = spToPx(12);
        borderStroke = dpToPx(2);

        levels = new ArrayList<>();
        progressRecF = new RectF();
        arrowRec = new Rect();
        heartRec = new Rect();
        numberIndicatorBound = new Rect();
        indicatorBorderRec = new RectF();
        progressBorderRec = new RectF();
        progressGlowRecF = new RectF();

        getAttrs(context, attrs, defStyleAttr);

        progressRadius = (int) ((float)progressWidth * 0.5f);
        maxProgressHeight = (int) (progressWidth * progressRatio);
        minProgressHeight = progressWidth - 2*progressPadding;

        icGem = getResources().getDrawable(R.drawable.ic_gem);
        icHeadProgress = getResources().getDrawable(R.drawable.voting_bar_head);
        icHeart = getResources().getDrawable(R.drawable.ic_heart);
        icArrow = getResources().getDrawable(R.drawable.ic_indicator_arrow);
        bgGlowReachMax = getResources().getDrawable(R.drawable.bg_glow);
        icGlowBall = getResources().getDrawable(R.drawable.ic_glow_ball);
        bgSparkle = getResources().getDrawable(R.drawable.bg_sparkle_small);

        bgGlowReachMax.setAlpha(DEFAULT_ALPHA_FADE_IN);
        icGlowBall.setAlpha(DEFAULT_ALPHA_FADE_IN);
        bgSparkle.setAlpha(DEFAULT_ALPHA_FADE_IN);

        //gem
        gemWidth = (int) (progressWidth * 0.4);
        float gemScale = (float) gemWidth / icGem.getIntrinsicWidth();
        gemHeight = (int) (gemScale * icGem.getIntrinsicHeight());

        //flow
        glowWidth = (int) (progressWidth * 2f);
        glowHeight = glowWidth;

        //arrow indicator
        arrowWith = (int) (progressWidth * 0.5);
        float arrowScale = (float) arrowWith / icArrow.getIntrinsicWidth();
        arrowHeight = (int) (arrowScale * icArrow.getIntrinsicHeight());

        //heart icon
        heartWidth = (int) (arrowWith * 1.5);
        float heartScale = (float)heartWidth / icHeart.getIntrinsicWidth();
        heartHeight = (int) (heartScale * icHeart.getIntrinsicHeight());

        //sparkle
        sparkleWidth = progressWidth*2;
        float sparkleScale = (float)sparkleWidth / bgSparkle.getIntrinsicWidth();
        sparkleHeight = (int) (sparkleScale * bgSparkle.getIntrinsicHeight());

        progressBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressBorderPaint.setStrokeWidth(borderStroke);

        rewardBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rewardBoxPaint.setStrokeWidth(borderStroke);

        gradientProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        numberIndicatorPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        numberIndicatorPaint.setColor(Color.WHITE);
        numberIndicatorPaint.setTextSize(numberIndicatorTextSize);

        indicatorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorBorderPaint.setStrokeWidth(borderStroke);

        progressGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressGlowPaint.setColor(Color.parseColor("#3CFFFFFF"));

        String maxProgressS = String.valueOf(maxProgress);
        numberIndicatorPaint.getTextBounds(maxProgressS, 0, maxProgressS.length(), numberIndicatorBound);
        indicatorWidth = arrowWith + 2*arrowMargin + heartWidth + 2*heartMargin
                + numberIndicatorBound.width() + 2*indicatorPadding;
        int maxViewWidth = glowWidth + 2*progressPadding + indicatorWidth;
        setMinimumDimension(maxViewWidth, maxProgressHeight + 2*minProgressHeight);

    }

    private void getAttrs(Context context, AttributeSet attrs, int defStyle){
        if (attrs == null){
            return;
        }

        TypedArray t = context.obtainStyledAttributes(attrs,
                R.styleable.StateProgressbar,
                defStyle, //if any values are in the theme
                0); //Do you have your own style group

        //Use the offset in the bag to get your value
        numberIndicatorTextSize = t.getDimensionPixelSize(R.styleable.StateProgressbar_indicatorTextSize, numberIndicatorTextSize);
        progressWidth = t.getDimensionPixelSize(R.styleable.StateProgressbar_progressThickness, progressWidth);
        progress = t.getInteger(R.styleable.StateProgressbar_progress, progress);
        maxProgress = t.getInteger(R.styleable.StateProgressbar_maxProgress, maxProgress);

        //Recycle the typed array
        t.recycle();
    }

    private void setMinimumDimension(int minWith, int minHeight){
        setMinimumWidth(minWith);
        setMinimumHeight(minHeight);
    }

    private void onDrawProgressBorder(Canvas canvas){
        int rad = progressRadius;
        progressBorderPaint.setStyle(Paint.Style.STROKE);
        progressBorderPaint.setColor(Color.parseColor("#4DFFFFFF"));
        canvas.drawRoundRect(progressBorderRec, rad, rad, progressBorderPaint);

        progressBorderPaint.setStyle(Paint.Style.FILL);
        progressBorderPaint.setColor(Color.parseColor("#66231f20"));
        canvas.drawRoundRect(progressBorderRec, rad, rad, progressBorderPaint);
    }

    private void onDrawLevelIndicators(final Canvas canvas){
        int gemCenterX = (int) (maxProgressRecF.left + maxProgressRecF.width()/2);
        int gemLeft = (int) (gemCenterX - gemWidth * 0.5);
        int gemRight = (int) (gemCenterX + gemWidth * 0.5);

        if (levels.size() < 0) {
            return;
        }

        for (int level=1; level <= levels.size(); level++){
            int value = levels.get(level-1);
            if (progress <= value || reachedLevelAtProgress > 0) {
                int gemTop = (int) (maxProgressHeight - ((float) value / maxProgress) * maxProgressHeight + minProgressHeight);
                int gemBottom = gemTop + gemHeight;

                int gemCenterY = (int) (gemBottom - (float) (gemHeight) / 2);
                int rewardBoxRad = (int) (maxProgressRecF.width()/2 + borderStroke);
                int rewardBoxTop = (int) (gemCenterY - progressBorderRec.width() / 2);

                if (rewardBoxTop < progressBorderRec.top) {
                    int delta = (int) (rewardBoxTop - progressBorderRec.top);
                    gemTop -= delta;
                    gemBottom -= delta;
                    gemCenterY -= delta;
                }

                //drawing gem icon and reward box for each level
                if (progress <= value) {
                    rewardBoxPaint.setStyle(Paint.Style.STROKE);
                    rewardBoxPaint.setColor(Color.parseColor("#4DFFFFFF"));
                    canvas.drawCircle(gemCenterX, gemCenterY, rewardBoxRad, rewardBoxPaint);

                    rewardBoxPaint.setStyle(Paint.Style.FILL);
                    rewardBoxPaint.setColor(Color.parseColor("#66231f20"));
                    canvas.drawCircle(gemCenterX, gemCenterY, rewardBoxRad, rewardBoxPaint);

                    icGem.setBounds(gemLeft, gemTop, gemRight, gemBottom);
                    icGem.draw(canvas);
                }


                //reaching level animation
                //a glow ball will be faded in 200ms
                if (reachedLevelAtProgress == value && !reachedMaxProgress) {
                    int halfWidth = (int) ((float) glowWidth / 2);
                    int halfHeight = (int) ((float) glowHeight / 2);
                    int glowBallLeft = gemCenterX - halfWidth;
                    int glowBallTop = gemCenterY - halfHeight;
                    int glowBallRight = gemCenterX + halfWidth;
                    int glowBallBottom = gemCenterY + halfHeight;
                    icGlowBall.setBounds(glowBallLeft, glowBallTop, glowBallRight, glowBallBottom);
                    icGlowBall.draw(canvas);
                    final int finalLevel = level;
                    postDelayed(() -> {
                        int alpha = (int) (icGlowBall.getAlpha() * 1.08f);
                        if(alpha > 255) {
                            alpha = DEFAULT_ALPHA_FADE_IN;
                            reachedLevelAtProgress++;
                            if (callback != null) {
                                callback.reachLevel(finalLevel, leftOverProgress);
                                leftOverProgress = 0;
                            }
                        }
                        icGlowBall.setAlpha(alpha);
                        invalidate();
                    }, 16);

                }

                if (animationLevelUnlockedAtLevel == level){
                    int halfWidth = (int) ((float) sparkleWidth / 2);
                    int halfHeight = (int) ((float) sparkleHeight / 2);
                    int sparkleLeft = gemCenterX - halfWidth;
                    int sparkleTop = gemCenterY - halfHeight;
                    int sparkleRight = gemCenterX + halfWidth;
                    int sparkleBottom = gemCenterY + halfHeight;
                    bgSparkle.setBounds(sparkleLeft, sparkleTop, sparkleRight, sparkleBottom);
                    bgSparkle.draw(canvas);
                    postDelayed(() -> {
                        int alpha = (int) (bgSparkle.getAlpha() * 1.1f);
                        if (alpha > 255){
                            alpha = DEFAULT_ALPHA_FADE_IN;
                            animationLevelUnlockedAtLevel = 0;
                            isAnimating = false;
                            if (onOpenLevelAnimationCallback != null){
                                onOpenLevelAnimationCallback.finish();
                                onOpenLevelAnimationCallback = null;
                            }
                        }
                        bgSparkle.setAlpha(alpha);
                        invalidate();
                    }, 16);
                }
            }
        }
    }

    private void onDrawProgressIndicator(Canvas canvas){
        //arrow
        int arrowTop = (int) (progressRecF.top - (float)arrowHeight/2);
        int arrowRight = (int) (progressRecF.left - progressPadding - arrowMargin);
        int arrowLeft = arrowRight - arrowWith;
        int arrowBottom = arrowTop + arrowHeight;
        arrowRec.set(arrowLeft, arrowTop, arrowRight, arrowBottom);
        icArrow.setBounds(arrowRec);
        icArrow.draw(canvas);

        //heart
        int arrowCenterY = (int) (arrowRec.top + (float)arrowRec.height() /2);
        int heartTop = (int) (arrowCenterY - (float)heartHeight /2);
        int heartRight = arrowLeft - arrowMargin - indicatorPadding;
        int heartLeft = heartRight - heartWidth;
        int heartBottom = heartTop + heartHeight;
        heartRec.set(heartLeft, heartTop, heartRight, heartBottom);
        icHeart.setBounds(heartRec);

        //number progress indicator
        String progressS = Utils.formatThousand(progress);
        numberIndicatorPaint.getTextBounds(progressS, 0, progressS.length(), numberIndicatorBound);
        int textTop = (int) (arrowCenterY + (float)numberIndicatorBound.height()/2);
        int textLeft = heartLeft - numberIndicatorBound.width() - heartMargin;

        // border
        int borderLeft = textLeft - indicatorPadding;
        int borderTop = heartTop - indicatorPadding;
        int borderRight = arrowLeft - arrowMargin;
        int borderBottom = heartBottom + indicatorPadding;
        int rad = dpToPx(4);

        indicatorBorderRec.set(borderLeft, borderTop, borderRight, borderBottom);
        indicatorBorderPaint.setStyle(Paint.Style.STROKE);
        indicatorBorderPaint.setColor(Color.parseColor("#4DFFFFFF"));
        canvas.drawRoundRect(indicatorBorderRec, rad, rad, indicatorBorderPaint);

        indicatorBorderPaint.setStyle(Paint.Style.FILL);
        indicatorBorderPaint.setColor(Color.parseColor("#66231f20"));
        canvas.drawRoundRect(indicatorBorderRec, rad, rad, indicatorBorderPaint);

        canvas.drawText(progressS, textLeft, textTop, numberIndicatorPaint);
        icHeart.draw(canvas);
    }

    private void onDrawProgress(Canvas canvas){
        //gradient progress
        int progressHeight = (int) (((float)progress / maxProgress) * maxProgressRecF.height());
        progressHeight = (progressHeight < maxProgressHeight)? progressHeight : maxProgressHeight - progressRadius;
        int right = (int) maxProgressRecF.right;
        int bottom = (int) maxProgressRecF.bottom + minProgressHeight;
        int left = (int) maxProgressRecF.left;
        int top = (int) maxProgressRecF.bottom - progressHeight;
        //progress
        LinearGradient shader = new LinearGradient(left, top, right, bottom, Color.parseColor("#F09AD7DD"), Color.parseColor("#F0ECC75E"), Shader.TileMode.CLAMP);
        gradientProgressPaint.setShader(shader);
        progressRecF.set(left, top, right, bottom);
        int rad = progressRadius;
        canvas.drawRoundRect(progressRecF, rad, rad, gradientProgressPaint);

        //glow
        int progressGlowLeft = (int) (left + progressRecF.width()/8);
        int progressGlowTop = (int) (top + (float)rad/2);
        int progressGlowRight = (int) (left + progressRecF.width()/2.4f);
        int progressGlowBottom = (int) (bottom - (float)rad/2);
        progressGlowRecF.set(progressGlowLeft, progressGlowTop, progressGlowRight, progressGlowBottom);
        canvas.drawRoundRect(progressGlowRecF, rad, rad, progressGlowPaint);

        //head
        int headEndY = (int) (top + maxProgressRecF.width());
        icHeadProgress.setBounds(left, top, right, headEndY);
        icHeadProgress.draw(canvas);
    }

    private void animateMaxProgress(Canvas canvas){
        bgGlowReachMax.setBounds(
                (int)maxProgressRecF.left - progressWidth - progressPadding,//width 120 - height 750
                (int)maxProgressRecF.top - progressWidth + progressPadding,
                (int)maxProgressRecF.left - progressPadding + 2*progressWidth,
                (int)maxProgressRecF.bottom + progressWidth + minProgressHeight);

//        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//        p.setColor(Color.BLUE);
//        canvas.drawRect((int)maxProgressRecF.left - progressWidth - progressPadding,
//                (int)maxProgressRecF.top - progressWidth + progressPadding,
//                (int)maxProgressRecF.left - progressPadding + 2*progressWidth,
//                (int)maxProgressRecF.bottom + progressWidth + minProgressHeight, p);

        bgGlowReachMax.draw(canvas);
        postDelayed(() -> {
            int alpha = (int) (bgGlowReachMax.getAlpha() * 1.08f);
            if (alpha > 255) {
                hasAnimationMaxProgress = true;
                alpha = DEFAULT_ALPHA_FADE_IN;
                isAnimating = false;
                if (callback != null) {
                    callback.reachLevel(levels.size(), leftOverProgress);
                    leftOverProgress = 0;
                }
            }
            bgGlowReachMax.setAlpha(alpha);
            invalidate();
        }, 16);


    }

//    public void restoreProgressBackup(){
//        int bk = 0;
//        if (progressBackupDuringAnimation > 0){
//            bk = progressBackupDuringAnimation;
//            progressBackupDuringAnimation = 0;
//
//        }else if (leftOverProgress > 0){
//            bk = this.progress + leftOverProgress;
//        }
//        Log.e(TAG, "restore "+ bk);
//        if (bk > 0) {
//            final int finalBk = bk;
//            postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    setProgress(finalBk);
//                }
//            }, 100);
//        }
//    }

    public void animateLevelUnlocked(int level, OnOpenLevelAnimation callback){
        isAnimating = true;
        animationLevelUnlockedAtLevel = level;
        this.onOpenLevelAnimationCallback = callback;
        invalidate();
    }

    public void resetProgress(){
        hasAnimationMaxProgress = false;
        reachedMaxProgress = false;
        isAnimating = false;
        animationLevelUnlockedAtLevel = 0;
        reachedLevelAtProgress = 0;
        leftOverProgress = 0;
        progress = 0;
        invalidate();
    }

    private boolean reachLevel(int progress){
        if (progress >= 0) {
            for (Integer level : levels) {
                if (level == progress) {
                    return true;
                }
            }
        }
        return false;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private int spToPx(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private int getImprovedDefaultHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            /**
             *  Documentation says that this mode is passed in when the layout wants to
             know what the true size is. True size could be as big as it could be; layout will likely then scroll it.
             With that thought, we have returned the maximum size for our view.
             */
            case MeasureSpec.UNSPECIFIED:
                return hGetMaximumHeight();

            /**
             *wrap_content:  The size that gets passed could be much larger, taking up the rest of the space. So it might
             say, “I have 411 pixels. Tell me your size that doesn’t exceed 411 pixels.” The question then to the
             programmer is: What should I return?
             */
            case MeasureSpec.AT_MOST:
                return this.getSuggestedMinimumHeight();

            /**
             *  match_parent: the size will be equal parent's size
             *  exact pixels: specified size which is set
             */
            case MeasureSpec.EXACTLY:
                return specSize;
        }
        //you shouldn't come here
        Timber.d("unknown specmode");
        return specSize;
    }

    private int getImprovedDefaultWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                return hGetMaximumWidth();

            /**
             * wrap_content:  The size that gets passed could be much larger, taking up the rest of the space. So it might
             say, “I have 411 pixels. Tell me your size that doesn’t exceed 411 pixels.” The question then to the
             programmer is: What should I return?
             */
            case MeasureSpec.AT_MOST:
                return this.getSuggestedMinimumWidth();

            /**
             *  match_parent: the size will be equal parent's size
             *  exact pixels: specified size which is set
             */
            case MeasureSpec.EXACTLY:
                return specSize;
        }
        //you shouldn't come here
        Timber.d("unknown specmode");
        return specSize;
    }

    private int hGetMaximumHeight() {
        return progressWidth + indicatorWidth;
    }

    private int hGetMaximumWidth() {
        return progressWidth + indicatorWidth;
    }


    //#region inner class ====================================
    public interface LevelReachCallback{
        /**
         * @param level start from 1 to n
         */
        void reachLevel(int level, int remain);
    }

    public interface OnOpenLevelAnimation{
        void finish();
    }
}
