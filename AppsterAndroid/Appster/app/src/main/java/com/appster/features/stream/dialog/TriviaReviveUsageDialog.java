package com.appster.features.stream.dialog;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.appster.BuildConfig;
import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.SwipeLeftRightDismissTouchListener;
import com.appster.data.AppPreferences;
import com.appster.dialog.ImmersiveDialogFragment;
import com.appster.layout.SquareImageView;
import com.appster.models.UserModel;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.SocialManager;
import com.apster.common.BranchIoUtil;
import com.apster.common.Constants;
import com.apster.common.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.pack.utility.CheckNetwork;
import com.pack.utility.StringUtil;

import org.jetbrains.annotations.NotNull;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by thanhbc on 3/12/18.
 */

public class TriviaReviveUsageDialog extends ImmersiveDialogFragment {

    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.llShareItemContainer)
    LinearLayout llShareItemContainer;
    @Bind(R.id.tv_share)
    CustomFontTextView tvShare;

    UserModel mUserModel;
    String mSharableBranchIoUrl;
    CallbackManager fbCallbackManager;

    private static final int REQUEST_CODE_FB_SHARE = 1;
    private static final String COUNTRY_CODE = "country_code";
    private String triviaCountryCode;

    public static TriviaReviveUsageDialog newInstance(String triviaCountryCode) {
        TriviaReviveUsageDialog fragment = new TriviaReviveUsageDialog();
        Bundle args = new Bundle();
        if (!StringUtil.isNullOrEmptyString(triviaCountryCode))
            args.putString(COUNTRY_CODE, triviaCountryCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.e("onStart");
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(Utils.dpToPx(260), Utils.dpToPx(375));
            getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) triviaCountryCode = bundle.getString(COUNTRY_CODE);
        setCancelable(false);
        AppPreferences myAppPreferences = AppPreferences.getInstance(getContext());
        mUserModel = myAppPreferences.getUserModel();
        webView.setBackgroundColor(Color.TRANSPARENT);
        loadPage(isVNTrivia(triviaCountryCode) ? BuildConfig.TRIVIA_REVIVE_DESC_VI : BuildConfig.TRIVIA_REVIVE_DESC);
        addShareItems();
        if (isVNTrivia(triviaCountryCode)) tvShare.setText(R.string.share_referral_code_vi);
    }

    private boolean isVNTrivia(String countryCOde) {
        return !StringUtil.isNullOrEmptyString(countryCOde) && Constants.COUNTRY_CODE_VN_FROM_SERVER_RETURN.equals(countryCOde);
    }

    private void addShareItems() {
        if (getContext() == null) return;
        llShareItemContainer.removeAllViews();
        addView(R.drawable.icon_share_facebook, getContext().getString(R.string.share_live_facebook), view -> {
            AppsterUtility.temporaryLockView(view);
            getBranchIoUrl(url -> {
                if (isFragmentUIActive()) {
                    mSharableBranchIoUrl = url;
                    SocialManager.getInstance().shareURLToFacebook(getContext(), url,
                            REQUEST_CODE_FB_SHARE,
                            getFbCallbackManager(),
                            new FacebookCallback<Sharer.Result>() {
                                @Override
                                public void onSuccess(Sharer.Result result) {
                                }

                                @Override
                                public void onCancel() {
                                }

                                @Override
                                public void onError(FacebookException error) {
                                }
                            });
                }
            });
        });

        addView(R.drawable.icon_share_whatsapp, getContext().getString(R.string.share_live_whatsapp), view -> {
            AppsterUtility.temporaryLockView(view);
            getBranchIoUrl(url -> {
                String content = String.format(getString(R.string.invite_sns_trivia_message), mUserModel.getReferralId());
                SocialManager.getInstance().shareVideoToWhatsapp(getContext(), content, url);

            });
        });

        addView(R.drawable.icon_share_twitter, getContext().getString(R.string.share_live_twitter), view -> {
            AppsterUtility.temporaryLockView(view);
            getBranchIoUrl(url -> {

                if (isFragmentUIActive()) {
                    String content = String.format(getString(R.string.invite_sns_trivia_message), mUserModel.getReferralId());
                    SocialManager.getInstance().ShareFeedQuotesToTwitter(getContext(), content, url);

                }
            });
        });

        addView(R.drawable.icon_share_email, getContext().getString(R.string.share_live_email), view -> {
            AppsterUtility.temporaryLockView(view);
            getBranchIoUrl(url -> {
                if (isFragmentUIActive()) {
                    String content = String.format(getString(R.string.invite_sns_trivia_message), mUserModel.getReferralId());
                    String subject = getString(R.string.invite_mail_subject);
                    SocialManager.getInstance().shareURLToEmail(getContext(), content, subject, url);

                }
            });
        });

        addView(R.drawable.icon_share_others, getContext().getString(R.string.share_live_others), view -> {
            AppsterUtility.temporaryLockView(view);
            getBranchIoUrl(url -> {
                if (isFragmentUIActive()) {
                    String content = String.format(getString(R.string.invite_sns_trivia_message), mUserModel.getReferralId()) + "\n" + url;
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, content);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));

                }
            });
        });
    }

    private void addView(int drawableId, String text, View.OnClickListener clickListener) {
        View categoryView = LayoutInflater.from(getContext()).inflate(
                R.layout.trivia_share_item, llShareItemContainer, false);
        llShareItemContainer.addView(categoryView);
        SquareImageView categoryImage = (SquareImageView) categoryView.findViewById(R.id.ivCategoryImage);
        ImageLoaderUtil.displayMediaImage(getContext(), drawableId, categoryImage);
        TextView categoryTitle = (TextView) categoryView.findViewById(R.id.tvCategoryTitle);
        categoryTitle.setText(text);

        if (clickListener != null) {
            categoryView.setOnClickListener(clickListener);
        }
    }

    private void loadPage(String pageUrl) {
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(pageUrl);
    }


    public void getBranchIoUrl(BranchIoUtil.OnBranchIoCallback callback) {
        if (!CheckNetwork.isNetworkAvailable(getContext())) {
            return;
        }

        if (!TextUtils.isEmpty(mSharableBranchIoUrl)) {
            callback.onComplete(mSharableBranchIoUrl);
            return;
        }

        BranchIoUtil.generateBranchIoUrl(getContext(), mUserModel.getUserImage(), mUserModel.getReferralId(), callback);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        View decorView = window.getDecorView();
        if (decorView != null) {
            decorView.setOnTouchListener(new SwipeLeftRightDismissTouchListener(decorView, null, new SwipeLeftRightDismissTouchListener.DismissCallbacks() {
                @Override
                public boolean canDismiss(@NotNull Object token) {
                    return true;
                }

                @Override
                public void onDismiss(@NotNull View view, @NotNull Object token) {
                    dismiss();
                }
            }));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Window window = getDialog().getWindow();
        View decorView = window.getDecorView();
        if (decorView != null) {
            decorView.setOnTouchListener(null);
        }
    }

    @Override
    protected int getRootLayoutResource() {
        return R.layout.dialog_trivia_revive_usage;
    }

    @OnClick(R.id.ibClose)
    public void onViewClicked() {
        dismissAllowingStateLoss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isAdded()) return;
        super.show(manager, tag);
    }

    @Override
    protected boolean isDimDialog() {
        return false;
    }

    CallbackManager getFbCallbackManager() {
        if (fbCallbackManager == null) {
            fbCallbackManager = CallbackManager.Factory.create();
        }
        return fbCallbackManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
