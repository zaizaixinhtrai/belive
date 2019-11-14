package com.appster.features.home.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.appster.R;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontTextView;
import com.appster.dialog.NoTitleDialogFragment;
import com.appster.features.jobs.DailyBonusJobCreator;
import com.appster.utility.AppsterUtility;
import com.appster.utility.DailyBonusUtils;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.data.repository.DailyBonusDataRepository;
import com.data.repository.datasource.DailyBonusDataSource;
import com.data.repository.datasource.cloud.CloudDailyBonusDataSource;
import com.domain.interactors.dailybonus.DailyBonusInformationUseCase;
import com.domain.models.TreatItemModel;
import com.domain.repository.DailyBonusRepository;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.appster.AppsterApplication.mAppPreferences;
import static com.appster.features.home.dialog.DailyTreatRevealPrizeDialog.DailyTreatType.TYPE_BIG;
import static com.appster.features.home.dialog.DailyTreatRevealPrizeDialog.DailyTreatType.TYPE_MINI;
import static com.appster.features.home.dialog.DailyTreatRevealPrizeDialog.DailyTreatType.TYPE_ULTIMATE;

/**
 * Created by linh on 06/11/2017.
 */

public class DailyTreatDialog extends NoTitleDialogFragment {
    private static final String DAILY_TREAT = "DAILY_TREAT";
    private static final int TIME_SHOW_TREAT = 500;
    private static final int TIME_SHOW_TREAT_TYPE = TIME_SHOW_TREAT;
    private static final int TREAT_TYPE_IMAGE_ANIMATION_DURATION = 200;
    private static final int GLOW_ANIMATION_REPEAT = 4;
    private static final int GLOW_ANIMATION_DURATION = 3600;

    @Bind(R.id.txt_daily_treat_message)
    CustomFontTextView mTxtMessage;
    @Bind(R.id.img_treat)
    ImageView mImgTreat;
    @Bind(R.id.img_treat_type)
    ImageView mImgTreatType;
    @Bind(R.id.img_glow)
    ImageView mImgGlow;
    @Bind(R.id.btn_open)
    CustomFontButton mBtnOpen;

    private TreatItemModel mDailyTreat;

    ObjectAnimator mShowTreatTypeAnimator;
    ObjectAnimator mShowGlowAnimator;
    AnimatorSet mAnimatorSet;
    Subscription mSubscription;
    private DailyBonusInformationUseCase mDailyBonusInformationUseCase;

    public static DailyTreatDialog newInstance(TreatItemModel dailyTreatType) {
        Bundle args = new Bundle();
        args.putParcelable(DAILY_TREAT, dailyTreatType);
        DailyTreatDialog fragment = new DailyTreatDialog();
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
            mDailyTreat = b.getParcelable(DAILY_TREAT);
        } else {
            mDailyTreat = new TreatItemModel();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        DailyBonusDataSource appConfigDataSource = new CloudDailyBonusDataSource(AppsterWebServices.get(), AppsterUtility.getAuth());
        DailyBonusRepository repository = new DailyBonusDataRepository(appConfigDataSource);
        mDailyBonusInformationUseCase = new DailyBonusInformationUseCase(uiThread, ioThread, repository);
        mSubscription = mDailyBonusInformationUseCase.execute(null)
                .filter(nextBonusInformationModelBaseResponse -> isFragmentUIActive())
                .subscribe(nextBonusInformationModelBaseResponse -> {
                    if (getContext() != null) {
                        DailyBonusUtils.setupDailyBonusNotification(Integer.parseInt(mAppPreferences.getUserId()), nextBonusInformationModelBaseResponse.getData().nextTimeSeconds, DailyBonusJobCreator.DAILY);
                        DailyBonusUtils.setupDailyBonusNotification(Integer.parseInt(mAppPreferences.getUserId()), nextBonusInformationModelBaseResponse.getData().remainingDaySeconds, DailyBonusJobCreator.WEEKLY);
                    }
                });
        setupViewsByType();
        initAnimators();
        startAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxUtils.unsubscribeIfNotNull(mSubscription);
    }

    @Override
    protected int getRootLayoutResource() {
        return R.layout.dialog_daily_treat;
    }

    @Override
    protected boolean isDimDialog() {
        return true;
    }

    @Override
    protected float dimAmount() {
        return 0.9f;
    }

    //======= events handlers ======================================================================
    @OnClick(R.id.btn_open)
    void onOpenButtonClicked() {
        if (mAnimatorSet != null) {
            mAnimatorSet.removeAllListeners();
            mAnimatorSet.cancel();
        }
//        DailyTreatRevealPrizeDialog dialog = DailyTreatRevealPrizeDialog.newInstance(mDailyTreat);
//        dialog.show(getFragmentManager(), DailyTreatRevealPrizeDialog.class.getName());
        dismiss();
    }

    //======= inner methods ========================================================================
    private void setupViewsByType() {
        @DrawableRes int treatRes;
        @DrawableRes int treatTypeRes;
        @DrawableRes int glowRes = 0;
        @StringRes int messageRes;
        treatRes = getTreatRes(mDailyTreat.treatColor);
        switch (mDailyTreat.prizeRank) {
            default:
            case TYPE_MINI:
                treatTypeRes = R.drawable.ic_daily_treat_mini;
                messageRes = R.string.daily_treat_message;
                break;

            case TYPE_BIG:
                treatTypeRes = R.drawable.ic_daily_treat_big;
                messageRes = R.string.daily_treat_message;
                break;

            case TYPE_ULTIMATE:
                treatTypeRes = R.drawable.ic_daily_treat_ultimate;
                glowRes = R.drawable.ic_glow_yellow;
                messageRes = R.string.daily_treat_message_ultimate;
                break;
        }
        mImgTreat.setImageResource(treatRes);
        mImgTreatType.setImageResource(treatTypeRes);
        mImgGlow.setImageResource(glowRes);
        mTxtMessage.setText(messageRes);
    }

    private int getTreatRes(int treatColor) {
        switch (treatColor) {
            case 1:
                return R.drawable.ic_daily_treat_yellow;
            case 2:
                return R.drawable.ic_daily_treat_purle;
            default:
                return R.drawable.ic_daily_treat_green;

        }
    }

    private void initAnimators() {
        mShowTreatTypeAnimator = ObjectAnimator.ofFloat(mImgTreatType, "scaleY", 0f, 1f)
                .setDuration(TREAT_TYPE_IMAGE_ANIMATION_DURATION);
        mShowTreatTypeAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isFragmentUIActive()) {
                    mImgTreatType.setVisibility(View.VISIBLE);
                    mImgTreat.setVisibility(View.INVISIBLE);
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
        mShowGlowAnimator = ObjectAnimator.ofFloat(mImgGlow, "rotation", 0f, 360f)
                .setDuration(GLOW_ANIMATION_DURATION);
        mShowGlowAnimator.setRepeatMode(ValueAnimator.RESTART);
        mShowGlowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mShowGlowAnimator.setInterpolator(null);
        mShowGlowAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mImgGlow != null) mImgGlow.setVisibility(View.VISIBLE);

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


    }

//    private void startAnimationWidthDelay() {
//        mSubscription = Observable.fromCallable(() -> null)
//                .delay(TIME_SHOW_TREAT_TYPE, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(o -> startAnimation(), Timber::e);
//    }

    private void startAnimation() {
        mImgTreat.setVisibility(View.VISIBLE);
        mImgGlow.setVisibility(View.INVISIBLE);
        mImgTreatType.setVisibility(View.INVISIBLE);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorSet.removeAllListeners();
                mAnimatorSet.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimatorSet.removeAllListeners();
                mAnimatorSet.cancel();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        if (mDailyTreat.prizeRank == TYPE_ULTIMATE) {
            mAnimatorSet.playSequentially(mShowTreatTypeAnimator, mShowGlowAnimator);
        } else {
            mAnimatorSet.play(mShowTreatTypeAnimator);
        }
        mAnimatorSet.setStartDelay(TIME_SHOW_TREAT);
        mAnimatorSet.start();
    }


}
