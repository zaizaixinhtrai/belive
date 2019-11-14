package com.appster.features.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.appster.R;
import com.appster.features.friend_suggestion.FriendSuggestionActivity;
import com.appster.main.MainActivity;
import com.appster.manager.AppLanguage;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.ConstantBundleKey;
import com.apster.common.Constants;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

import timber.log.Timber;

import static com.appster.AppsterApplication.mAppPreferences;

public class OnBoardingActivity extends WelcomeActivity {

    private static final String CALLED_FROM_LOGIN = "called_from_login";
    private boolean isCalledFromLogin;
    private String mLoginFrom;

    public static Intent createIntent(Context context, boolean isCalledFromLogin, String loginVia) {
        Intent intent = new Intent(context, OnBoardingActivity.class);
        intent.putExtra(CALLED_FROM_LOGIN, isCalledFromLogin);
        intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_FROM, loginVia);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            isCalledFromLogin = getIntent().getExtras().getBoolean(CALLED_FROM_LOGIN, false);
            mLoginFrom = getIntent().getExtras().getString(ConstantBundleKey.BUNDLE_LOGIN_FROM);
        }
        Timber.e("isCalledFromLogin=" + isCalledFromLogin);
    }

    @Override
    protected WelcomeConfiguration configuration() {
        mAppPreferences.setHasShowBoarding(true);
        EventTracker.trackEvent(EventTrackingName.EVENT_ONBOARDING_SCREEN);

        return new WelcomeConfiguration.Builder(this)
                .defaultHeaderTypefacePath("fonts/FuturaStd-Bold.ttf")
                .defaultDescriptionTypefacePath("fonts/helveticalight.ttf")
                .doneButtonTypefacePath("fonts/helveticaneuebold.ttf")
                .bottomLayout(WelcomeConfiguration.BottomLayout.BELIVE)
                .exitAnimation(R.anim.push_in_to_left)
                .page(new BeLivePage(R.drawable.onboarding1,
                        getString(R.string.onboarding_title1),
                        getString(R.string.onboarding_desc1))
                        .background(R.color.color_ffc1a8)
                        .parallax(false)
                )

                .page(new BeLivePage(R.drawable.onboarding2,
                        getString(R.string.onboarding_title2),
                        getString(R.string.onboarding_desc2))
                        .background(R.color.color_fee7ad)
                        .parallax(false)

                )
                .page(new BeLivePage(R.drawable.onboarding3,
                        getString(R.string.onboarding_title3),
                        getString(R.string.onboarding_desc3))
                        .background(R.color.color_bbd6f3)
                        .parallax(false)
                )
                .swipeToDismiss(false)
                .showNextButton(false)
                .canSkip(false)
                .build();
    }

    @Override
    protected void completeWelcomeScreen() {
        EventTracker.trackEvent(EventTrackingName.EVENT_ONBOARDING_SCREEN_START);
        if (isCalledFromLogin) {
            gotoMainScreen();
        } else {
            goToFriendSuggestionScreen();
        }
    }

    @Override
    protected void cancelWelcomeScreen() {
        this.finishAffinity();
    }

    private void gotoMainScreen() {
        AppLanguage.setLocale(this, Constants.APP_LANGUAGE_ENGLISH_EN);
        mAppPreferences.setLanguageType(Constants.APP_LANGUAGE_TYPE_ENGLISH);
        mAppPreferences.setAppLanguage(Constants.APP_LANGUAGE_ENGLISH_EN);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(OnBoardingActivity.this,
                R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(OnBoardingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    private void goToFriendSuggestionScreen() {
        EventTracker.trackEvent(EventTrackingName.EVENT_FRIEND_SUGGESTION);
        Intent intent = FriendSuggestionActivity.createIntent(this, mLoginFrom);
        startActivity(intent);
    }

    public static String onBoardingKey() {
        return "OnBoardingScreen";
    }
}
