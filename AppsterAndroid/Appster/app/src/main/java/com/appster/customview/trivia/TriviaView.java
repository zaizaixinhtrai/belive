package com.appster.customview.trivia;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.RenderMode;
import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.appster.features.stream.Role;
import com.appster.features.stream.viewer.TriviaSound;
import com.apster.common.Constants;
import com.apster.common.Utils;
import com.domain.models.TriviaInfoModel;
import com.domain.models.TriviaResultModel;
import com.pack.utility.StringUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.apster.common.Utils.getNavigationBarSize;

/**
 * Created by thanhbc on 2/22/18.
 */

public class TriviaView extends LinearLayout {

    @Bind(R.id.tvAnswerStatus)
    CustomFontTextView tvAnswerStatus;
    @Bind(R.id.tvQuestion)
    CustomFontTextView tvQuestion;
    @Bind(R.id.option1)
    TriviaOption option1;
    @Bind(R.id.option2)
    TriviaOption option2;
    @Bind(R.id.option3)
    TriviaOption option3;
    @Bind(R.id.triviaTimer)
    TriviaTimer triviaTimer;
    @Bind(R.id.tvRevivedCount)
    CustomFontTextView tvRevivedCount;
    @Bind(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;
    protected TriviaTimerCircleAngleAnimation mAnimation;
    int option1YPosition = 0;

    private TriviaInfoModel.Questions mQuestion;
    private TriviaInfoModel.Questions.Options selectedOption;
    TriviaOption selectedOptionView;
    boolean isTimesup = false;
    OnTriviaOptionListener mTriviaOptionListener;

    @Role
    int mRole = Role.VIEWER;

    @GameState
    int mUserGameState = GameState.ALIVE;

    @GameState
    int mUserPreviousGameState = GameState.ALIVE;

    private static final String COUNTDOWN_FILENAME = "trivia_timer.json";
    private static final String CORRECT_FILENAME = "trivia_correct.json";
    private static final String INCORRECT_FILENAME = "trivia_incorrect.json";
    private static final String DISQUALIFIED_FILENAME = "trivia_disqualified.json";
    private static final String NETWORRK_ERROR_FILENAME = "trivia_network_error.json";
    private MediaPlayer mTriviaPlayer;
    protected CountDownTimer mTriviaSoundDownTimer;
    AtomicInteger mCurrentTriviaVibrateIndex = new AtomicInteger(-1);
    private AtomicBoolean mHasShowDisqualifiedAnimation = new AtomicBoolean(false);
    private int mTriviaPreviousOptionViewId = 0;
    private MediaPlayer mSelectOptionPlayer;

    private Boolean isNetworkError = false;

    public void setNetworkError(Boolean networkError) {
        isNetworkError = networkError;
    }

    public TriviaView(Context context) {
        this(context, null);
    }

    public TriviaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        if (getContext() == null) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.trivia_view, this, true);
        ButterKnife.bind(this, view);
        lottieAnimationView.setRenderMode(RenderMode.HARDWARE);
        lottieAnimationView.enableMergePathsForKitKatAndAbove(true);
        triviaTimer.setDegreesUpTillPreFill(360);
        // The arc will be of 360 degrees - a circle.
        mAnimation = new TriviaTimerCircleAngleAnimation(triviaTimer, 0);
    }

    public void setRole(@Role int role) {
        this.mRole = role;
    }

    public void setUserGameState(@GameState int userGameState) {
        this.mUserGameState = userGameState;
    }

    public void setUserPreviousGameState(@GameState int userPreviousGameState) {
        mUserPreviousGameState = userPreviousGameState;
    }

    public void setTriviaOptionListener(OnTriviaOptionListener triviaOptionListener) {
        mTriviaOptionListener = triviaOptionListener;
    }

    public int getMoveUpHeight() {
        return option1YPosition - Utils.dpToPx(15) + getNavigationBarSize(getContext()).y;
    }


    @OnClick({R.id.option1, R.id.option2, R.id.option3})
    public void onViewClicked(View view) {
        if (isTimesup || mRole == Role.HOST) return;
        isTimesup = true;
        if (mUserGameState == GameState.ELIMINATED) {
//            tvAnswerStatus.setVisibility(VISIBLE);
//            tvAnswerStatus.setText(getAnswerStatusByState(OptionState.ELIMINATED));
//            tvAnswerStatus.setBackground(getDrawableByState(OptionState.ELIMINATED));
//            ((TriviaOption) view).updatePgViewState(R.drawable.trivia_state_normal);

            return;
        }
        grayOutAllOptions(option1, option2, option3);
        selectedOptionView = (TriviaOption) view;
        view.setSelected(true);
        selectedOption = ((TriviaInfoModel.Questions.Options) view.getTag());
        if (mTriviaOptionListener != null && mRole != Role.HOST) {
            mTriviaOptionListener.onTriviaOptionSelected(selectedOption);
            if (mTriviaPreviousOptionViewId != view.getId()) {
                playSelectOptionSound();
                mTriviaPreviousOptionViewId = view.getId();
            }
        }
    }

    public void resetData() {
        selectedOption = null;
        tvQuestion.setText("");
        resetOptionState(option1, option2, option3);
        option1.setOption("");
        option2.setOption("");
        option3.setOption("");
        tvAnswerStatus.setVisibility(GONE);
        isTimesup = false;
        selectedOptionView = null;
        lottieAnimationView.setVisibility(INVISIBLE);
    }

    private void resetOptionState(TriviaOption... options) {
        for (TriviaOption option : options) {
            option.resetState();
        }
    }

    private void setQuestion(TriviaInfoModel.Questions question) {
        setVisibility(VISIBLE);
        if (option1 != null) {
            option1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (option1 != null) {
                        int[] point = new int[2];
                        option1.getLocationOnScreen(point);
                        option1YPosition = point[1];
                        Timber.e("option 1 %s ", option1YPosition);
                        option1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
        if (question.options != null && !question.options.isEmpty()) {
            mQuestion = question;
            tvQuestion.setText(mQuestion.title);
            setOptions(option1, option2, option3);
            animatedViews(tvQuestion, option1, option2, option3);
        }
    }

    void animatedViews(View... views) {
        for (int i = 0; i < views.length; i++) {
            long animationDelay = 150 + (i * 100);
            View view = views[i];
            view.setScaleX(0);
            view.setScaleY(0);
            view.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setDuration(200)
                    .setInterpolator(new DecelerateInterpolator())
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    private void setOptions(TriviaOption... options) {
        for (int i = 0; i < options.length; i++) {
            TriviaOption option = options[i];
            option.setOption(mQuestion.options.get(i).option);
            option.setTag(mQuestion.options.get(i));
        }

    }


    public void startQuestion(TriviaInfoModel.Questions question, int countDownTime) {
        tvRevivedCount.setVisibility(GONE);
        setQuestion(question);
        if (mUserGameState == GameState.ELIMINATED && mRole != Role.HOST) {
            if (triviaTimer != null) {
                triviaTimer.setVisibility(GONE);
                if (!mHasShowDisqualifiedAnimation.get())
                    initEliminatedAnimationListener();
                loadJsonString(getAssetByState(OptionState.ELIMINATED));
            }
            grayOutAllOptions(option1, option2, option3);
        } else {
            resetOptionState(option1, option2, option3);
            loadJsonString(getAssetByState(OptionState.UNSELECTED));
            initAnimationListener();
            //don't need triviatimer here
            if (triviaTimer != null) triviaTimer.setVisibility(INVISIBLE);
//            startCountDown(countDownTime);

            if (mRole != Role.HOST) {
                playTriviaSound(TriviaSound.QUESTIONS_SOUND);
                if (mUserGameState != GameState.ELIMINATED) {
                    startAnswerTimerCountDown(countDownTime);
                }
            }

        }
    }

    private void initAnimationListener() {
        if (lottieAnimationView == null) return;
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                resetLottieListener(this);
                lottieAnimationView.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void initEliminatedAnimationListener() {
        if (lottieAnimationView == null) return;

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                resetLottieListener(this);
                mHasShowDisqualifiedAnimation.set(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    void resetLottieListener(Animator.AnimatorListener animatorListener) {
        if (lottieAnimationView != null) {
            lottieAnimationView.removeAnimatorListener(animatorListener);
        }
    }

    private void startAnswerTimerCountDown(int countDownSecs) {
        if (mTriviaSoundDownTimer != null) mTriviaSoundDownTimer.cancel();
        mTriviaSoundDownTimer = new CountDownTimer(countDownSecs * 1000, 500) {

            @Override
            public void onTick(long l) {
                long secondLeft = l / 1000;
                if (mCurrentTriviaVibrateIndex.get() == secondLeft) return;
                if (secondLeft == 2 || secondLeft == 1 || secondLeft == 0) {
                    turnOnVibration();
                    mCurrentTriviaVibrateIndex.set((int) secondLeft);
                }
            }

            @Override
            public void onFinish() {
                turnOnVibration();
                isTimesup = true;
                //check if user no choose an answer
                if (selectedOptionView == null) {
                    mUserGameState = GameState.ELIMINATED;
                }
            }
        }.start();
    }

    void turnOnVibration() {
        if (getContext() == null || getContext().getSystemService(VIBRATOR_SERVICE) == null) return;
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(350);
        }
    }

    private void playTriviaSound(@TriviaSound int triviaSound) {
        if (mTriviaPlayer != null && mTriviaPlayer.isPlaying()) {
            mTriviaPlayer.stop();
        }
        int rawId = -1;
        Timber.e("triviaSound= %s", triviaSound);
        switch (triviaSound) {
            case TriviaSound.QUESTIONS_SOUND:
                rawId = R.raw.trivia_question_music;
                break;
            case TriviaSound.CORRECT_SOUND:
                rawId = R.raw.trivia_answer_correct_sound;
                break;
            case TriviaSound.WRONG_SOUND:
                rawId = R.raw.trivia_answer_wrong_sound;
                break;
        }

        mTriviaPlayer = MediaPlayer.create(getContext(), rawId);
        setTriviaSoundVolume();
        mTriviaPlayer.setLooping(false);
        mTriviaPlayer.setOnCompletionListener(onCompletionTriviaPlayerListener);
        mTriviaPlayer.start();
    }

    private void playSelectOptionSound() {

        if (mSelectOptionPlayer == null) {
            mSelectOptionPlayer = MediaPlayer.create(getContext(), R.raw.trivia_select_question_sound);
            mSelectOptionPlayer.setLooping(false);
            mSelectOptionPlayer.setOnCompletionListener(onCompletionSelectOptionPlayerListener);
        } else {
            if (mSelectOptionPlayer.isPlaying()) mSelectOptionPlayer.stop();
        }
        mSelectOptionPlayer.start();
    }

    private void setTriviaSoundVolume() {
        AudioManager audio = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if (audio == null) return;
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float percent = 0.5f;
        float newVolume = (currentVolume * percent) / maxVolume;
        mTriviaPlayer.setVolume(newVolume, newVolume);
    }

    private MediaPlayer.OnCompletionListener onCompletionTriviaPlayerListener = mp -> {
        if (mTriviaPlayer != null) {
            mTriviaPlayer.release();
            mTriviaPlayer = null;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionSelectOptionPlayerListener = mp -> {
        if (mSelectOptionPlayer != null) {
            mSelectOptionPlayer.release();
            mSelectOptionPlayer = null;
        }
    };

    public void releaseAllPlayer() {
        if (mTriviaPlayer != null) {
            mTriviaPlayer.stop();
            mTriviaPlayer.release();
            mTriviaPlayer = null;
        }
        if (mSelectOptionPlayer != null) {
            mSelectOptionPlayer.stop();
            mSelectOptionPlayer.release();
            mSelectOptionPlayer = null;
        }
    }

    public void setAnswerStatusText(String content) {
        if (mRole == Role.HOST && tvAnswerStatus != null) {
            tvAnswerStatus.setBackground(getDrawableByState(OptionState.UNSELECTED));
            tvAnswerStatus.setText(content);
            tvAnswerStatus.setVisibility(VISIBLE);
        }
    }

    private void grayOutAllOptions(TriviaOption... options) {
        for (TriviaOption option : options) {
            if (option != null) option.updateEliminatedState();
        }
    }


    public void showAnswer(TriviaResultModel result, String countryCode) {
        if (result == null) return;
        animatedViews(tvQuestion, option1, option2, option3);
        resetOptionState(option1, option2, option3);
        boolean userChooseCorrectAns = result.isCorrectAnswer;
        int participants = result.participant;
        if (mRole == Role.HOST || mUserGameState == GameState.ELIMINATED) {
            triviaTimer.setDegreesUpTillPreFill(360);
            triviaTimer.setText("");
            triviaTimer.requestLayout();
            triviaTimer.setVisibility(GONE);
            tvAnswerStatus.setVisibility(GONE);
        }
        if (selectedOptionView == null) {
            //time's up or eliminated
            mUserGameState = GameState.ELIMINATED;
            if (mRole != Role.HOST) {
                if (!mHasShowDisqualifiedAnimation.get())
                    initEliminatedAnimationListener();
                loadJsonString(getAssetByState(OptionState.ELIMINATED));
            }
            for (int i = 0; i < result.answers.size(); i++) {
                TriviaResultModel.TriviaAnswers answer = result.answers.get(i);
                int state = answer.isAnswer ? OptionState.CORRECT : OptionState.UNSELECTED;
                if (i == 0) {
                    updateOptionAnswerState(option1, participants, state, answer.chosenNum);
                } else if (i == 1) {
                    updateOptionAnswerState(option2, participants, state, answer.chosenNum);
                } else if (i == 2) {
                    updateOptionAnswerState(option3, participants, state, answer.chosenNum);
                }
            }
        } else {
            loadJsonString(getAssetByState(userChooseCorrectAns ? OptionState.CORRECT : OptionState.INCORRECT));
//            tvAnswerStatus.setVisibility(VISIBLE);
//            tvAnswerStatus.setText(getAnswerStatusByState(userChooseCorrectAns ? OptionState.CORRECT : OptionState.INCORRECT));
//            tvAnswerStatus.setBackground(getDrawableByState(userChooseCorrectAns ? OptionState.CORRECT : OptionState.INCORRECT));
            mUserGameState = userChooseCorrectAns ? GameState.ALIVE : GameState.ELIMINATED;
            for (int i = 0; i < result.answers.size(); i++) {
                TriviaResultModel.TriviaAnswers answer = result.answers.get(i);
                boolean isChosenAnswer = answer.optionId == selectedOption.optionId;
                int state = isChosenAnswer ?
                        (answer.isAnswer ? OptionState.CORRECT : OptionState.INCORRECT) :
                        (answer.isAnswer ? OptionState.CORRECT : OptionState.UNSELECTED);
                Timber.e("OptionState %s", state);
                if (i == 0) {
                    updateOptionAnswerState(option1, participants, state, answer.chosenNum);
                } else if (i == 1) {
                    updateOptionAnswerState(option2, participants, state, answer.chosenNum);
                } else if (i == 2) {
                    updateOptionAnswerState(option3, participants, state, answer.chosenNum);
                }
            }
        }
        if (mTriviaOptionListener != null && mRole != Role.HOST && mUserGameState != mUserPreviousGameState) {
            playTriviaSound(TriviaSound.WRONG_SOUND);
            mTriviaOptionListener.onUserGameStateUpdated(mUserGameState);
            mUserPreviousGameState = mUserGameState;
        }

        if (mRole != Role.HOST && mUserGameState == GameState.ALIVE) {
            playTriviaSound(TriviaSound.CORRECT_SOUND);
        }

        // show revived count
        if (result.previousRevivedCount > 0) {
            tvRevivedCount.setVisibility(VISIBLE);
            tvRevivedCount.setText(String.format(isVNTrivia(countryCode) ? getContext().getString(R.string.revived_from_last_round_vi) : getContext().getString(R.string.revived_from_last_round), String.valueOf(result.previousRevivedCount)));
        } else {
            tvRevivedCount.setVisibility(GONE);
        }
        setVisibility(VISIBLE);
    }

    private boolean isVNTrivia(String countryCode) {
        if (StringUtil.isNullOrEmptyString(countryCode)) return false;
        return Constants.COUNTRY_CODE_VN_FROM_SERVER_RETURN.equals(countryCode);
    }

    private void updateOptionAnswerState(TriviaOption option, int participants, @OptionState int state, int selectedNum) {
        if (option != null) {
            option.updateState(state,
                    participants,
                    selectedNum);
        }
    }

    String getAnswerStatusByState(@OptionState int state) {
        String answer = "";
        int stringRes = -1;
        switch (state) {
            case OptionState.CORRECT:
                stringRes = R.string.trivia_ans_correct;
                break;
            case OptionState.ELIMINATED:
                stringRes = R.string.trivia_ans_eliminated;
                break;
            case OptionState.INCORRECT:
                stringRes = R.string.trivia_ans_incorrect;
                break;
            case OptionState.UNSELECTED:
                break;
        }
        if (stringRes != -1 && getContext() != null) answer = getContext().getString(stringRes);
        return answer;
    }

    Drawable getDrawableByState(@OptionState int state) {
        int drawable = R.drawable.bg_trivia_answer_idle;
        switch (state) {
            case OptionState.CORRECT:
                drawable = R.drawable.bg_trivia_answer_correct;
                break;
            case OptionState.INCORRECT:
            case OptionState.ELIMINATED:
                drawable = R.drawable.bg_trivia_answer_incorrect_eliminated;
                break;
            case OptionState.UNSELECTED:
                drawable = R.drawable.bg_trivia_answer_idle;
                break;
        }
        return ContextCompat.getDrawable(getContext(), drawable);
    }


    public void setViewToEliminated() {
        mUserGameState = GameState.ELIMINATED;
        selectedOptionView = null;
        if (triviaTimer != null && tvAnswerStatus != null) {
            if (mAnimation != null) mAnimation.setAnimationListener(null);
            triviaTimer.clearAnimation();
            triviaTimer.setDegreesUpTillPreFill(360);
            triviaTimer.setText("");
            triviaTimer.requestLayout();
            tvAnswerStatus.setText(getAnswerStatusByState(OptionState.ELIMINATED));
            tvAnswerStatus.setBackground(getDrawableByState(OptionState.ELIMINATED));
            tvAnswerStatus.setVisibility(VISIBLE);
        }
        grayOutAllOptions(option1, option2, option3);

    }

    //region animation

    private String getAssetByState(int state) {
        String fileName;
        switch (state) {
            case OptionState.CORRECT:
                fileName = CORRECT_FILENAME;
                break;
            case OptionState.INCORRECT:
                fileName = isNetworkError ? NETWORRK_ERROR_FILENAME : INCORRECT_FILENAME;
                break;
            case OptionState.ELIMINATED:
                fileName = DISQUALIFIED_FILENAME;
                break;
            default:
                fileName = COUNTDOWN_FILENAME;
                break;
        }
        return fileName;
    }

    private void loadJsonString(String fileName) {
        if (fileName == null || getContext() == null) {
            return;
        }
        lottieAnimationView.setProgress((fileName.equals(DISQUALIFIED_FILENAME) && mHasShowDisqualifiedAnimation.get()) ? 1f : 0f);
        LottieComposition.Factory.fromAssetFileName(getContext(), fileName, composition -> {
            if (composition != null) {
                setComposition(composition);
            }
        });

    }

    private void setComposition(LottieComposition composition) {
        if (lottieAnimationView != null) {
            if (composition.hasImages() && TextUtils.isEmpty(lottieAnimationView.getImageAssetsFolder())) {
                return;
            }

            lottieAnimationView.setComposition(composition);
            playAnimation();
        }
    }

    void playAnimation() {
        if (lottieAnimationView != null) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
        }
    }

    //endregion

    public interface OnTriviaOptionListener {
        void onTriviaOptionSelected(TriviaInfoModel.Questions.Options selectedOption);

        void onUserGameStateUpdated(@GameState int newState);
    }
}
