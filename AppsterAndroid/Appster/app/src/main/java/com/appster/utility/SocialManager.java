package com.appster.utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.R;
import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.appster.services.BeLiveTwitterApiClient;
import com.appster.webservice.request_models.LoginFacebookRequestModel;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.DownloadBitmap;
import com.apster.common.DownloadVideos;
import com.apster.common.FileUtility;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pack.utility.StringUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import rx.Subscription;
import timber.log.Timber;

/**
 * Created by USER on 10/14/2015.
 */
public class SocialManager {


    private static CallbackManager callbackManager;
    private static SocialManager instance = null;
    static GoogleApiClient googleApiClient;
    public Context context;
    public boolean login;
    private boolean createdAccount;
    private Bitmap bitmapSend;
    //    AccessToken accessToken = null;
    SocialLoginListener socialLoginListener;
    public SocialSharingListener socialSharingListener;
    boolean isStartingTask;
    public boolean isComepleteSharing;

    public static String SHARE_TYPE_POST = "post";
    public static String SHARE_TYPE_STREAM = "stream";

    public enum TypeShare {
        SHARE_FACEBOOK,
        SHARE_INSTAGRAM,
        SHARE_TWITTER,
        SHARE_WHATSAPP
    }


    private Bitmap getBitmapSend() {
        return bitmapSend;
    }

    public void setBitmapSend(Bitmap bitmapSend) {
        this.bitmapSend = bitmapSend;
    }

    public boolean isCreatedAccount() {
        return createdAccount;
    }

    public void setCreatedAccount(boolean createdAccount) {
        this.createdAccount = createdAccount;
    }


    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public void cancelStartingTask() {
        isStartingTask = false;
    }


    static synchronized public SocialManager getInstance() {
        if (instance == null) {
            instance = new SocialManager();
        }

        return instance;
    }

    public static void cancelInstance() {
        callbackManager = null;
        instance = null;

    }


    public void login(final Context context, SocialLoginListener callback, final LoginFacebookRequestModel requestLogin) {
        this.context = context;

        socialLoginListener = callback;

//        if (AccessToken.getCurrentAccessToken() == null || AccessToken.getCurrentAccessToken().isExpired()) {
        isStartingTask = true;
        if (socialLoginListener != null) {
            socialLoginListener.onStartingAuthentication();
        }

        // Logout
        LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions((Activity) context,
                Collections.singletonList("public_profile, email,user_friends"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        requestDataFacebook(loginResult.getAccessToken(), requestLogin);
                    }

                    @Override
                    public void onCancel() {

                        if (AppsterApplication.mAppPreferences.isLoginFacebook()) {
                            if (socialLoginListener != null) {
                                socialLoginListener.loginWithFacebookInfo(requestLogin);
                            }
                        }
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");

                    }
                });

//        } else {
//            requestDataFacebook(AccessToken.getCurrentAccessToken(), requestLogin);
//        }

    }


    void requestDataFacebook(AccessToken mToken, final LoginFacebookRequestModel requestLogin) {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,email,gender");
        new GraphRequest(mToken, "/me", params, HttpMethod.GET, response -> {
            if (response.getError() != null) {
                // handle error
                System.out.println("ERROR");
                if (socialLoginListener != null) {
                    socialLoginListener.onLoginFail(String.valueOf(response.getError()));
                }
            } else {
                System.out.println("Success");

                try {
                    if (socialLoginListener != null) {
                        socialLoginListener.onAuthentSuccess();
                    }
                    JSONObject json = response.getJSONObject();
                    String str_email;
                    try {
                        str_email = json.getString("email");
                    } catch (JSONException error) {
                        str_email = "no email";
                    }
                    String gender = "";
                    if (json.has("gender")) {
                        gender = json.getString("gender");
                    }
                    String str_id = json.getString("id");
                    String str_name = json.getString("name");
                    requestLogin.setEmail(str_email);
                    requestLogin.setUserName(StringUtil.extractUserNameFromEmail(str_email));
                    requestLogin.setFb_id(str_id);
                    requestLogin.setDisplay_name(str_name);
                    AppsterApplication.mAppPreferences.setFacebookDisplayName(str_name);
                    requestLogin.setProfile_Pic(Constants.getFBImageProfile(str_id));
                    requestLogin.setGender(gender);

                    isStartingTask = false;
                    if (socialLoginListener != null) {
                        socialLoginListener.loginWithFacebookInfo(requestLogin);
                    }

                } catch (JSONException e) {
                    if (socialLoginListener != null) {
                        socialLoginListener.onLoginFail(response.getError().toString());
                    }

                }

            }


        }).executeAsync();
    }

    public void shareFBStream(Context context, String content, String link) {
        loginForSharing(context, CommonDefine.TYPE_STREAM, null, content, link);
    }

    public void shareTweet(Context context, String content, String link) {
        socialSharingListener = (SocialSharingListener) context;
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        BeLiveTwitterApiClient beLiveTwitterApiClient = new BeLiveTwitterApiClient(session);
        beLiveTwitterApiClient.getCustomService().postTweet(content + " " + link)
                .enqueue(new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        Timber.e("share completed");
                        if (socialSharingListener != null) {
                            socialSharingListener.onCompleteSharing(TypeShare.SHARE_TWITTER, context, context.getResources().getString(R.string.stream_share_twitter_success));
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Timber.e("share failed %s", exception.getMessage());
                        if (socialSharingListener != null) {
                            socialSharingListener.onErrorSharing(TypeShare.SHARE_TWITTER, context, context.getResources().getString(R.string.some_error_happen_please_try));
                        }
                    }
                });

    }

    public void shareFacebookPopup(final Context context, final int type, final Uri uri, final String discription, final Bitmap bitmap) {
        shareFacebookPopup(context, (SocialSharingListener) context, type, uri, discription, bitmap);
    }

    public void shareFacebookPopup(final Context context, SocialSharingListener listener, final int type, final Uri uri, final String discription, final Bitmap bitmap) {
        socialSharingListener = listener;
        ShareDialog shareDialog = new ShareDialog((Activity) context);
        if (callbackManager == null) callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, null, Constants.REQUEST_CODE_SHARE_FEED);
        switch (type) {
            case CommonDefine.TYPE_QUOTES:
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(discription))
                        .build();
                shareDialog.show(linkContent);

                break;
            case CommonDefine.TYPE_VIDEO:
                ShareVideo shareVideo = new ShareVideo.Builder()
                        .setLocalUrl(uri)
                        .build();
                ShareVideoContent content = new ShareVideoContent.Builder()
                        .setVideo(shareVideo)
                        .build();
                shareDialog.show(content);
                break;
            case CommonDefine.TYPE_IMAGE:

                SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).setCaption(discription).build();
                SharePhotoContent imageContent = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(imageContent);
                break;
            default:
                break;
        }


    }

    public void loginForSharing(final Context context, final int type, final Uri uri, final String discription) {
        loginForSharing(context, type, uri, discription, "");
    }

    private void loginForSharing(final Context context, final int type, final Uri uri, final String discription, String link) {
        this.context = context;
        socialSharingListener = (SocialSharingListener) context;
        if (AccessToken.getCurrentAccessToken() == null || AccessToken.getCurrentAccessToken().isExpired()) {
            isStartingTask = true;
            if (socialLoginListener != null) {
                socialLoginListener.onStartingAuthentication();
            }
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions((Activity) context,
                    Collections.singletonList("public_profile, email"));


            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(final LoginResult loginResult) {
                            if (socialLoginListener != null) {
                                socialLoginListener.onAuthentSuccess();
                            }

                            Bundle params = new Bundle();
                            params.putString("fields", "id,name,email");

                            new GraphRequest(loginResult.getAccessToken(), "/me", params, HttpMethod.GET, response -> {
                                if (response.getError() != null) {
                                    // handle error
                                    System.out.println("ERROR");
                                    if (socialLoginListener != null) {
                                        socialLoginListener.onLoginFail(response.getError().toString());
                                    }
                                } else {
                                    System.out.println("Success");

                                    try {
                                        if (socialLoginListener != null) {
                                            socialLoginListener.onAuthentSuccess();
                                        }
                                        JSONObject json = response.getJSONObject();
                                        String str_name = json.getString("name");

                                        AppsterApplication.mAppPreferences.setFacebookDisplayName(str_name);
                                        isStartingTask = false;

                                        if (socialLoginListener != null) {
                                            socialLoginListener.onCompleteLogin();
                                        }

                                        shareFacebookBackground(context, type, uri, discription, link);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        if (socialLoginListener != null) {
                                            socialLoginListener.onLoginFail(response.getError().toString());
                                        }
                                    }
                                }
                            }).executeAsync();

                        }


                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException exception) {
                            System.out.println("onError");

                        }
                    });

        } else {
            isStartingTask = true;
            shareFacebookBackground(context, type, uri, discription, link);
        }
    }


    public void loginInSettingScreen(final Context context) {
        this.context = context;

        socialLoginListener = (SocialLoginListener) context;

        if (AccessToken.getCurrentAccessToken() == null || AccessToken.getCurrentAccessToken().isExpired()) {
            isStartingTask = true;
            if (socialLoginListener != null) {
                socialLoginListener.onStartingAuthentication();
            }

            // Logout
            LoginManager.getInstance().logOut();

            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions((Activity) context,
                    Collections.singletonList("public_profile, email"));

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(final LoginResult loginResult) {

                            System.out.println("Success");
                            Bundle params = new Bundle();
                            params.putString("fields", "id,name,email");
                            new GraphRequest(loginResult.getAccessToken(), "/me", params, HttpMethod.GET, response -> {
                                if (response.getError() != null) {
                                    // handle error
                                    System.out.println("ERROR");
                                    if (socialLoginListener != null) {
                                        socialLoginListener.onLoginFail(response.getError().toString());
                                    }
                                } else {
                                    System.out.println("Success");

                                    try {
                                        if (socialLoginListener != null) {
                                            socialLoginListener.onAuthentSuccess();
                                        }
                                        JSONObject json = response.getJSONObject();
                                        String str_name = json.getString("name");

                                        AppsterApplication.mAppPreferences.setFacebookDisplayName(str_name);
                                        isStartingTask = false;

                                        if (socialLoginListener != null) {
                                            socialLoginListener.onCompleteLogin();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        if (socialLoginListener != null) {
                                            socialLoginListener.onLoginFail(response.getError().toString());
                                        }
                                    }
                                }
                            }).executeAsync();
                        }


                        @Override
                        public void onCancel() {


                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Timber.d(exception);

                        }
                    });

        }
    }

    public void logOut() {
        LoginManager.getInstance().logOut();
        socialSharingListener = null;
        setBitmapSend(null);
        callbackManager = null;
        instance = null;
        context = null;
        AppsterApplication.mAppPreferences.setLoginFacebook(false);
        AccessToken.setCurrentAccessToken(null);
    }

    public boolean isFacebookLoggedIn() {

        return !(AccessToken.getCurrentAccessToken() == null || AccessToken.getCurrentAccessToken().isExpired());

    }

    public void getFacebookAccessToken(Context context, OnGetFBTokenListener listener) {
        Timber.e("getFacebookAccessToken");
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            listener.onSuccess(accessToken.getToken());
        } else {
            if (context == null) return;
            // Logout
            LoginManager.getInstance().logOut();
            isStartingTask = true;
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions((Activity) context,
                    Collections.singletonList("public_profile, email,user_friends"));

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {

                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Timber.e(loginResult.toString());
                            listener.onSuccess(loginResult.getAccessToken().getToken());
                            isStartingTask = false;
                        }

                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Timber.e(error);
                            listener.onFailed();
                        }
                    });
        }
    }

    void shareFacebookBackground(final Context context, int typeShare, final Uri uriPath, String discription, String link) {

        socialSharingListener.onStartSharing(TypeShare.SHARE_FACEBOOK, context);
        switch (typeShare) {
            case CommonDefine.TYPE_STREAM:
                Bundle paramsStream = new Bundle();
                paramsStream.putString("message", discription);
                paramsStream.putString("link", link);
                // params.putString("access");

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/feed",
                        paramsStream,
                        HttpMethod.POST,
                        response -> {

                            if (response.getError() != null) {
                                if (socialSharingListener != null) {
                                    socialSharingListener.onErrorSharing(TypeShare.SHARE_FACEBOOK, context, response.toString());
                                }
                            } else {

                                if (socialSharingListener != null) {
                                    socialSharingListener.onCompleteSharing(TypeShare.SHARE_FACEBOOK, context, context.getResources().getString(R.string.social_sucess));
                                }
                            }

                            isStartingTask = false;
                        }
                ).executeAsync();
                break;
            case CommonDefine.TYPE_QUOTES:

                Bundle params = new Bundle();
                params.putString("message", discription);
                // params.putString("access");

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/feed",
                        params,
                        HttpMethod.POST,
                        response -> {

                            if (response.getError() != null) {
                                if (socialSharingListener != null) {
                                    socialSharingListener.onErrorSharing(TypeShare.SHARE_FACEBOOK, context, response.toString());
                                }
                            } else {

                                if (socialSharingListener != null) {
                                    socialSharingListener.onCompleteSharing(TypeShare.SHARE_FACEBOOK, context, context.getResources().getString(R.string.social_sucess));
                                }
                            }

                            isStartingTask = false;
                        }
                ).executeAsync();
                break;
            case CommonDefine.TYPE_VIDEO:

                shareVideoUserGraph(AppsterUtility.getRealPathFromURI(uriPath, (Activity) context), discription);


                break;
            case CommonDefine.TYPE_IMAGE:
                SharePhoto sharePhoto;
                if (discription == null) {
                    sharePhoto = new SharePhoto.Builder()
                            .setBitmap(getBitmapSend())

                            .build();
                } else {
                    sharePhoto = new SharePhoto.Builder()
                            .setBitmap(getBitmapSend())
                            .setCaption(discription)
                            .build();
                }
                SharePhotoContent content1 = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                ShareApi.share(content1, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        if (socialSharingListener != null) {
                            socialSharingListener.onCompleteSharing(TypeShare.SHARE_FACEBOOK, context, context.getResources().getString(R.string.social_sucess));
                            isStartingTask = false;
                        }
                    }

                    @Override
                    public void onCancel() {
                        isStartingTask = false;

                    }

                    @Override
                    public void onError(FacebookException error) {
                        if (socialSharingListener != null) {
                            socialSharingListener.onErrorSharing(TypeShare.SHARE_FACEBOOK, context, error.toString());
                            isStartingTask = false;
                        }
                    }
                });
                break;
        }


    }

    public void shareFeedToFacebook(Context context, ItemModelClassNewsFeed item) {
        if (item == null) return;
        ShareDialog shareDialog = new ShareDialog((AppCompatActivity) context);
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(item.getWebPostUrl()))
                .build();

        shareDialog.show(linkContent);
    }

    public void shareURLToFacebook(Context context, String Url) {
        shareURLToFacebook(context, Url, -1, null, null);
    }

    public void shareURLToFacebook(Context context, String Url, int requestCode, CallbackManager callbackManager, FacebookCallback<Sharer.Result> shareCallBack) {
        ShareDialog shareDialog = new ShareDialog((AppCompatActivity) context);
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(Url))
                .build();
        if (requestCode != -1 && shareCallBack != null && callbackManager != null)
            shareDialog.registerCallback(callbackManager, shareCallBack, requestCode);
        shareDialog.show(linkContent);
    }

    public void shareURLToFacebook(Context context, String Url, int requestCode, FacebookCallback<Sharer.Result> shareCallBack) {
        isStartingTask = true;
        if (callbackManager == null) callbackManager = CallbackManager.Factory.create();
        shareURLToFacebook(context, Url, requestCode, callbackManager, shareCallBack);
    }


    public void shareFeedToTwitter(final Context context, final ItemModelClassNewsFeed item, boolean isStream) {
        if (item == null) return;
        if (item.getMediaType() == CommonDefine.TYPE_IMAGE) {

            String fileName = getFileNameFromURL(item.getMediaImage());

            DownloadBitmap.getInstance().deleteAllFile();
            if (DownloadBitmap.getInstance().isNeedDownloadedImage(Constants.APPSTERS_IMAGE_SHARE,
                    fileName)) {

                DownloadBitmap.getInstance().downloadBitmap(context, item.getMediaImage(),
                        Constants.APPSTERS_IMAGE_SHARE, fileName,
                        new DownloadBitmap.IDownloadListener() {
                            @Override
                            public void successful(String filePath) {
                                ShareFeedImageToTwitter(context, Uri.parse(filePath), item.getWebPostUrl(), item.getUserName(), SHARE_TYPE_POST);
                            }

                            @Override
                            public void fail() {

                            }
                        });
            } else {
                ShareFeedImageToTwitter(context,
                        Uri.parse(Constants.APPSTERS_IMAGE_SHARE + File.separator + fileName),
                        item.getWebPostUrl(), item.getUserName(), SHARE_TYPE_POST);
            }

        } else if (item.getMediaType() == CommonDefine.TYPE_VIDEO) {

            FileUtility.deleteVideoCacheFile();
            DownloadVideos.getInstance().isVideoAlreadyDownloaded(item.getNfs_MediyaVideo(),
                    (isNeedToDownload, videoLocalPath) -> ((Activity) context).runOnUiThread(() -> {
                        if (!isNeedToDownload) {
                            shareVideoToTwitter(context, videoLocalPath, item.getWebPostUrl(), item.getUserName(), SHARE_TYPE_POST);
                        } else {

                            DownloadVideos.getInstance().downloadVideoFile(item.getNfs_MediyaVideo(), new DownloadVideos.IDownloadListener() {
                                @Override
                                public void successful(final String filePath) {
                                    ((Activity) context).runOnUiThread(() -> shareVideoToTwitter(context, filePath, item.getWebPostUrl(), item.getUserName(), SHARE_TYPE_POST));
                                }

                                @Override
                                public void fail() {
                                }
                            });
                        }
                    }));
        } else {
            ShareFeedQuotesToTwitter(context, item.getWebPostUrl(), item.getUserName(), "", isStream ? SHARE_TYPE_STREAM : SHARE_TYPE_POST, true);
        }
    }

    private String getFileNameFromURL(String url) {

        if (StringUtil.isNullOrEmptyString(url)) {
            return "";
        }

        return url.substring(url.lastIndexOf("/"));
    }

    public void ShareFeedQuotesToTwitter(Context context, String url, String userName, String title, String type, boolean isHost) {
        ShareFeedQuotesToTwitter(context, getShareContent(context, userName, title, type, isHost), url);
    }

    public void ShareFeedQuotesToTwitter(Context context, String content, String url) {
        Intent intent = new TweetComposer.Builder(context)
                .text(content + "\n" + url).createIntent();
        ((Activity) context).startActivityForResult(intent, Constants.TWEET_SHARE_REQUEST_CODE);
    }

    void ShareFeedImageToTwitter(Context context, final Uri uri, String url, String userName, String type) {

        URL urlImage;
        try {
            urlImage = new URL(url);
        } catch (MalformedURLException e) {
            Timber.e(e);
            return;
        }

        TweetComposer.Builder builder = new TweetComposer.Builder(context)
                .text(getShareContent(context, userName, "", type, false))
                .image(uri)
                .url(urlImage);

        builder.show();
    }

    void shareVideoToTwitter(Context context, String filepath, String url, String userName, String type) {


        try {
            File f = new File(filepath);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setClassName("com.twitter.android", "com.twitter.composer.SelfThreadComposerActivity");
            intent.setType("video/*");
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            intent.putExtra(Intent.EXTRA_TEXT, getShareContent(context, userName, "", type, false) + " " + url);
            intent.putExtra(Intent.EXTRA_SUBJECT, getShareContent(context, userName, "", type, false) + " " + url);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.putExtra(Intent.EXTRA_STREAM, replaceUriNotAllowed(context, Uri.fromFile(f)));
            } else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            }
            // Check if the Twitter app is installed on the phone.
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_SHARE_FEED);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.share_no_twitter_app), Toast.LENGTH_LONG).show();

        }
    }


    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        if (isStartingTask && callbackManager != null) {
            callbackManager.onActivityResult(requestCode, responseCode, data);
        }

    }

    private RxPermissions mRxPermissions;
    Subscription mSubscriptionExternalPermissions;

    private void createShareExternalPermissions(Context context, int mediaType, Uri uri) {
        mSubscriptionExternalPermissions = mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        shareFeedToInstagram(context, mediaType, uri);
                        FileUtility.deleteVideoCacheFile();
                        DownloadBitmap.getInstance().deleteAllFile();
                    }
                    RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                });
    }

    public void shareFeedToInstagram(final Context context, final ItemModelClassNewsFeed itemFeed) {
        if (itemFeed == null) return;
        mRxPermissions = new RxPermissions((Activity) context);
        if (itemFeed.getMediaType() == CommonDefine.TYPE_IMAGE) {

            String fileName = getFileNameFromURL(itemFeed.getMediaImage());
//            DownloadBitmap.getInstance().deleteAllFile();
            mSubscriptionExternalPermissions = mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            DownloadBitmap.getInstance().deleteAllFile();
                            if (DownloadBitmap.getInstance().isNeedDownloadedImage(Constants.APPSTERS_IMAGE_SHARE,
                                    fileName)) {
                                DownloadBitmap.getInstance().downloadBitmap(context, itemFeed.getMediaImage(),
                                        Constants.APPSTERS_IMAGE_SHARE, fileName,
                                        new DownloadBitmap.IDownloadListener() {
                                            @Override
                                            public void successful(String filePath) {
                                                shareFeedToInstagram(context, itemFeed.getMediaType(), Uri.parse("file://" + filePath));
                                                RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                                            }

                                            @Override
                                            public void fail() {
                                                Timber.e("DownloadBitmap_fail");
                                                RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                                            }
                                        });
                            } else {
                                shareFeedToInstagram(context, itemFeed.getMediaType(), Uri.parse("file://" + Constants.APPSTERS_IMAGE_SHARE +
                                        fileName));
                                RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                            }
                        } else {
                            RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                        }
                    });

        } else if (itemFeed.getMediaType() == CommonDefine.TYPE_VIDEO) {
//            FileUtility.deleteVideoCacheFile();
            mSubscriptionExternalPermissions = mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            FileUtility.deleteVideoCacheFile();
                            DownloadVideos.getInstance().isVideoAlreadyDownloaded(itemFeed.getNfs_MediyaVideo(),
                                    (isNeedToDownload, videoLocalPath) -> ((Activity) context).runOnUiThread(() -> {
                                        if (!isNeedToDownload) {
                                            shareFeedToInstagram(context, itemFeed.getMediaType(), Uri.parse(videoLocalPath));
                                        } else {
                                            DownloadVideos.getInstance().downloadVideoFile(itemFeed.getNfs_MediyaVideo(), new DownloadVideos.IDownloadListener() {
                                                @Override
                                                public void successful(final String filePath) {
                                                    ((Activity) context).runOnUiThread(() -> {
                                                        shareFeedToInstagram(context, itemFeed.getMediaType(), Uri.parse(filePath));
                                                        RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                                                    });
                                                }

                                                @Override
                                                public void fail() {
                                                    RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                                                }
                                            });
                                        }
                                    }));
                        } else {
                            RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                        }
                    });
        } else {

            Toast.makeText(context.getApplicationContext(), context.getString(R.string.share_instagram_not_share_text), Toast.LENGTH_SHORT).show();
        }
    }

    private Uri replaceUriNotAllowed(Context context, Uri uri) {
        if (uri == null)
            return uri;

        String uriString = uri.toString();
        if (StringUtil.isNullOrEmptyString(uriString)) return uri;
        if (uriString.startsWith("file:///")) {
            uriString = uriString.replace("file:///", "");
        }

        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(uriString));

    }

    public void shareFeedToInstagram(Context context, int type, final Uri uri) {

        // Create the new Intent using the 'Send' action.\
        String typeShare = null;
        if (type == CommonDefine.TYPE_IMAGE) {
            typeShare = "image/*";

        } else if (type == CommonDefine.TYPE_VIDEO) {
            typeShare = "video/*";
        }

        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");

            shareIntent.putExtra(Intent.EXTRA_STREAM, replaceUriNotAllowed(context, uri));
            shareIntent.setType(typeShare);
            ((Activity) context).startActivityForResult(shareIntent, Constants.REQUEST_CODE_SHARE_FEED);
            //Close current activity
        } else {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.share_no_instagram_app), Toast.LENGTH_SHORT).show();
        }
    }


    private void shareVideoUserGraph(String videoPath, String description) {

        GraphRequest request = GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(), "/me/videos", null, response -> {
            if (response.getError() != null) {
                String error = response.getError().toString();
                if (socialSharingListener != null) {
                    socialSharingListener.onErrorSharing(TypeShare.SHARE_FACEBOOK, context, error);
                }
            } else {
                if (socialSharingListener != null) {
                    socialSharingListener.onCompleteSharing(TypeShare.SHARE_FACEBOOK, context, context.getResources().getString(R.string.social_sucess));
                }
            }

        });
        Bundle params = request.getParameters();
        try {
            byte[] data = readBytes(videoPath);
            params.putByteArray("video.mp4", data);
            params.putString("description", description);
            request.setParameters(params);
            request.executeAsync();
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    private byte[] readBytes(String dataPath) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        try (InputStream inputStream = new FileInputStream(dataPath)) {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            Timber.e(e);
        }
        return byteBuffer.toByteArray();
    }

    public void shareFeedToWhatsapp(final Context context, final ItemModelClassNewsFeed itemFeed, boolean isStream) {
        if (itemFeed == null) return;
        mRxPermissions = new RxPermissions((Activity) context);
        if (itemFeed.getMediaType() == CommonDefine.TYPE_IMAGE) {

            String fileName = getFileNameFromURL(itemFeed.getMediaImage());

//            DownloadBitmap.getInstance().deleteAllFile();
            mSubscriptionExternalPermissions = mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            if (DownloadBitmap.getInstance().isNeedDownloadedImage(Constants.APPSTERS_IMAGE_SHARE, fileName)) {
                                DownloadBitmap.getInstance().deleteAllFile();
                                DownloadBitmap.getInstance().downloadBitmap(context, itemFeed.getMediaImage(),
                                        Constants.APPSTERS_IMAGE_SHARE, fileName,
                                        new DownloadBitmap.IDownloadListener() {
                                            @Override
                                            public void successful(String filePath) {
                                                shareImageToWhatsapp(context, Uri.parse(filePath), itemFeed.getWebPostUrl(), itemFeed.getUserName(), SHARE_TYPE_POST);
                                                RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                                            }

                                            @Override
                                            public void fail() {
                                                RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                                            }
                                        });
                            } else {
                                shareImageToWhatsapp(context, Uri.parse(Constants.APPSTERS_IMAGE_SHARE +
                                        fileName), itemFeed.getWebPostUrl(), itemFeed.getUserName(), SHARE_TYPE_POST);
                                RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                            }
                        }
                        RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                    });

        } else if (itemFeed.getMediaType() == CommonDefine.TYPE_VIDEO) {
            shareVideoToWhatsapp(context, itemFeed.getWebPostUrl(), itemFeed.getUserName(), "", SHARE_TYPE_POST, true);
        } else {
            shareQuotesToWhatsapp(context, itemFeed.getTitle(), itemFeed.getWebPostUrl(), itemFeed.getUserName(), isStream ? SHARE_TYPE_STREAM : SHARE_TYPE_POST);
        }
    }

    void shareImageToWhatsapp(Context context, Uri uriImage, String urlImage, String userName, String type) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("image/*");
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra(Intent.EXTRA_STREAM, replaceUriNotAllowed(context, uriImage));
        if (SHARE_TYPE_STREAM.equals(type)) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, getStreamShareContent(context, userName) + System.getProperty("line.separator") + urlImage);
        } else {
            sendIntent.putExtra(Intent.EXTRA_TEXT, getShareContent(context, userName, "", type, false) + urlImage);
        }

        try {
            ((Activity) context).startActivityForResult(sendIntent, Constants.REQUEST_CODE_SHARE_FEED);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.whatsapp_have_not_been_installed), Toast.LENGTH_SHORT).show();
        }
    }

    public void shareVideoToWhatsapp(Context context, String urlvideo, String userName, String title, String type, boolean isHost) {
        shareVideoToWhatsapp(context, urlvideo, getShareContent(context, userName, title, type, isHost));
    }

    public void shareVideoToWhatsapp(Context context, String content, String url) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, content + "\n" + url);
        sendIntent.setPackage("com.whatsapp");

        try {
            ((Activity) context).startActivityForResult(sendIntent, Constants.REQUEST_CODE_SHARE_FEED);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.whatsapp_have_not_been_installed), Toast.LENGTH_SHORT).show();
        }
    }

    public void shareStreamURLToWhatsapp(Context context, Uri uriFile, String url, String userName) {
        mRxPermissions = new RxPermissions((Activity) context);
        mSubscriptionExternalPermissions = mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        shareImageToWhatsapp(context, uriFile, url, userName, SHARE_TYPE_STREAM);
                        RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                    }
                    RxUtils.unsubscribeIfNotNull(mSubscriptionExternalPermissions);
                });
    }

    private void shareQuotesToWhatsapp(Context context, String text, String url, String userName, String type) {
        String content;
        if (SHARE_TYPE_STREAM.equals(type)) {
            content = getShareContent(context, userName, "", type, false) + "\n" + url;
        } else {
            content = getShareContent(context, userName, "", type, false) + url;
        }
        shareQuotesToWhatsapp(context, text, content);
    }

    public void shareQuotesToWhatsapp(Context context, String title, String url, String content) {
        String str = content + "\n" + url;
        shareQuotesToWhatsapp(context, title, str);
    }

    public void shareQuotesToWhatsapp(Context context, String title, String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);

        try {
            ((Activity) context).startActivityForResult(sendIntent, Constants.REQUEST_CODE_SHARE_FEED);
        } catch (android.content.ActivityNotFoundException ex) {
            AppsterApplication.mAppPreferences.saveShareStreamModel(null);
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.whatsapp_have_not_been_installed), Toast.LENGTH_SHORT).show();
        }
    }

    public void shareFeedToShareAction(final boolean isEmail, final Context context, final ItemModelClassNewsFeed itemFeed, boolean isStream) {
        if (itemFeed == null) return;
        if (itemFeed.getMediaType() == CommonDefine.TYPE_IMAGE) {

            String fileName = getFileNameFromURL(itemFeed.getMediaImage());

            DownloadBitmap.getInstance().deleteAllFile();
            if (DownloadBitmap.getInstance().isNeedDownloadedImage(Constants.APPSTERS_IMAGE_SHARE,
                    fileName)) {

                DownloadBitmap.getInstance().downloadBitmap(context, itemFeed.getMediaImage(),
                        Constants.APPSTERS_IMAGE_SHARE, fileName,
                        new DownloadBitmap.IDownloadListener() {
                            @Override
                            public void successful(String filePath) {
                                if (isEmail) {
                                    shareImageToEmail(context, Uri.parse("file://" + filePath), itemFeed.getWebPostUrl(), itemFeed.getUserName(), SHARE_TYPE_POST);
                                } else {
                                    shareImageToOthers(context, Uri.parse("file://" + filePath), itemFeed.getWebPostUrl(), itemFeed.getUserName(), SHARE_TYPE_POST);

                                }
                            }

                            @Override
                            public void fail() {

                            }
                        });
            } else {
                if (isEmail) {
                    shareImageToEmail(context, Uri.parse("file://" + Constants.APPSTERS_IMAGE_SHARE +
                            fileName), itemFeed.getWebPostUrl(), itemFeed.getUserName(), SHARE_TYPE_POST);
                } else {
                    shareImageToOthers(context, Uri.parse("file://" + Constants.APPSTERS_IMAGE_SHARE +
                            fileName), itemFeed.getWebPostUrl(), itemFeed.getUserName(), SHARE_TYPE_POST);
                }
            }

        } else if (itemFeed.getMediaType() == CommonDefine.TYPE_VIDEO) {

            if (!isEmail) {
                shareVideoToOthers(context, itemFeed.getWebPostUrl(), itemFeed.getUserName(), SHARE_TYPE_POST);
                return;
            }
            FileUtility.deleteVideoCacheFile();
            DownloadVideos.getInstance().isVideoAlreadyDownloaded(itemFeed.getNfs_MediyaVideo(), (isNeedToDownload, videoLocalPath) -> ((Activity) context).runOnUiThread(() -> {
                if (!isNeedToDownload) {
                    shareVideoToEmail(context, Uri.parse("file://" + videoLocalPath), itemFeed.getWebPostUrl(), itemFeed.getUserName(), SHARE_TYPE_POST);
                } else {

                    DownloadVideos.getInstance().downloadVideoFile(itemFeed.getNfs_MediyaVideo(), new DownloadVideos.IDownloadListener() {
                        @Override
                        public void successful(final String filePath) {
                            ((Activity) context).runOnUiThread(() -> shareVideoToEmail(context, Uri.parse("file://" + videoLocalPath), itemFeed.getWebPostUrl(), itemFeed.getUserName(), SHARE_TYPE_POST));

                        }

                        @Override
                        public void fail() {
                        }
                    });
                }
            }));

        } else {
            if (isEmail) {
                shareQuotesToEmail(context, itemFeed, isStream);
            } else {
                shareQuotesToOthers(context, itemFeed.getWebPostUrl(), itemFeed.getUserName(), isStream ? SHARE_TYPE_STREAM : SHARE_TYPE_POST);
            }
        }
    }


    public void shareURLToEmail(Context context, String URL, String userName, String title, String type, boolean isHost) {
        shareURLToEmail(context, getShareContent(context, userName, title, type, isHost), context.getString(R.string.invite_mail_subject), URL);
    }

    public void shareURLToOthers(Context context, String URL, String userName, String title, String type, boolean isHost) {
        shareURLToOthers(context, getShareContent(context, userName, title, type, isHost), URL);
    }

    public void shareURLToEmail(Context context, String content, String subject, String url) {
        String str = content + "\n" + url;
        shareQuotesToEmail(context, subject, str);

    }

    public void shareURLToOthers(Context context, String content, String url) {
        String str = content + "\n" + url;
        shareQuotesToOthers(context, str);

    }

    private void shareQuotesToEmail(Context context, ItemModelClassNewsFeed itemFeed, boolean isStream) {
        String subject = getShareContent(context, itemFeed.getUserName(), "", isStream ? SHARE_TYPE_STREAM : SHARE_TYPE_POST, false).trim();
        String url = itemFeed.getWebPostUrl();
        shareQuotesToEmail(context, subject, url);
    }

    private void shareQuotesToEmail(Context context, String subject, String content) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content + "\n");

        try {
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_SHARE_FEED);
            } else {
                AppsterApplication.mAppPreferences.saveShareStreamModel(null);
            }
        } catch (android.content.ActivityNotFoundException ex) {
            Timber.d(ex);
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.email_have_not_been_installed), Toast.LENGTH_SHORT).show();
            AppsterApplication.mAppPreferences.saveShareStreamModel(null);
        }
    }

    private void shareQuotesToOthers(Context context, String url, String userName, String type) {
        String content;
        if (SHARE_TYPE_STREAM.equals(type)) {
            content = getShareContent(context, userName, "", type, false) + "\n" + url;
        } else {
            content = getShareContent(context, userName, "", type, false) + url;
        }
        shareQuotesToOthers(context, content);
    }

    private void shareQuotesToOthers(Context context, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, content + "\n");
        intent.setType("text/plain");
//        context.startActivity(Intent.createChooser(intent, "Share Via"));
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Share Via"), Constants.REQUEST_CODE_SHARE_FEED);
    }

    @SuppressLint("StringFormatInvalid")
    private String getStreamShareContent(Context context, String userName) {
        String validatedUserName = TextUtils.isEmpty(userName) ? "" : userName.trim();
        String content = String.format(context.getString(R.string.title_share_end_stream), "", validatedUserName).trim();

        return content;
    }

    void shareImageToEmail(Context context, Uri uriImage, String urlImage, String userName, String type) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, getShareContent(context, userName, "", type, false).trim());
        intent.putExtra(android.content.Intent.EXTRA_TEXT, urlImage);
        intent.putExtra(Intent.EXTRA_STREAM, replaceUriNotAllowed(context, uriImage));
        try {
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_SHARE_FEED);
            }
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.email_have_not_been_installed), Toast.LENGTH_SHORT).show();
        }
    }

    void shareVideoToEmail(Context context, Uri uriImage, String urlImage, String userName, String type) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, getShareContent(context, userName, "", type, false).trim());
        intent.putExtra(android.content.Intent.EXTRA_TEXT, urlImage);
        intent.putExtra(Intent.EXTRA_STREAM, uriImage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_SHARE_FEED);
        }
    }

    void shareImageToOthers(Context context, Uri uriImage, String urlImage, String userName, String type) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_STREAM, replaceUriNotAllowed(context, uriImage));
        if (SHARE_TYPE_STREAM.equals(type)) {
            intent.putExtra(Intent.EXTRA_TEXT, getStreamShareContent(context, userName) + System.getProperty("line.separator") + urlImage);
        } else {
            intent.putExtra(Intent.EXTRA_TEXT, getShareContent(context, userName, "", type, false) + urlImage);
        }
        context.startActivity(Intent.createChooser(intent, "Share Via"));

    }

    void shareVideoToOthers(Context context, String urlImage, String userName, String type) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.setType("text/plain");
        if (SHARE_TYPE_STREAM.equals(type)) {
            intent.putExtra(Intent.EXTRA_TEXT, getStreamShareContent(context, userName) + System.getProperty("line.separator") + urlImage);
        } else {
            intent.putExtra(Intent.EXTRA_TEXT, getShareContent(context, userName, "", type, false) + urlImage);
        }
        context.startActivity(Intent.createChooser(intent, "Share Via"));

    }

    @SuppressLint("StringFormatInvalid")
    private String getShareContent(Context context, String userName, String title, String type, boolean isHost) {
        if (SHARE_TYPE_STREAM.equals(type)) {
            String validatedUserName = TextUtils.isEmpty(userName) ? "" : userName.trim();
            String validatedTitle = TextUtils.isEmpty(title) ? "" : title.trim();
            String content;
            if (isHost) {
                content = String.format(context.getString(R.string.header_title_share_stream_host), validatedTitle, validatedUserName).trim();

            } else {
                content = String.format(context.getString(R.string.header_title_share_stream_viewer), validatedTitle, validatedUserName).trim();
            }

            return content;
        } else {//SHARE_TYPE_POST
            return String.format(context.getString(R.string.header_title_share_post), type, userName);
        }
    }

    public boolean hasSetupTwitter(Context context) {
        PackageManager pkManager = context.getPackageManager();
        try {
            PackageInfo pkgInfo = pkManager.getPackageInfo("com.twitter.android", 0);
            String getPkgInfo = pkgInfo.toString();
            if (!getPkgInfo.contains("com.twitter.android")) {
                // APP NOT INSTALLED
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.share_no_twitter_app), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.share_no_twitter_app), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean hasSetupInstagram(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent == null) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.share_no_instagram_app), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean hasSetupWhatsapp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
        if (intent == null) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.wechat_have_not_been_installed), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean hasSetupWeChat(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
        if (intent == null) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.weibo_have_not_been_installed), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean hasSetupEmail(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);

        if (list.size() == 0) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.email_have_not_been_installed), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public static void loginWithGoogle(final AppCompatActivity context, int requestCode, GoogleApiClient.OnConnectionFailedListener callback) {
        googleApiClient = getGoogleApiClient(context, getGoogleSignInOptions());

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        context.startActivityForResult(signInIntent, requestCode);
    }

    private static GoogleSignInOptions getGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    private static GoogleApiClient getGoogleApiClient(Context context, GoogleSignInOptions gso) {
        if (googleApiClient == null) {
            return new GoogleApiClient.Builder(context.getApplicationContext())
                    .enableAutoManage((AppCompatActivity) context, null)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        return googleApiClient;
    }

    public static void logoutGoogle(final AppCompatActivity activity, GoogleApiClient.ConnectionCallbacks callback) {
        if (googleApiClient == null) {
            googleApiClient = getGoogleApiClient(activity, getGoogleSignInOptions());
        }
        googleApiClient.connect();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        status -> {
                            if (googleApiClient != null && status.isSuccess() && googleApiClient.isConnected()) {
                                if (activity != null && !activity.isFinishing()) {
                                    googleApiClient.stopAutoManage(activity);
                                }
                                googleApiClient.disconnect();
                                googleApiClient = null;
                            }
                            Timber.d("logoutGoogle %s", String.valueOf(status.isSuccess()));
                        });
                if (callback != null) {
                    callback.onConnected(bundle);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                if (callback != null) {
                    callback.onConnectionSuspended(i);
                }
            }
        });
    }

    public static void releaseGoogleLogin(AppCompatActivity context) {
        if (googleApiClient != null) {
            googleApiClient.stopAutoManage(context);
            googleApiClient.disconnect();
            googleApiClient = null;
        }
    }

    public interface SocialLoginListener {
        void onStartingAuthentication();


        void onLoginFail(String message);

        void onAuthentSuccess();

        void loginWithFacebookInfo(LoginFacebookRequestModel requestLogin);


        void onCompleteLogin();

    }


    public interface SocialSharingListener {

        void onNotLoginForSharing();

        void onStartSharing(TypeShare typeShare, Context context);

        void onErrorSharing(TypeShare typeShare, Context context, String message);

        void onCompleteSharing(TypeShare typeShare, Context context, String message);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public interface OnGetFBTokenListener {

        void onSuccess(String accessToken);

        void onFailed();
    }
}


