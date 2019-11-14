package com.appster.dialog;

import android.animation.Animator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieTask;
import com.airbnb.lottie.RenderMode;
import com.appster.R;
import com.appster.customview.ExpensiveGift;
import com.appster.message.ChatItemModelClass;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.appster.customview.ExpensiveGift.BURGER;
import static com.appster.customview.ExpensiveGift.COFFEE;
import static com.appster.customview.ExpensiveGift.CUP_CAKE;
import static com.appster.customview.ExpensiveGift.FIRE_WORKS;
import static com.appster.customview.ExpensiveGift.GOOD_FORTUNE;
import static com.appster.customview.ExpensiveGift.HIGH_FIVE;
import static com.appster.customview.ExpensiveGift.LOVE;
import static com.appster.customview.ExpensiveGift.LOVE_BALLOON;
import static com.appster.customview.ExpensiveGift.SNEAKERS;
import static com.appster.customview.ExpensiveGift.SPORTS_CAR;

/**
 * Created by linh on 01/03/2017.
 */

public class ExpensiveGiftDialog extends DialogFragment implements ExpensiveGift.EndAnimation {
    private ExpensiveGift rlExpensiveGift;

    private LottieAnimationView lottieAnimationView;

    private ChatItemModelClass giftItem;

    private OnDialogDismiss dialogDismissCallback;

    private static final String[] mLottieExpensiveGiftId = new String[]{
            BURGER, COFFEE, CUP_CAKE, LOVE, HIGH_FIVE, LOVE_BALLOON, SNEAKERS, GOOD_FORTUNE, FIRE_WORKS, SPORTS_CAR};

    private static final String CUP_CAKE_FILENAME = "BeLive_Cupcake.json";
    private static final String FIRE_WORKS_FILENAME = "BeLive_FIreworks.json";
    private static final String SPORTS_CAR_FILENAME = "BeLive_CAR.json";
    private static final String GOOD_FORTUNE_FILENAME = "BeLive_Fortunecat.json";
    private static final String LOVE_FILENAME = "BeLive_Heart.json";
    private static final String HIGH_FIVE_FILENAME = "BeLive_HI5json.json";
    private static final String LOVE_BALLOON_FILENAME = "BeLive_Hotairballoon.json";
    private static final String SNEAKERS_FILENAME = "BeLive_stansmith.json";
    private static final String COFFEE_FILENAME = "BeLive_Starbucks.json";
    private static final String BURGER_FILENAME = "BeLive_Burger.json";
    private boolean isLottieGift = false;

    private AtomicBoolean mLottieAnimationRunning = new AtomicBoolean(false);
    private final Map<String, String> assetFolders = new HashMap<String, String>() {{
        put(CUP_CAKE_FILENAME, "Images/BeLive_Cupcake");
        put(FIRE_WORKS_FILENAME, "Images/BeLive_FIreworks");
        put(SPORTS_CAR_FILENAME, "Images/BeLive_CAR");
        put(GOOD_FORTUNE_FILENAME, "Images/BeLive_Fortunecat");
        put(LOVE_FILENAME, "Images/BeLive_Heart");
        put(HIGH_FIVE_FILENAME, "Images/BeLive_HI5json");
        put(LOVE_BALLOON_FILENAME, "Images/BeLive_Hotairballoon");
        put(SNEAKERS_FILENAME, "Images/BeLive_stansmith");
        put(COFFEE_FILENAME, "Images/BeLive_Starbucks");
        put(BURGER_FILENAME, "Images/BeLive_Burger");
    }};

    public void setDialogDismissCallback(OnDialogDismiss dialogDismissCallback) {
        this.dialogDismissCallback = dialogDismissCallback;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // Set the IMMERSIVE flag.
            // Set the content to appear under the system bars so that the content
            // doesn't resize when the system bars hide and show.
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(layoutParams);
            window.getAttributes().windowAnimations = R.style.DialogZoomAnimation;

            Timber.e("-- SYSTEM_UI_FLAG_IMMERSIVE_STICKY");
        }

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
//        } else {
//            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.dialog_expensive_gift, container, false);

        rlExpensiveGift = (ExpensiveGift) root.findViewById(R.id.rl_expensive_gift);
        rlExpensiveGift.setEndDetachAnimationCallback(this);
        lottieAnimationView = root.findViewById(R.id.lottieAnimationView);
        initLottieAnimation();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLottieGift) {
            rlExpensiveGift.postDelayed(() -> {
                try {
                    rlExpensiveGift.addGift(giftItem);
                } catch (Exception e) {
                    Timber.d(e);
                    dismiss();
                }
            }, 500);
        } else {
            if (giftItem != null) setUpAnimationByAsset(giftItem.getGiftId());
        }
    }

    @Override
    public void onStop() {
//        if (lottieAnimationView != null) lottieAnimationView.cancelAnimation();
        super.onStop();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Timber.e("-- dialog dismiss");
        mLottieAnimationRunning.set(false);
        super.onDismiss(dialog);
        if (dialogDismissCallback != null) {
            dialogDismissCallback.onDismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * add gift item into,
     * if there is any exist gift then queue it into list.
     */
    public void addGift(ChatItemModelClass itemModelClass) {
        giftItem = itemModelClass;
        isLottieGift = Arrays.asList(mLottieExpensiveGiftId).contains(giftItem.getGiftId());
    }

    private void initLottieAnimation() {
        if (lottieAnimationView != null) {
            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
//                    mLottieAnimationRunning.set(true);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isFragmentUIActive()) {
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                        dismissAllowingStateLoss();
                    }

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (isFragmentUIActive()) {
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                        dismissAllowingStateLoss();
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            lottieAnimationView.setRenderMode(RenderMode.HARDWARE);
            lottieAnimationView.enableMergePathsForKitKatAndAbove(true);
        }
//        lottieAnimationView.setScale(16 / SCALE_SLIDER_FACTOR);
    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }

    /***
     * for
     * @param giftId - which is giftId
     */
    private void setUpAnimationByAsset(String giftId) {
        if (getContext() != null && lottieAnimationView != null && !mLottieAnimationRunning.get()) {
            mLottieAnimationRunning.set(true);
            String assetName = getLottieAssetByGiftId(giftId);
            if (assetName == null) return;
            String folder = assetFolders.get(assetName);

            Observable<LottieComposition> rx = Observable.create(subscriber -> {
                lottieAnimationView.setImageAssetsFolder(folder);
                LottieTask<LottieComposition> task = LottieCompositionFactory.fromAsset(getContext(), assetName);
                task.addListener(composition -> {
                    subscriber.onNext(composition);
                    subscriber.onCompleted();
                });
                task.addFailureListener( error -> {
                    subscriber.onError(error);
                    subscriber.onCompleted();
                });
            });
            rx.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setComposition);
        }

//
    }

    private String getLottieAssetByGiftId(String giftId) {
        switch (giftId) {
            case BURGER:
                return BURGER_FILENAME;
            case COFFEE:
                return COFFEE_FILENAME;
            case CUP_CAKE:
                return CUP_CAKE_FILENAME;
            case LOVE:
                return LOVE_FILENAME;
            case HIGH_FIVE:
                return HIGH_FIVE_FILENAME;
            case LOVE_BALLOON:
                return LOVE_BALLOON_FILENAME;
            case SNEAKERS:
                return SNEAKERS_FILENAME;
            case GOOD_FORTUNE:
                return GOOD_FORTUNE_FILENAME;
            case FIRE_WORKS:
                return FIRE_WORKS_FILENAME;
            case SPORTS_CAR:
                return SPORTS_CAR_FILENAME;
        }
        return null;
    }

    private void setComposition(LottieComposition composition) {
        if (isFragmentUIActive() && lottieAnimationView != null) {
            if (composition.hasImages() && TextUtils.isEmpty(lottieAnimationView.getImageAssetsFolder())) {
                return;
            }

            lottieAnimationView.setComposition(composition);
            playAnimation();
        }
    }

    private void playAnimation() {
        if (lottieAnimationView != null) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.setProgress(0f);
            lottieAnimationView.resumeAnimation();
        }
    }

    @Override
    public void finish() {
        dismissAllowingStateLoss();
    }

    public interface OnDialogDismiss {
        void onDismiss();
    }
}
