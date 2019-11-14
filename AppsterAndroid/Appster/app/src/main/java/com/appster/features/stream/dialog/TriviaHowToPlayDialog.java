package com.appster.features.stream.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.appster.BuildConfig;
import com.appster.R;
import com.appster.customview.SwipeLeftRightDismissTouchListener;
import com.appster.dialog.ImmersiveDialogFragment;
import com.apster.common.Constants;
import com.pack.utility.StringUtil;

import org.jetbrains.annotations.NotNull;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by thanhbc on 3/12/18.
 */

public class TriviaHowToPlayDialog extends ImmersiveDialogFragment {
    @Bind(R.id.webView)
    WebView webView;
    private static final String COUNTRY_CODE = "country_code";
    private String triviaCountryCode;

    public static TriviaHowToPlayDialog newInstance(String triviaCountryCode) {
        Bundle args = new Bundle();
        if (!StringUtil.isNullOrEmptyString(triviaCountryCode))
            args.putString(COUNTRY_CODE, triviaCountryCode);
        TriviaHowToPlayDialog fragment = new TriviaHowToPlayDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isVNTrivia(String countryCOde) {
        return !StringUtil.isNullOrEmptyString(countryCOde) && Constants.COUNTRY_CODE_VN_FROM_SERVER_RETURN.equals(countryCOde);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        Bundle bundle = getArguments();
        if (bundle != null) triviaCountryCode = bundle.getString(COUNTRY_CODE);
        webView.setBackgroundColor(Color.TRANSPARENT);
        loadPage(isVNTrivia(triviaCountryCode) ? BuildConfig.TRIVIA_HOW_TO_PLAY_VI : BuildConfig.TRIVIA_HOW_TO_PLAY);
    }

    private void loadPage(String pageUrl) {
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
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


    @Override
    public void show(FragmentManager manager, String tag) {
        if (isAdded()) return;
        super.show(manager, tag);
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
        return R.layout.dialog_trivia_how_to_play;
    }

    @Override
    protected boolean isDimDialog() {
        return false;
    }

    @OnClick(R.id.ibClose)
    public void onViewClicked() {
        dismissAllowingStateLoss();
    }
}
