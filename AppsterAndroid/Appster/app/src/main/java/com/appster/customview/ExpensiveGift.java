package com.appster.customview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appster.R;
import com.appster.message.ChatItemModelClass;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.PixelUtil;
import com.apster.common.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by linh on 28/02/2017.
 */

public class ExpensiveGift extends LinearLayout {
    public static final String LUXURY_CAR = "17";
    public static final String SUPER_CAR = "8";
    public static final String MANSION = "14";
    public static final String DESIGNER_WATCH = "13";
    public static final String ROSE_BOUQUET = "12";
    public static final String DIAMOND_RING = "16";
    public static final String TEDDY_BEAR = "15";
    public static final String BURGER = "100";
    public static final String COFFEE = "101";
    public static final String CUP_CAKE = "102";
    public static final String LOVE = "103";
    public static final String HIGH_FIVE = "104";
    public static final String LOVE_BALLOON = "105";
    public static final String SNEAKERS = "106";
    public static final String GOOD_FORTUNE = "107";
    public static final String FIRE_WORKS = "108";
    public static final String SPORTS_CAR = "109";
    public static final String CNY_GIFT = "110";
    private static final String[] mExpensiveGifts = new String[]{ROSE_BOUQUET, TEDDY_BEAR, DESIGNER_WATCH, DIAMOND_RING, LUXURY_CAR, SUPER_CAR, MANSION,CNY_GIFT,
            BURGER,COFFEE,CUP_CAKE,LOVE,HIGH_FIVE, LOVE_BALLOON,SNEAKERS,GOOD_FORTUNE,FIRE_WORKS,SPORTS_CAR};
//    private static final List<String> mExpensiveList = Arrays.asList(LUXURY_CAR,SUPER_CAR,MANSION,DESIGNER_WATCH,DIAMOND_RING);
//    static final int IMG_DEFAULT_SIZE = Target.SIZE_ORIGINAL;
    private static final int GIFT_IMAGE_HEIGHT = Utils.dpToPx(200);
    private static final int GIFT_IMAGE_WIDTH = Utils.getScreenWidth();

    private static final int GIFT_COLOR_ONE = 0;
    private static final int GIFT_COLOR_TWO = 1;
    private static final int GIFT_COLOR_THREE = 2;

    public interface EndAnimation {
        void finish();
    }


    EndAnimation mEndDetachAnimationCallback;

    public ExpensiveGift(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpensiveGift(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEndDetachAnimationCallback(EndAnimation endDetachAnimationCallback) {
        mEndDetachAnimationCallback = endDetachAnimationCallback;
    }

    public void addGift(ChatItemModelClass itemModelClass) throws Exception {
        setVisibility(VISIBLE);
        BaseAnimation giftView = null;

        if (LUXURY_CAR.equals(itemModelClass.getGiftId())) {
            giftView = new DiagonalSlideAnimation(getContext(), this, itemModelClass, mEndDetachAnimationCallback);
            giftView.setGiftImage(getLuxuryCarDrawable(itemModelClass.giftColor));
        } else if (SUPER_CAR.equals(itemModelClass.getGiftId())) {
            giftView = new DiagonalSlideAnimation(getContext(), this, itemModelClass, mEndDetachAnimationCallback);
            giftView.setGiftImage(getSuperCarDrawable(itemModelClass.giftColor));

        } else if (DESIGNER_WATCH.equals(itemModelClass.getGiftId())) {
            giftView = new HorizontalSlideAnimation(getContext(), this, itemModelClass, mEndDetachAnimationCallback);
            giftView.setGiftImage(getWatchDrawable(itemModelClass.giftColor));
            this.setBackgroundColor(Color.TRANSPARENT);

        } else if (ROSE_BOUQUET.equals(itemModelClass.getGiftId())) {
            giftView = new HorizontalSlideAnimation(getContext(), this, itemModelClass, mEndDetachAnimationCallback);
            giftView.setGiftImage(getBouquetDrawable(itemModelClass.giftColor));
            this.setBackgroundColor(Color.TRANSPARENT);

        }else if(CNY_GIFT.equals(itemModelClass.getGiftId())){
            giftView = new HorizontalSlideAnimation(getContext(), this, itemModelClass, mEndDetachAnimationCallback);
            giftView.setGiftImage(R.drawable.img_cny_angbao);
            this.setBackgroundColor(Color.TRANSPARENT);
        }else if (TEDDY_BEAR.equals(itemModelClass.getGiftId())) {
            giftView = new HorizontalSlideAnimation(getContext(), this, itemModelClass, mEndDetachAnimationCallback);
            giftView.setGiftImage(getTeddyDrawable(itemModelClass.giftColor));
            this.setBackgroundColor(Color.TRANSPARENT);

        } else if (DIAMOND_RING.equals(itemModelClass.getGiftId())) {
            giftView = new SparkleHorizontalSlideAnimation(getContext(), this, itemModelClass, mEndDetachAnimationCallback);
            giftView.setGiftImage(getDiamondRingDrawable(itemModelClass.giftColor));
            giftView.setGiftImageGlow(getDiamondRingDrawableGlow(itemModelClass.giftColor));
            giftView.setGiftImageSparkle(R.drawable.img_gift_sparkle_big);
            this.setBackgroundColor(Color.TRANSPARENT);

        } else if (MANSION.equals(itemModelClass.getGiftId())) {
            giftView = new MansionAnimation(getContext(), this, itemModelClass, mEndDetachAnimationCallback);
            giftView.setGiftImage(getMansionDrawable(itemModelClass.giftColor));
            giftView.setGiftImageGlow(getMansionDrawableGlow(itemModelClass.giftColor));
            giftView.setGiftImageSparkle(R.drawable.img_gift_sparkle_big);
            giftView.setGiftAvatarSparkle(R.drawable.bg_sparkle_small);
            this.setPadding(0, 0, 0, 0);
        }

        if (giftView != null) {
            giftView.fetchResource();
        }
    }

    public static boolean checkExpensiveGiftByMessage(String giftId) {
        for (String gift : mExpensiveGifts) {
            if (gift.equals(giftId)) {
                return true;
            }
        }
        return false;
    }

    //#region inner classes ========================================================================

    private int getLuxuryCarDrawable(int giftColor) {

        switch (giftColor) {
            case GIFT_COLOR_ONE:
                return R.drawable.img_gift_luxury_car_black;
            case GIFT_COLOR_TWO:
                return R.drawable.img_gift_luxury_car_red;
            case GIFT_COLOR_THREE:
                return R.drawable.img_gift_luxury_car_purple;
            default:
                return R.drawable.img_gift_luxury_car_black;
        }
    }

    private int getSuperCarDrawable(int giftColor) {

        switch (giftColor) {
            case GIFT_COLOR_ONE:
                return R.drawable.img_super_car_yellow;
            case GIFT_COLOR_TWO:
                return R.drawable.img_super_car_red;
            case GIFT_COLOR_THREE:
                return R.drawable.img_super_car_purple;
            default:
                return R.drawable.img_super_car_yellow;
        }
    }

    private int getWatchDrawable(int giftColor) {

        switch (giftColor) {
            case GIFT_COLOR_ONE:
                return R.drawable.img_gift_designer_watch_silver_glow;
            case GIFT_COLOR_TWO:
                return R.drawable.img_gift_designer_watch_rose_glow;
            case GIFT_COLOR_THREE:
                return R.drawable.img_gift_designer_watch_black_glow;
            default:
                return R.drawable.img_gift_designer_watch_silver_glow;
        }
    }

    private int getBouquetDrawable(int giftColor) {

        switch (giftColor) {
            case GIFT_COLOR_ONE:
                return R.drawable.img_gift_bouquet_rose;
            case GIFT_COLOR_TWO:
                return R.drawable.img_gift_bouquet_purple;
            case GIFT_COLOR_THREE:
                return R.drawable.img_gift_bouquet_gold;
            default:
                return R.drawable.img_gift_bouquet_rose;
        }
    }

    private int getTeddyDrawable(int giftColor) {

        switch (giftColor) {
            case GIFT_COLOR_ONE:
                return R.drawable.img_gift_teddy_bear_brown;
            case GIFT_COLOR_TWO:
                return R.drawable.img_gift_teddy_bear_red;
            case GIFT_COLOR_THREE:
                return R.drawable.img_gift_teddy_bear_black;
            default:
                return R.drawable.img_gift_teddy_bear_brown;
        }
    }

    private int getMansionDrawable(int giftColor) {

        switch (giftColor) {
            case GIFT_COLOR_ONE:
                return R.drawable.img_gift_mansion_white;
            case GIFT_COLOR_TWO:
                return R.drawable.img_gift_mansion_red;
            case GIFT_COLOR_THREE:
                return R.drawable.img_gift_mansion_blue;
            default:
                return R.drawable.img_gift_mansion_white;
        }
    }

    private int getMansionDrawableGlow(int giftColor) {

        switch (giftColor) {
            case GIFT_COLOR_ONE:
                return R.drawable.img_gift_mansion_white_glow;
            case GIFT_COLOR_TWO:
                return R.drawable.img_gift_mansion_red_glow;
            case GIFT_COLOR_THREE:
                return R.drawable.img_gift_mansion_blue_glow;
            default:
                return R.drawable.img_gift_mansion_white_glow;
        }
    }

    private int getDiamondRingDrawable(int giftColor) {

        switch (giftColor) {
            case GIFT_COLOR_ONE:
                return R.drawable.img_gift_diamond_ring_white;
            case GIFT_COLOR_TWO:
                return R.drawable.img_gift_diamond_ring_purple;
            case GIFT_COLOR_THREE:
                return R.drawable.img_gift_diamond_ring_blue;
            default:
                return R.drawable.img_gift_diamond_ring_white;
        }
    }

    private int getDiamondRingDrawableGlow(int giftColor) {

        switch (giftColor) {
            case GIFT_COLOR_ONE:
                return R.drawable.img_gift_diamond_ring_white_glow;
            case GIFT_COLOR_TWO:
                return R.drawable.img_gift_diamond_ring_purple_glow;
            case GIFT_COLOR_THREE:
                return R.drawable.img_gift_diamond_ring_blue_glow;
            default:
                return R.drawable.img_gift_diamond_ring_white_glow;
        }
    }

    /**
     * used for mansion gift
     */
    static class MansionAnimation extends BaseAnimation {

        private static final int FADE_IN_DURATION = 1500;
        private static final int SPARKLE_SCALE_DURATION = 2000;

        @Bind(R.id.img_gift_avatar_sparkle)
        ImageView mImgAvatarSparkle;
        @Bind(R.id.img_gift_image_glow)
        ImageView mImgGiftImageGlow;
        @Bind(R.id.img_gift_image_sparkle)
        ImageView mImgGiftImageSparkle;

        boolean mGiftImageGlowLoaded = false;
        boolean mGiftImageSparkleLoaded = false;
        boolean mGiftAvatarSparkleLoaded = false;
        ObjectAnimator wholeViewAlpha;
        ObjectAnimator giftImageFadeIn;
        ObjectAnimator giftImageGlowFadeOut;
        ObjectAnimator giftSparkleScaleX;
        ObjectAnimator giftSparkleScaleY;
        ObjectAnimator avatarSparkleTranslateY;
        ObjectAnimator avatarSparkleScaleX;
        ObjectAnimator avatarSparkleScaleY;
        ObjectAnimator giftImageGlowAlpha;

        public MansionAnimation(Context context, ViewGroup parent, ChatItemModelClass itemModelClass, EndAnimation callback) {
            super(context, parent, itemModelClass, callback);


            wholeViewAlpha = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
            wholeViewAlpha.setDuration(FADE_IN_DURATION);

            giftImageFadeIn = ObjectAnimator.ofFloat(mImgGiftImageGlow, "alpha", 0f, 1f);
            giftImageFadeIn.setDuration(FADE_IN_DURATION);

            giftImageGlowFadeOut = ObjectAnimator.ofFloat(mImgGiftImageGlow, "alpha", 1f, 0f);
            giftImageGlowFadeOut.setDuration(500);

            giftSparkleScaleX = ObjectAnimator.ofFloat(mImgGiftImageSparkle, "scaleX", 0f, 1f);
            giftSparkleScaleX.setDuration(FADE_IN_DURATION);

            giftSparkleScaleY = ObjectAnimator.ofFloat(mImgGiftImageSparkle, "scaleY", 0f, 1f);
            giftSparkleScaleY.setDuration(FADE_IN_DURATION);
            giftSparkleScaleY.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mImgGiftImageSparkle != null) {
                        mImgGiftImageSparkle.setAlpha(0f);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            int y1 = PixelUtil.dpToPx(getContext(), 200);
            avatarSparkleTranslateY = ObjectAnimator.ofFloat(mImgAvatarSparkle, "translationY", y1, 0f);
            avatarSparkleTranslateY.setDuration(1500);

            avatarSparkleScaleX = ObjectAnimator.ofFloat(mImgAvatarSparkle, "scaleX", 0f, 1f);
            avatarSparkleScaleX.setDuration(SPARKLE_SCALE_DURATION);

            avatarSparkleScaleY = ObjectAnimator.ofFloat(mImgAvatarSparkle, "scaleY", 0f, 1f);
            avatarSparkleScaleY.setDuration(SPARKLE_SCALE_DURATION);

            giftImageGlowAlpha = ObjectAnimator.ofFloat(mImgGiftImageGlow, "alpha", 0f, 1f);
            giftImageGlowAlpha.setDuration(SPARKLE_SCALE_DURATION);
            giftImageGlowAlpha.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mImgGiftImageGlow != null && mImgAvatarSparkle != null) {
                        mImgGiftImageGlow.setAlpha(0f);
                        mImgAvatarSparkle.setAlpha(0f);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

        @Override
        protected int getResourceLayout() {
            return R.layout.view_expensive_gift_3;
        }

        @Override
        void animateAttaching(EndAnimation callback) {
            mAnimSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (callback != null) {
                        callback.finish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mAnimSet.play(wholeViewAlpha).with(giftImageFadeIn).with(giftSparkleScaleX).with(giftSparkleScaleY);
            mAnimSet.play(giftImageGlowFadeOut).after(wholeViewAlpha);
            mAnimSet.play(avatarSparkleTranslateY).with(avatarSparkleScaleX).with(avatarSparkleScaleY).with(giftImageGlowAlpha).after(giftImageGlowFadeOut).after(500);

            mAnimSet.start();
        }

        @Override
        void fetchResource() throws NoSuchFieldException {
            super.fetchResource();

            if (mGiftImageSparkle <= 0 || mGiftImageGlow <= 0 || mGiftAvatarSparkle <= 0) {
                throw new NoSuchFieldException("haven't set sparkle image yet!");
            }

            ImageLoaderUtil.displayUserImage(getContext(), mGiftImageGlow, mImgGiftImageGlow, ImageLoaderUtil.ScaleType.FIT_CENTER, false, GIFT_IMAGE_WIDTH, GIFT_IMAGE_HEIGHT, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    mGiftImageGlowLoaded = true;
                    Timber.e("-- mImgGiftImageGlow loaded onError");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    mGiftImageGlowLoaded = true;
                    Timber.e("-- mImgGiftImageGlow loaded");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }
            });

            ImageLoaderUtil.displayUserImage(getContext(), mGiftImageSparkle, mImgGiftImageSparkle, ImageLoaderUtil.ScaleType.FIT_CENTER, false, GIFT_IMAGE_WIDTH, GIFT_IMAGE_HEIGHT, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    mGiftImageSparkleLoaded = true;
                    Timber.e("-- mGiftImageSparkleLoaded loaded onError");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    mGiftImageSparkleLoaded = true;
                    Timber.e("-- mGiftImageSparkleLoaded loaded");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }
            });

            ImageLoaderUtil.displayUserImage(getContext(), mGiftAvatarSparkle, mImgAvatarSparkle, ImageLoaderUtil.ScaleType.FIT_CENTER, false, GIFT_IMAGE_WIDTH, GIFT_IMAGE_HEIGHT, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    mGiftAvatarSparkleLoaded = true;
                    Timber.e("-- mGiftAvatarSparkleLoaded loaded onError");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    mGiftAvatarSparkleLoaded = true;
                    Timber.e("-- mGiftAvatarSparkleLoaded loaded");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }
            });
        }

        @Override
        boolean resourcesLoaded() {
            return super.resourcesLoaded() && mGiftImageGlowLoaded && mGiftImageSparkleLoaded && mGiftAvatarSparkleLoaded;
        }
    }

    /**
     * used for Diamond Ring gift
     */
    static class SparkleHorizontalSlideAnimation extends BaseAnimation {

        @Bind(R.id.img_gift_image_glow_outer)
        ImageView mImgGiftImageGlowOuter;
        @Bind(R.id.img_gift_image_sparkle)
        ImageView mImgGiftImageSparkle;

        boolean mGiftImageGlowOuterLoaded = false;
        boolean mGiftImageSparkleLoaded = false;

        public SparkleHorizontalSlideAnimation(Context context, ViewGroup parent, ChatItemModelClass itemModelClass, EndAnimation callback) {
            super(context, parent, itemModelClass, callback);
        }

        @Override
        protected int getResourceLayout() {
            return R.layout.view_expensive_gift_2;
        }

        @Override
        protected void attachToParent(ViewGroup parent) {
            parent.addView(this);
        }

        @Override
        void animateAttaching(EndAnimation callback) {

            ObjectAnimator translateX = ObjectAnimator.ofFloat(this, "translationX", -mParent.getWidth(), 0f);
            translateX.setDuration(DURATION_SLIDE_ANIMATION);
//            translateX.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator alpha = ObjectAnimator.ofFloat(mImgGiftImageGlowOuter, "alpha", 0f, 1f);
            alpha.setDuration(DURATION_SLIDE_ANIMATION);
//            alpha.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mImgGiftImageSparkle, "scaleX", 0.1f, 1f);
            scaleX.setDuration(GIFT_SPARKLE_DURATION);
//            scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mImgGiftImageSparkle, "scaleY", 0.1f, 1f);
            scaleY.setDuration(GIFT_SPARKLE_DURATION);
//            scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

            mAnimSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (callback != null) {
                        callback.finish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mAnimSet.play(alpha).after(translateX).with(scaleX).with(scaleY);
            mAnimSet.start();
        }

        @Override
        void fetchResource() throws NoSuchFieldException {
            super.fetchResource();

            if (mGiftImageSparkle <= 0 || mGiftImageGlow <= 0) {
                throw new NoSuchFieldException("haven't set sparkle image yet!");
            }

            ImageLoaderUtil.displayUserImage(getContext(), mGiftImageGlow, mImgGiftImageGlowOuter, ImageLoaderUtil.ScaleType.FIT_CENTER, false, GIFT_IMAGE_WIDTH, GIFT_IMAGE_HEIGHT, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    mGiftImageGlowOuterLoaded = true;
                    Timber.e("-- mGiftImageGlowOuterLoaded loaded onError");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    mGiftImageGlowOuterLoaded = true;
                    Timber.e("-- mGiftImageGlowOuterLoaded loaded");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }
            });

            ImageLoaderUtil.displayUserImage(getContext(), mGiftImageSparkle, mImgGiftImageSparkle, ImageLoaderUtil.ScaleType.FIT_CENTER, false, GIFT_IMAGE_WIDTH, GIFT_IMAGE_HEIGHT, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    mGiftImageSparkleLoaded = true;
                    Timber.e("-- mGiftImageSparkleLoaded loaded onError");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    mGiftImageSparkleLoaded = true;
                    Timber.e("-- mGiftImageSparkleLoaded loaded");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }
            });
        }

        @Override
        boolean resourcesLoaded() {
            return super.resourcesLoaded() && mGiftImageGlowOuterLoaded && mGiftImageSparkleLoaded;
        }
    }

    /**
     * used for supper car, luxury car, designer watch
     */
    static class HorizontalSlideAnimation extends BaseAnimation {

        public HorizontalSlideAnimation(Context context, ViewGroup parent, ChatItemModelClass itemModelClass, EndAnimation callback) {
            super(context, parent, itemModelClass, callback);
        }

        @Override
        protected void attachToParent(ViewGroup parent) {
            parent.addView(this);
        }

        @Override
        void animateAttaching(EndAnimation callback) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationX", -mParent.getWidth(), 0f);
            objectAnimator.setDuration(DURATION_SLIDE_ANIMATION);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (callback != null) {
                        callback.finish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            objectAnimator.start();
        }
    }

    static class DiagonalSlideAnimation extends BaseAnimation {

        public DiagonalSlideAnimation(Context context, ViewGroup parent, ChatItemModelClass itemModelClass, EndAnimation callback) {
            super(context, parent, itemModelClass, callback);
        }

        @Override
        protected void attachToParent(ViewGroup parent) {
            parent.addView(this);
        }

        @Override
        void animateAttaching(EndAnimation callback) {
            ObjectAnimator translateX = ObjectAnimator.ofFloat(this, "translationX", -mParent.getWidth(), 0f);
            Timber.e("-- parent width " + mParent.getWidth());
            translateX.setDuration(DURATION_SLIDE_ANIMATION);

            float y = mParent.getWidth() * 0.4f;
            ObjectAnimator translateY = ObjectAnimator.ofFloat(this, "translationY", -y, 0f);
            translateY.setDuration(DURATION_SLIDE_ANIMATION);

            mAnimSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (callback != null) {
                        callback.finish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mAnimSet.playTogether(translateX, translateY);
            mAnimSet.start();
        }


    }

    static abstract class BaseAnimation extends RelativeLayout {
        final static int DURATION_SLIDE_ANIMATION = 800;
        final static int GIFT_DISPLAY_DURATION = 4000;
        final static int GIFT_SPARKLE_DURATION = 2000;

        View mRootView;
        ViewGroup mParent;
        @Bind(R.id.img_avatar)
        CircleImageView mImgAvatar;
        @Bind(R.id.txt_display_name)
        TextView mTxtDisplayName;
        @Bind(R.id.img_gift_image)
        ImageView mImgGiftImage;

        String mAvatarUrl;
        String mDisplayName;
        int mGiftImage;
        int mGiftImageGlow;
        int mGiftImageSparkle;
        int mGiftAvatarSparkle;

        boolean mAvatarLoaded = false;
        boolean mGiftImageLoaded = false;
        EndAnimation mEndDetachCallback;
        protected AnimatorSet mAnimSet;

        public void setAvatarUrl(String avatarUrl) {
            mAvatarUrl = avatarUrl;
        }

        public String getDisplayName() {
            return mDisplayName;
        }

        public void setDisplayName(String displayName) {
            mDisplayName = displayName;
        }

        public void setGiftImage(int giftImage) {
            mGiftImage = giftImage;
        }

        public void setGiftImageGlow(int giftImageGlow) {
            mGiftImageGlow = giftImageGlow;
        }

        public void setGiftImageSparkle(int giftImageSparkle) {
            mGiftImageSparkle = giftImageSparkle;
        }

        public void setGiftAvatarSparkle(int giftAvatarSparkle) {
            mGiftAvatarSparkle = giftAvatarSparkle;
        }

        public BaseAnimation(Context context, ViewGroup parent, ChatItemModelClass itemModelClass, EndAnimation callback) {
            super(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mRootView = inflater.inflate(getResourceLayout(), this, true);
            ButterKnife.bind(mRootView);

            mTxtDisplayName.setText(itemModelClass.getChatDisplayName());
            mAvatarUrl = itemModelClass.getProfilePic();
            mParent = parent;
            mEndDetachCallback = callback;

            mAnimSet = new AnimatorSet();
            mAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimSet.setupStartValues();
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            animateAttaching(() -> postDelayed(this::animateDetaching, GIFT_DISPLAY_DURATION));
        }


        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            mAnimSet.cancel();
            ButterKnife.unbind(mRootView);
            if (mEndDetachCallback != null) {
                mEndDetachCallback.finish();
                mEndDetachCallback = null;
            }
        }

        protected int getResourceLayout() {
            return R.layout.view_expensive_gift;
        }

        void fetchResource() throws NoSuchFieldException {

            if (mGiftImage <= 0) {
                throw new NoSuchFieldException("haven't set gift image yet!");
            }

            final int size = PixelUtil.dpToPx(getContext(), 60);
            //load avatar
            ImageLoaderUtil.displayUserImage(getContext(), mAvatarUrl, mImgAvatar, true, size, size, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    mAvatarLoaded = true;
                    Timber.e("-- mAvatarLoaded loaded onError");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    mAvatarLoaded = true;
                    Timber.e("-- mAvatarLoaded loaded");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }
            });

            ImageLoaderUtil.displayUserImage(getContext(), mGiftImage, mImgGiftImage, ImageLoaderUtil.ScaleType.FIT_CENTER, false, GIFT_IMAGE_WIDTH, GIFT_IMAGE_HEIGHT, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void onFailed(Exception e) {
                    mGiftImageLoaded = true;
                    Timber.e("-- mGiftImageLoaded loaded onError");
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    mGiftImageLoaded = true;
                    Timber.e("-- mGiftImageLoaded loaded");
                    if (resourcesLoaded()) {
                        attachToParent(mParent);
                    }
                }
            });
        }

        boolean resourcesLoaded() {
            return mAvatarLoaded && mGiftImageLoaded;
        }

        void animateDetaching() {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mParent, "alpha", 1f, 0.1f);
            objectAnimator.setDuration(DURATION_SLIDE_ANIMATION);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mParent.removeView(BaseAnimation.this);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            objectAnimator.start();
        }

        protected void attachToParent(ViewGroup parent) {
            parent.addView(this);
        }

        abstract void animateAttaching(EndAnimation callback);
    }
}
