package com.appster.features.home.dialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.dialog.NoTitleDialogFragment;
import com.appster.models.event_bus_models.EventBusRefreshHomeTab;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.RxUtils;
import com.appster.utility.SocialManager;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.domain.models.TreatCollectModel;
import com.pack.utility.BitmapUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.appster.features.home.dialog.DailyTreatRevealPrizeDialog.DailyTreatType.TYPE_BIG;
import static com.appster.features.home.dialog.DailyTreatRevealPrizeDialog.DailyTreatType.TYPE_MINI;
import static com.appster.features.home.dialog.DailyTreatRevealPrizeDialog.DailyTreatType.TYPE_REVIVE;
import static com.appster.features.home.dialog.DailyTreatRevealPrizeDialog.DailyTreatType.TYPE_REVIVE_BIG_PACKAGE;
import static com.appster.features.home.dialog.DailyTreatRevealPrizeDialog.DailyTreatType.TYPE_ULTIMATE;

/**
 * Created by linh on 07/11/2017.
 */

public class DailyTreatRevealPrizeDialog extends NoTitleDialogFragment implements SocialManager.SocialSharingListener, DailyTreatRevealPrizeContract.View, ImageLoaderUtil.ImageLoaderCallback {
    private static final String SHARE_URL = "http://get.belive.sg/rewards";
    private static final String PRIZE_SCREEN_SHOT_FILE = "prize_screen.png";
    private static final String DAILY_TREAT_TYPE = "DAILY_TREAT_TYPE";
    private static final int GLOW_ANIMATION_DURATION = 3600;
    private static final int SPARKLE_SCALE_UP_DURATION = 1500;
    private static final int SPARKLE_FADE_IN_DURATION = 1000;
    private static final int SPARKLE_FADE_OUT_DURATION = 500;
    private static final int SPARKLE_DELAYED_TIME = 1000;

    //    @Bind(R.id.btn_claim)
//    CustomFontButton mBtnClaim;
    @Bind(R.id.img_glow)
    ImageView mImgGlow;
    @Bind(R.id.img_sparkle)
    ImageView mImgSparkle;
    @Bind(R.id.img_gift)
    ImageView mImgPrize;
    @Bind(R.id.txt_gift_count)
    CustomFontTextView mTxtPrizeCount;
    @Bind(R.id.txt_gift_name)
    CustomFontTextView mTxtPrizeName;
    //    @Bind(R.id.txt_comeback_message)
//    CustomFontTextView mTxtComebackMessage;
    @Bind(R.id.txt_daily_treat_message)
    CustomFontTextView mTxtRevealPrizeMessage;
    @Bind(R.id.fmImageGift)
    FrameLayout fmImageGift;

    private String mShareContent;
    private String mShareTitle;

    private TreatCollectModel mDailyTreat;

    ObjectAnimator mShowGlowAnimator;
    ObjectAnimator sparkleScaleXAnimator;
    ObjectAnimator sparkleScaleYAnimator;
    ObjectAnimator sparkleFadeInAnimator;
    ObjectAnimator sparkleFadeOutAnimator;
//    AnimatorSet mAnimatorSet;

    CompositeSubscription mCompositeSubscription;
    Bitmap mScreenshotBitmap;

    DailyTreatRevealPrizeContract.UserActions mPresenter;
    private PublishSubject<Long> mIntervalSubject = PublishSubject.create();
    private DaylyTreatPrizeListner daylyTreatPrizeListner;

    public static DailyTreatRevealPrizeDialog newInstance(TreatCollectModel dailyTreatType) {

        Bundle args = new Bundle();
        args.putParcelable(DAILY_TREAT_TYPE, dailyTreatType);
        DailyTreatRevealPrizeDialog fragment = new DailyTreatRevealPrizeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    //======= lifecycle ============================================================================
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            //noinspection WrongConstant
            mDailyTreat = b.getParcelable(DAILY_TREAT_TYPE);
        }
        mPresenter = new DailyTreatRevealPrizePresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setCancelable(false);
        initAnimators();
        bindView();
//        mPresenter.getAppConfig();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_SHARE_FEED) {
            Toast.makeText(getContext(), "shared", Toast.LENGTH_SHORT).show();
            Timber.d("shared");
            File file = new File(PRIZE_SCREEN_SHOT_FILE);
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (mAnimatorSet != null){
//            mAnimatorSet.removeAllListeners();
//            mAnimatorSet.cancel();
//        }
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        mIntervalSubject.onNext(-1L);
    }

    @Override
    protected int getRootLayoutResource() {
        return R.layout.dialog_daily_treat_reveal_prize;
    }

    @Override
    protected boolean isDimDialog() {
        return true;
    }

    @Override
    protected float dimAmount() {
        return 0.9f;
    }

    //======= event handlers =======================================================================
    @OnClick(R.id.btn_share_belive)
    void onShareBeliveButtonClicked() {

    }

    @OnClick(R.id.btn_share_facebook)
    void onShareFacebookButtonClicked() {
        mCompositeSubscription.add(Observable.fromCallable(() -> BitmapUtil.screenShot(getView(), ContextCompat.getColor(getContext(), R.color.dim_background_dialog_90)))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    SocialManager.getInstance().shareFacebookPopup(getContext(), this, CommonDefine.TYPE_IMAGE, Uri.parse(SHARE_URL), mShareContent, bitmap);
                    mScreenshotBitmap = bitmap;
                }));
    }

    @OnClick(R.id.btn_share_instagram)
    void onShareInstagramButtonClicked() {
        mCompositeSubscription.add(Observable.fromCallable(() -> {
            Bitmap mShareBitmap = BitmapUtil.screenShot(getView(), ContextCompat.getColor(getContext(), R.color.dim_background_dialog_90));
            Timber.d("screenshot has been taken");
            Uri uri = BitmapUtil.storeAndGetUri(mShareBitmap, PRIZE_SCREEN_SHOT_FILE);
            mShareBitmap.recycle();
            return uri;
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uri -> SocialManager.getInstance().shareFeedToInstagram(getContext(), CommonDefine.TYPE_IMAGE, uri), Timber::e));


    }

    @OnClick(R.id.btn_share_whatsapp)
    void onShareWhatsappButtonClicked() {
        SocialManager.getInstance().shareQuotesToWhatsapp(getContext(), mShareTitle, SHARE_URL, mShareContent);
    }

    @OnClick(R.id.btn_share_twitter)
    void onShareTwitterButtonClicked() {
        SocialManager.getInstance().ShareFeedQuotesToTwitter(getContext(), mShareContent, SHARE_URL);
    }

    @OnClick(R.id.btn_share_mail)
    void OnShareMailButtonClicked() {
        SocialManager.getInstance().shareURLToEmail(getContext(), mShareContent, mShareTitle, SHARE_URL);
    }

    @OnClick(R.id.btn_back)
    void onBackButtonClicked() {
        if (mDailyTreat.getTreatRank() == TYPE_REVIVE ||
                mDailyTreat.getTreatRank() == TYPE_REVIVE_BIG_PACKAGE) {
            EventBus.getDefault().post(new EventBusRefreshHomeTab());
        }
        dismiss();
    }

    @OnClick(R.id.btn_go_to_bag)
    void onGoToBagButtonClicked() {
        if (daylyTreatPrizeListner != null) daylyTreatPrizeListner.onClickGoToBag();
        dismiss();
    }

    //======= mvp callbacks ========================================================================
//    @Override
//    public void onGetAppConfigSuccessfully(int nextTreatTimeLeft) {
//        if (getViewContext() != null)
//            DailyBonusUtils.setupDailyBonusNotification(Integer.parseInt(mAppPreferences.getUserId()), nextTreatTimeLeft, DailyBonusJobCreator.DAILY);
////        setupNextTreatCountDown(nextTreatTimeLeft);
//    }
//
//    @Override
//    public void onGetAppConfigSuccessFailed() {
//
//    }

    @Override
    public Context getViewContext() {
        return getContext();
    }

    @Override
    public void loadError(String errorMessage, int code) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    //======= sns callback =========================================================================
    @Override
    public void onNotLoginForSharing() {

    }

    @Override
    public void onStartSharing(SocialManager.TypeShare typeShare, Context context) {

    }

    @Override
    public void onErrorSharing(SocialManager.TypeShare typeShare, Context context, String message) {

    }

    @Override
    public void onCompleteSharing(SocialManager.TypeShare typeShare, Context context, String message) {
        if (mScreenshotBitmap != null) {
            mScreenshotBitmap.recycle();
        }
        File file = new File(PRIZE_SCREEN_SHOT_FILE);
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        Timber.d("onCompleteSharing");
    }

    //========= picasso callbacks ==================================================================
    @Override
    public void onFailed(Exception e) {
        if (mImgPrize != null)
            mImgPrize.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onSuccess(Bitmap bitmap) {
        if (isFragmentUIActive()) {
            setupViewsByType();
            startAnimation();
        }
    }

    public void setDaylyTreatPrizeListner(DaylyTreatPrizeListner daylyTreatPrizeListner) {
        this.daylyTreatPrizeListner = daylyTreatPrizeListner;
    }

    //======= inner methods ========================================================================
    private void init() {
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        mShareContent = getString(R.string.daily_treat_share_content, mDailyTreat.getAmount(), mDailyTreat.getTitle());
        mShareTitle = "";
    }

    private void bindView() {
        mTxtPrizeCount.setText(mDailyTreat.getTitle());
        mTxtPrizeName.setText(mDailyTreat.getDescription());
        mTxtRevealPrizeMessage.setText(getString((mDailyTreat.isClaim() ? R.string.daily_treat_result_message_already_claimed : R.string.daily_treat_result_message)));
//        mBtnClaim.setText(getString(mDailyTreat.isClaim() ? R.string.claim_later : R.string.claim));
        if (!mDailyTreat.getImage().isEmpty()) {
            ImageLoaderUtil.displayUserImage(getContext(), mDailyTreat.getImage(), mImgPrize, false, this);
        } else {
            mImgPrize.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

//    /**
//     * @param totalTime in second
//     */
//    private void setupNextTreatCountDown(int totalTime) {
//        mCompositeSubscription.add(mIntervalSubject.mergeWith(Observable.interval(1, TimeUnit.SECONDS))
//                .takeWhile(time -> time != -1)
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(aLong -> {
//                    int timeLeft = (int) (totalTime - aLong);
//                    return StringUtil.convertTimeStampToStringTime(timeLeft, ':');
//                })
//                .subscribe(strTimeLeft -> {
//                    String message = getString(R.string.daily_treat_reopen_in, strTimeLeft);
//                    mTxtComebackMessage.setText(formatTheTime(message, Constants.VIETNAMESE_LANGUAGE_PHONE.equals(UiUtils.getLocalization()) ? 12 : 13, strTimeLeft.length()));
//                }, Timber::e));
//
//    }

    private void setupViewsByType() {
        @DrawableRes int glowRes;
        @DrawableRes int sparkleRes = 0;
        switch (mDailyTreat.getTreatRank()) {
            default:
            case TYPE_MINI:
                glowRes = R.drawable.ic_glow_red;
                break;

            case TYPE_BIG:
                glowRes = R.drawable.ic_glow_blue;
                break;

            case TYPE_ULTIMATE:
                sparkleRes = R.drawable.img_gift_sparkle_big;
                glowRes = R.drawable.ic_glow_yellow;
                break;

            case TYPE_REVIVE:
            case TYPE_REVIVE_BIG_PACKAGE:
                glowRes = R.drawable.ic_glow_yellow;
                break;
        }

        mImgGlow.setImageResource(glowRes);
        mImgSparkle.setImageResource(sparkleRes);
    }

    private void initAnimators() {
        mShowGlowAnimator = ObjectAnimator.ofFloat(mImgGlow, "rotation", 0f, 360f)
                .setDuration(GLOW_ANIMATION_DURATION);
        mShowGlowAnimator.setRepeatMode(ValueAnimator.RESTART);
        mShowGlowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mShowGlowAnimator.setInterpolator(null);
        mShowGlowAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isFragmentUIActive()) {
                    mImgGlow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        sparkleFadeInAnimator = ObjectAnimator.ofFloat(mImgSparkle, "alpha", 0f, 1f);
        sparkleFadeInAnimator.setDuration(SPARKLE_FADE_IN_DURATION);
        sparkleFadeInAnimator.setRepeatMode(ValueAnimator.RESTART);
        //        sparkleFadeInAnimator.setRepeatCount(ValueAnimator.INFINITE);

        sparkleFadeOutAnimator = ObjectAnimator.ofFloat(mImgSparkle, "alpha", 1f, 0f);
        sparkleFadeOutAnimator.setDuration(SPARKLE_FADE_OUT_DURATION);
        sparkleFadeOutAnimator.setRepeatMode(ValueAnimator.RESTART);
        sparkleFadeOutAnimator.setStartDelay(SPARKLE_SCALE_UP_DURATION - SPARKLE_FADE_OUT_DURATION);
        //        sparkleFadeOutAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sparkleFadeOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isFragmentUIActive()) {
                    startSparkleAnimationWidthDelay();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        sparkleScaleXAnimator = ObjectAnimator.ofFloat(mImgSparkle, "scaleX", 0f, 1f);
        sparkleScaleXAnimator.setDuration(SPARKLE_SCALE_UP_DURATION);
        sparkleScaleXAnimator.setRepeatMode(ValueAnimator.RESTART);
//        sparkleScaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);

        sparkleScaleYAnimator = ObjectAnimator.ofFloat(mImgSparkle, "scaleY", 0f, 1f);
        sparkleScaleYAnimator.setDuration(SPARKLE_SCALE_UP_DURATION);
        sparkleScaleYAnimator.setRepeatMode(ValueAnimator.RESTART);
//        sparkleScaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    private void startAnimation() {
        mShowGlowAnimator.start();
        startSparkleAnimation();
    }

    private void startSparkleAnimationWidthDelay() {
        mCompositeSubscription.add(Observable.fromCallable(() -> null)
                .delay(SPARKLE_DELAYED_TIME, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> startSparkleAnimation(), Timber::e));
    }

    private void startSparkleAnimation() {
        if (mDailyTreat.getTreatRank() != TYPE_ULTIMATE) return;
        sparkleScaleXAnimator.start();
        sparkleScaleYAnimator.start();
        sparkleFadeInAnimator.start();
        sparkleFadeOutAnimator.start();
    }

    private int calculateRemainTimeToNextTreat() {
        Calendar today = Calendar.getInstance();
        Calendar nextTreat = Calendar.getInstance();
        nextTreat.add(Calendar.DATE, 1);
        nextTreat.set(Calendar.HOUR_OF_DAY, 9);
        nextTreat.set(Calendar.MINUTE, 1);
        nextTreat.set(Calendar.SECOND, 0);
        nextTreat.set(Calendar.MILLISECOND, 0);

        return (int) ((nextTreat.getTimeInMillis() - today.getTimeInMillis()) / 1000);
    }

    private CharSequence formatTheTime(String text, int start, int length) {
        TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", Typeface.createFromAsset(getContext().getAssets(), "fonts/" + getString(R.string.font_helveticaneuebold)));
        int color = ContextCompat.getColor(getContext(), R.color.daily_treat_time_left);
        int end = start + length;
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(color), start, end, 0);
        spannableString.setSpan(typefaceSpan, start, end, 0);
        return spannableString.subSequence(0, text.length());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    //==== inner classes ===========================================================================
    @IntDef({TYPE_MINI, TYPE_BIG, TYPE_ULTIMATE, TYPE_REVIVE, TYPE_REVIVE_BIG_PACKAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DailyTreatType {
        int TYPE_MINI = 1;
        int TYPE_BIG = 2;
        int TYPE_ULTIMATE = 3;
        int TYPE_REVIVE = 4;
        int TYPE_REVIVE_BIG_PACKAGE = 5;
    }

    public interface DaylyTreatPrizeListner {
        void onClickGoToBag();
    }
}
