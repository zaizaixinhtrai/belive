package com.appster.post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.bundle.BundleMedia;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.taggableedittext.TaggableEditText;
import com.appster.location.GPSTClass;
import com.appster.location.GetAddress;
import com.appster.models.PostDataModel;
import com.appster.models.event_bus_models.EventBusRefreshFragment;
import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.SocialManager;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.EditPostRequestModel;
import com.appster.webservice.request_models.LoginFacebookRequestModel;
import com.appster.webservice.request_models.PostCreatePostRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.CountryCode;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.UiUtils;
import com.apster.common.Utils;
import com.pack.utility.CheckNetwork;
import com.pack.utility.StringUtil;
import com.pack.utility.VideoUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.apster.common.FileUtility.MEDIA_TYPE_IMAGE_CROPPED;

/**
 * Created by User on 9/21/2015.
 */


public class ActivityPostMedia extends BaseActivity implements PostContract.View, View.OnClickListener,
        SocialManager.SocialSharingListener, SocialManager.SocialLoginListener {

    private final int CROP_PIC = 1;
    private final int UNKNOWN_LOCATION = -1;
    @Bind(R.id.pageTitle)
    TextView pageTitle;
    @Bind(R.id.image_videos)
    ImageView imageVideos;
    @Bind(R.id.post_play_video)
    ImageView postPlayVideo;
    @Bind(R.id.fm_media)
    FrameLayout fmMedia;
    @Bind(R.id.postDescription)
    TaggableEditText edtPostDescription;
    @Bind(R.id.txt_numberText)
    TextView txtNumberText;
    @Bind(R.id.checkInText)
    CustomFontTextView txtCheckIn;
    @Bind(R.id.cancel_text_iv)
    ImageView cancelTextIv;

    @Bind(R.id.ll_share_facebook)
    LinearLayout llShareFacebook;
    @Bind(R.id.ll_share_twister)
    LinearLayout llShareTwitter;
    @Bind(R.id.ll_share_instagram)
    LinearLayout llShareInstagram;
    @Bind(R.id.ll_share_whatsApp)
    LinearLayout llShareWhatsApp;

    @Bind(R.id.ll_share_email)
    LinearLayout llShareEmail;
    @Bind(R.id.ll_share_others)
    LinearLayout llShareOthers;
    @Bind(R.id.v_share_facebook_divider)
    View vShareFacebookDivider;
    @Bind(R.id.v_share_twitter_divider)
    View vShareTwitterDivider;
    @Bind(R.id.v_share_instagram_divider)
    View vShareInstagramDivider;
    @Bind(R.id.v_share_whatsapp_divider)
    View vShareWhatsappDivider;


    @Bind(R.id.btn_post)
    Button btnPost;


    @Bind(R.id.ll_root)
    LinearLayout llRoot;

    private String address = "";
    private String pathVideoCamera = "";
    private Bitmap imageThumbnailVideo;

    private String postDescription = "";
    private Uri uriImageSend;
    private Bitmap bitmapSend;

    private int type = 1;
    private boolean isEditMode;
    private Uri uriData;
    private BundleMedia bundleMedia;
    private String pathMedia;
    boolean isCompletedSharing;
    ItemModelClassNewsFeed itemModelClassNewsFeed;
    String descriptionBefore = "";

    PostContract.PostActions mPresenter;

    private RxPermissions mRxPermissions;

    public static Intent createIntent(Context context) {
        return new Intent(context, ActivityPostMedia.class);
    }

    //==== lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);
        ButterKnife.bind(this);
        mPresenter = new PostPresenter(AppsterWebServices.get(), "Bearer " + AppsterApplication.mAppPreferences.getUserToken());
        mPresenter.attachView(this);
        bundleMedia = (BundleMedia) getBaseBundle(ConstantBundleKey.BUNDLE_MEDIA_KEY);
        intiView();
        setOnclickListener();
        setLimitTextCount();
        getDataSend();
        disableClickSharing();
        mRxPermissions = new RxPermissions(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        handlePostButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDialog();
        mPresenter.detachView();
    }

    //=== mvp callback
    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {

    }

    @Override
    public void showProgress() {
        if (!DialogManager.isShowing()) {
            DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));
        }
    }

    @Override
    public void hideProgress() {
        DialogManager.getInstance().dismisDialog();
    }

    @Override
    public void onPostSuccessfully(PostDataModel data) {
        itemModelClassNewsFeed = new ItemModelClassNewsFeed(data);
        checkShareFeed();
    }

    @Override
    public void onPostFailed() {

    }

    @Override
    public void onEditPostSuccessfully(BaseResponse<PostDataModel> editPostModel) {
        Toast.makeText(getApplicationContext(), getString(R.string.post_post_updated), Toast.LENGTH_SHORT).show();
        if (editPostModel.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
            Toast.makeText(getApplicationContext(), editPostModel.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        itemModelClassNewsFeed = bundleMedia.getItemModelClassNewsFeed();
        if (itemModelClassNewsFeed != null) {
            checkShareFeed();
        } else {
            releaseMemory(ActivityPostMedia.this);
        }
    }

    @Override
    public void onEditPostFailed() {

    }

    @Override
    public void showProgress(String message) {
        showDialog(this, getString(R.string.post_updating));
    }
    //=== end mvp callback

    private void intiView() {
        llRoot.setOnClickListener(this);
        cancelTextIv.setOnClickListener(v -> {
            cancelTextIv.setVisibility(View.INVISIBLE);
            address = "";
            txtCheckIn.setText(address);
            txtCheckIn.setCustomFont(this, getString(R.string.font_opensansregular));
            UiUtils.hideSoftKeyboard(ActivityPostMedia.this);
        });

        txtCheckIn.setOnClickListener(v -> {
            if (txtCheckIn.getLinksClickable()) {
                onClickCheckLocation(v);
                UiUtils.hideSoftKeyboard(ActivityPostMedia.this);
            }
        });

        llShareFacebook.setOnClickListener(view -> {
            llShareFacebook.setSelected(!llShareFacebook.isSelected());
            if (llShareFacebook.isSelected()) {
                llShareTwitter.setSelected(false);
                llShareInstagram.setSelected(false);
                llShareWhatsApp.setSelected(false);
                llShareEmail.setSelected(false);
                llShareOthers.setSelected(false);
            }
            UiUtils.hideSoftKeyboard(ActivityPostMedia.this);
        });

        imageVideos.setOnClickListener(view -> {

//            if (bitmapSend == null && StringUtil.isNullOrEmptyString(bundleMedia.getPostId())) {
                if (isEditMode) return;
                if (type == CommonDefine.TYPE_VIDEO) {
//                    showVideosPopUp();
                } else if (type == CommonDefine.TYPE_IMAGE) {
                    showPicPopUp();
                }
//            }
        });

        llShareTwitter.setOnClickListener(view -> {
            llShareTwitter.setSelected(!llShareTwitter.isSelected());
            if (llShareTwitter.isSelected()) {
                llShareFacebook.setSelected(false);
                llShareInstagram.setSelected(false);
                llShareWhatsApp.setSelected(false);
                llShareEmail.setSelected(false);
                llShareOthers.setSelected(false);
            }
            UiUtils.hideSoftKeyboard(ActivityPostMedia.this);
        });

        llShareWhatsApp.setOnClickListener(view -> {
            llShareWhatsApp.setSelected(!llShareWhatsApp.isSelected());
            if (llShareWhatsApp.isSelected()) {
                llShareFacebook.setSelected(false);
                llShareTwitter.setSelected(false);
                llShareInstagram.setSelected(false);
                llShareEmail.setSelected(false);
                llShareOthers.setSelected(false);
            }
            UiUtils.hideSoftKeyboard(ActivityPostMedia.this);
        });

        llShareEmail.setOnClickListener(view -> {
            llShareEmail.setSelected(!llShareEmail.isSelected());
            if (llShareEmail.isSelected()) {
                llShareFacebook.setSelected(false);
                llShareTwitter.setSelected(false);
                llShareInstagram.setSelected(false);
                llShareWhatsApp.setSelected(false);
                llShareOthers.setSelected(false);

            }
            UiUtils.hideSoftKeyboard(ActivityPostMedia.this);
        });

        llShareInstagram.setOnClickListener(view -> {
            llShareInstagram.setSelected(!llShareInstagram.isSelected());
            if (llShareInstagram.isSelected()) {
                llShareFacebook.setSelected(false);
                llShareTwitter.setSelected(false);
                llShareWhatsApp.setSelected(false);
                llShareEmail.setSelected(false);
                llShareOthers.setSelected(false);

            }
            UiUtils.hideSoftKeyboard(ActivityPostMedia.this);
        });

        llShareOthers.setOnClickListener(view -> {
            llShareOthers.setSelected(!llShareOthers.isSelected());
            if(llShareOthers.isSelected()) {
                llShareFacebook.setSelected(false);
                llShareTwitter.setSelected(false);
                llShareWhatsApp.setSelected(false);
                llShareInstagram.setSelected(false);
                llShareEmail.setSelected(false);
            }
        });
    }

    private void setLimitTextCount() {
        edtPostDescription.setAnchorView((View) edtPostDescription.getParent().getParent());
        edtPostDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String tex = StringUtil.encodeString(s.toString());
                if (tex.length() > Constants.LIMIT_TEXT_LENG_POST) {
                    String des = StringUtil.decodeString(descriptionBefore);
                    edtPostDescription.setText(des);
                    int selection = TextUtils.isEmpty(des) ? 0 : des.length() - 1;
                    edtPostDescription.setSelection(selection);
                } else {
                    descriptionBefore = tex;
                }
                int newLength = descriptionBefore.length();
                onDescriptionLengthChanged(newLength);
                onCounterChanged(newLength);
                handlePostButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setOnclickListener() {
        btnPost.setOnClickListener(this);
    }

    private void onDescriptionLengthChanged(int length) {
        if (length > 0) {
            edtPostDescription.setCustomFont(getBaseContext(), getString(R.string.font_opensansregular));
        } else {
            edtPostDescription.setCustomFont(getBaseContext(), getString(R.string.font_opensansemibold));
        }
    }

    private void onCounterChanged(int length) {
        String counter = String.valueOf(length + "/" + Constants.LIMIT_TEXT_LENG_POST);
        txtNumberText.setText(counter);
    }

    void handlePostButton() {
        boolean shouldEnable = false;

        postDescription = edtPostDescription.getText().toString().trim();
        if (type == CommonDefine.TYPE_QUOTES && !TextUtils.isEmpty(edtPostDescription.getText())) {
            shouldEnable = true;
        } else if (type == CommonDefine.TYPE_IMAGE && (bitmapSend != null || isEditMode)) {
            shouldEnable = true;
        } else if (type == CommonDefine.TYPE_VIDEO && ((!TextUtils.isEmpty(pathVideoCamera) && imageThumbnailVideo != null) || isEditMode)) {
            shouldEnable = true;
        }

        if (shouldEnable) {
            btnPost.setEnabled(true);
        } else {
            btnPost.setEnabled(false);
        }
    }

    private void getDataSend() {
        if (bundleMedia != null) {

            type = bundleMedia.getType();

            if (type == CommonDefine.TYPE_QUOTES) {
                fmMedia.setVisibility(View.GONE);
                pageTitle.setText(getString(R.string.post_quotes));
                llShareWhatsApp.setVisibility(View.GONE);
                vShareInstagramDivider.setVisibility(View.GONE);
            }

            if (type == CommonDefine.TYPE_VIDEO) {
                pageTitle.setText(getString(R.string.post_video));

                if (bundleMedia.getPostId() == null) {
                    isEditMode = true;
                    uriData = Uri.parse(bundleMedia.getUriPath());

                    pathMedia = getRealPathFromURI(uriData, this);
                    pathVideoCamera = pathMedia;
                    imageThumbnailVideo = VideoUtil.createVideoThumbnail(pathMedia);
//                    Log.e("imageThumbnailVideo", imageThumbnailVideo + "");
                    if (imageThumbnailVideo != null) {
                        imageVideos.setImageBitmap(Bitmap.createScaledBitmap(imageThumbnailVideo,
                                Constants.BITMAP_THUMBNAIL_WIDTH, Constants.BITMAP_THUMBNAIL_HEIGHT, false));
                    }

                } else {
                    isEditMode = true;
                    showBitmapEditSharing();
                }
                postPlayVideo.setVisibility(View.VISIBLE);
            } else if (type == CommonDefine.TYPE_IMAGE) {
                pageTitle.setText(getString(R.string.post_photo));

                if (StringUtil.isNullOrEmptyString(bundleMedia.getPostId())) {
                    onImageChanged(bundleMedia);
                } else {
                    isEditMode = true;
                    showBitmapEditSharing();
                }

                postPlayVideo.setVisibility(View.GONE);
            }
            if (bundleMedia.getPostId() != null) {
                if (bundleMedia.getDiscription() != null) {
                    edtPostDescription.setTextAndFormatText(StringUtil.decodeString(bundleMedia.getDiscription()));
                    int leng = edtPostDescription.getText().length();
                    edtPostDescription.setSelection(leng);
                }

                address = bundleMedia.getItemModelClassNewsFeed().getAddress();
                if (!StringUtil.isNullOrEmptyString(address)) {
                    txtCheckIn.setText(bundleMedia.getItemModelClassNewsFeed().getAddress());
                    cancelTextIv.setVisibility(View.VISIBLE);
                }
            }
        } else {
            type = 0;
            fmMedia.setVisibility(View.GONE);
            llShareInstagram.setVisibility(View.GONE);
            vShareInstagramDivider.setVisibility(View.GONE);
            pageTitle.setText(getString(R.string.post_quotes));
        }

        descriptionBefore = edtPostDescription.getText().toString();
        String counter = String.valueOf(StringUtil.encodeString(descriptionBefore).length() + "/" + Constants.LIMIT_TEXT_LENG_POST);
        txtNumberText.setText(counter);
    }

    private void onImageChanged(BundleMedia bundleMedia){
        uriImageSend = Uri.parse(bundleMedia.getUriPath());
        bitmapSend = Utils.getBitmapFromURi(ActivityPostMedia.this, uriImageSend);
        if (bitmapSend != null) {
            imageVideos.setImageBitmap(Bitmap.createScaledBitmap(bitmapSend,
                    Constants.BITMAP_THUMBNAIL_WIDTH, Constants.BITMAP_THUMBNAIL_HEIGHT, false));
        }
    }

    private String removeBlank(String message) {

        message = message.replaceAll("(\\s)+", "$1");

        return message;
    }

    private double mCurrentLat = UNKNOWN_LOCATION;
    private double mCurrentLon = UNKNOWN_LOCATION;


    public void onClickCheckLocation(View view) {

        mCompositeSubscription.add(mRxPermissions.request(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        // Get Location
                        GPSTClass gpstClass = GPSTClass.getInstance();
                        gpstClass.getLocation(this);
                        // check if GPS enabled
                        if (!gpstClass.canGetLocation()) {
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
                            showSettingsAlert();
                            return;
                        }

                        mCurrentLat = gpstClass.getLatitude();
                        mCurrentLon = gpstClass.getLongitude();

                        address = GetAddress.getAddress(ActivityPostMedia.this, mCurrentLat, mCurrentLon);

                        if (StringUtil.isNullOrEmptyString(address)) {
                            return;
                        }

                        cancelTextIv.setVisibility(View.VISIBLE);
                        txtCheckIn.setText(address);
                        txtCheckIn.setCustomFont(this, getString(R.string.font_opensansregular));
                    }
                }));
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingsAlert() {

        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title("GPS not enabled")
                .message("Do you want to go to settings menu?")
                .confirmText(getString(R.string.setting_slider))
                .singleAction(false)
                .onConfirmClicked(() -> {
                    Intent intent1 = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent1);
                })
                .build().show(this);
    }

    public void post() {
        postDescription = edtPostDescription.getText().toString().trim();
        if (type == CommonDefine.TYPE_QUOTES) {
            if (StringUtil.isNullOrEmptyString(postDescription)) {
                utility.showMessage(getString(R.string.app_name),
                        getString(R.string.please_enter_description),
                        ActivityPostMedia.this);

                return;
            }
        } else if (type == CommonDefine.TYPE_IMAGE) {
            if (bitmapSend == null) {
                return;
            }
        } else if (type == CommonDefine.TYPE_VIDEO) {
            if (StringUtil.isNullOrEmptyString(pathVideoCamera) || imageThumbnailVideo == null) {
                return;
            }
        }

        if (!CheckNetwork.isNetworkAvailable(ActivityPostMedia.this)) {
            utility.showMessage(
                    getString(R.string.app_name),
                    getResources().getString(
                            R.string.no_internet_connection),
                    ActivityPostMedia.this);
            return;
        }

        postDescription = removeBlank(postDescription);
        postDescription = StringUtil.encodeString(postDescription);
        btnPost.setClickable(false);

        PostCreatePostRequestModel request;
        if (type == CommonDefine.TYPE_IMAGE) {
            request = new PostCreatePostRequestModel(address,
                    mCurrentLat, mCurrentLon, postDescription, type, Utils.getFileFromBitMap(this, bitmapSend));

        } else if (type == CommonDefine.TYPE_VIDEO) {
            File file = new File(pathVideoCamera);
            request = new PostCreatePostRequestModel(address,
                    mCurrentLat, mCurrentLon, postDescription, type, Utils.getFileFromBitMap(this, imageThumbnailVideo), file);
        } else {
            request = new PostCreatePostRequestModel(address,
                    mCurrentLat, mCurrentLon, postDescription, type);
        }
        request.setTaggedUsers(edtPostDescription.getStringTaggedUsersIdAndClear());

        mPresenter.post(request.build());
    }

    private void onPostError(String message, int code) {
        if (DialogManager.isShowing()) {
            DialogManager.getInstance().dismisDialog();
        }
        btnPost.setClickable(true);
        handleError(message, code);
    }

    public void onBackClick(View view) {

        confirmBackPressed();

    }

    @Override
    public void onBackPressed() {

        confirmBackPressed();

    }

    private void confirmBackPressed() {

        boolean needShowConfirm = false;

        if (bundleMedia != null) {

            if (bundleMedia.isPost()) {

                if (imageThumbnailVideo != null || bitmapSend != null) {
                    needShowConfirm = true;
                }
            }
        } else {

            if (type == CommonDefine.TYPE_QUOTES) {

                String description = edtPostDescription.getText().toString().trim();

                if (!StringUtil.isNullOrEmptyString(description)) {
                    needShowConfirm = true;
                }

            }
        }

        if (!needShowConfirm) {
            exitPost();
            return;
        }

        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        DialogbeLiveConfirmation confirmation = new DialogbeLiveConfirmation(builder);
        builder.title(getString(R.string.app_name))
                .message(getString(R.string.post_discard_current_post))
                .confirmText(getString(R.string.post_btn_Discard))
                .singleAction(false)
                .onConfirmClicked(this::exitPost)
                .build().show(this);

    }

    private void exitPost() {

        if (imageThumbnailVideo != null && !imageThumbnailVideo.isRecycled()) {
            imageThumbnailVideo.recycle();
            imageThumbnailVideo = null;
        }

        if (bitmapSend != null && !bitmapSend.isRecycled()) {
            bitmapSend.recycle();
            bitmapSend = null;
        }

        Utils.hideSoftKeyboard(this);
        setResult(RESULT_CANCELED);

        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {

            if (requestCode == Constants.REQUEST_CODE_SHARE_FEED) {
                checkShareFeed();
            }

            return;
        }

        Uri imageCroppedURI;

        switch (requestCode) {

            case Constants.REQUEST_PIC_FROM_CROP:
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    bundleMedia.setUriPath(resultUri.toString());
                    onImageChanged(bundleMedia);
                } else {
                    Toast.makeText(this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
                }
                break;

            case CROP_PIC:

                if (data == null) {
                    break;
                }

                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                bitmapSend = extras.getParcelable("data");
                imageVideos.setImageBitmap(Bitmap.createScaledBitmap(bitmapSend,
                        Constants.BITMAP_THUMBNAIL_WIDTH, Constants.BITMAP_THUMBNAIL_HEIGHT, false));
                break;
            case ConstantBundleKey.SHARE_INSTAGRAM_REQUEST:
                setResult(RESULT_OK);
                finish();
                break;

            case Constants.REQUEST_PIC_FROM_LIBRARY:
                if (data != null) {
                    uriImageSend = data.getData();
                    if (uriImageSend != null) {
                        imageCroppedURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE_CROPPED);
                        performCrop(uriImageSend, imageCroppedURI);
                    }
                }

                break;

            case Constants.REQUEST_PIC_FROM_CAMERA:
                uriImageSend = data.getData();
                if (uriImageSend != null) {
                    bundleMedia.setUriPath(uriImageSend.toString());
                    onImageChanged(bundleMedia);
                }

                break;

            case Constants.INSTAGRAM_SHARE_RETURN:

                break;


            case Constants.REQUEST_CODE_SHARE_FEED:

                checkShareFeed();

                break;

        }

        if (llShareFacebook.isSelected() || llShareInstagram.isSelected()) {
            SocialManager.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }


    void checkShareFeed() {
        if (llShareFacebook.isSelected()) {
            llShareFacebook.setSelected(false);
            SocialManager.getInstance().shareFacebookPopup(this, itemModelClassNewsFeed.getMediaType(), bundleMedia == null ? null : Uri.parse(bundleMedia.getUriPath()), itemModelClassNewsFeed.getWebPostUrl(), bitmapSend);
//            releaseMemory(ActivityPostMedia.this);
//            if (type != CommonDefine.TYPE_QUOTES) {
//                if (bitmapSend != null) {
//                    SocialManager.getInstance().setBitmapSend(bitmapSend);
//                }
//                SocialManager.getInstance().loginForSharing(ActivityPostMedia.this, type, Uri.parse(bundleMedia.getUriPath()), edtPostDescription.getText().toString());
//
//            } else {
//                SocialManager.getInstance().loginForSharing(ActivityPostMedia.this, type, null, edtPostDescription.getText().toString());
//            }

        } else if (llShareInstagram.isSelected()) {
            if (SocialManager.getInstance().hasSetupInstagram(this)) {
                llShareInstagram.setSelected(false);
                SocialManager.getInstance().shareFeedToInstagram(this, itemModelClassNewsFeed);
            } else {
                releaseMemory(ActivityPostMedia.this);
            }

        } else if (llShareWhatsApp.isSelected()) {
            if (SocialManager.getInstance().hasSetupWhatsapp(this)) {
                llShareWhatsApp.setSelected(false);
                SocialManager.getInstance().shareFeedToWhatsapp(this, itemModelClassNewsFeed, false);
            } else {
                releaseMemory(ActivityPostMedia.this);
            }
        } else if (llShareEmail.isSelected()) {
            if (SocialManager.getInstance().hasSetupEmail(this)) {
                llShareEmail.setSelected(false);
                SocialManager.getInstance().shareFeedToShareAction(true,this, itemModelClassNewsFeed, false);
            } else {
                releaseMemory(ActivityPostMedia.this);
            }

        } else if (llShareTwitter.isSelected()) {
            if (SocialManager.getInstance().hasSetupTwitter(this)) {
                llShareTwitter.setSelected(false);
                SocialManager.getInstance().shareFeedToTwitter(this, itemModelClassNewsFeed, false);
                releaseMemory(ActivityPostMedia.this);
            } else {
                releaseMemory(ActivityPostMedia.this);
            }
        } else if (llShareOthers.isSelected()) {
            llShareOthers.setSelected(false);
            SocialManager.getInstance().shareFeedToShareAction(false,this, itemModelClassNewsFeed, false);
            releaseMemory(ActivityPostMedia.this);

        } else {
            releaseMemory(ActivityPostMedia.this);
        }
    }

    @Override
    public void onClick(View view) {
        if (preventMultiClicks()) {
            return;
        }
        switch (view.getId()) {

            case R.id.btn_post:

                if (bundleMedia == null) {
                    post();
                    return;
                }

                try {
                    if (bundleMedia.getPostId() == null) {
                        post();
                    } else {
                        sendEditPost();
                    }
                } catch (NullPointerException error) {
//                    post(view);
                }


                break;

            case R.id.ll_root:
                UiUtils.hideSoftKeyboard(this);
                break;
        }


    }

    @Override
    public void onStartSharing(SocialManager.TypeShare typeShare, Context context) {
        showDialog(context, getString(R.string.post_sharing));

        SocialManager.getInstance().isComepleteSharing = false;
    }

    @Override
    public void onErrorSharing(SocialManager.TypeShare typeShare, Context context, String message) {
        dismisDialog();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        btnPost.setClickable(true);

        checkShareFeed();

    }

    @Override
    public void onCompleteSharing(SocialManager.TypeShare typeShare, Context context, String message) {
        dismisDialog();
        SocialManager.getInstance().isComepleteSharing = true;
        isCompletedSharing = true;
        SocialManager.getInstance().setBitmapSend(null);
        SocialManager.getInstance().socialSharingListener = null;

        Toast.makeText(getApplicationContext(), getString(R.string.post_share_facebook_success), Toast.LENGTH_SHORT).show();

        checkShareFeed();

    }

    void releaseMemory(Context context) {

        if (imageThumbnailVideo != null && !imageThumbnailVideo.isRecycled()) {
            imageThumbnailVideo.recycle();
            imageThumbnailVideo = null;
        }

        if (bitmapSend != null && !bitmapSend.isRecycled()) {
            bitmapSend.recycle();
            bitmapSend = null;
        }

        Intent intent = new Intent();
        if (bundleMedia == null) {
            ((Activity) context).setResult(RESULT_OK);

        } else if (StringUtil.isNullOrEmptyString(bundleMedia.getPostId())) {
            ((Activity) context).setResult(RESULT_OK);

        } else if (!StringUtil.isNullOrEmptyString(bundleMedia.getPostId())) {
            intent.putExtra(ConstantBundleKey.BUNDLE_TITLE_EDIT_POST, StringUtil.encodeString(edtPostDescription.getText().toString()));
            intent.putExtra(ConstantBundleKey.BUNDLE_ADDRESS_EDIT_POST, address);
            intent.putExtra(ConstantBundleKey.BUNDLE_POSITION_EDIT_POST, bundleMedia.getPosition());
            intent.putExtra(ConstantBundleKey.BUNDLE_ID_EDIT_POST, bundleMedia.getPostId());
        }

        ((Activity) context).setResult(RESULT_OK, intent);
        EventBus.getDefault().post(new EventBusRefreshFragment(type));
        SocialManager.getInstance().context = null;
        finish();
    }

    @Override
    public void onNotLoginForSharing() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        AppsterUtility.goToActivity(this, "LoginActivity", null);
    }

    @Override
    public void onStartingAuthentication() {

    }

    @Override
    public void onLoginFail(String message) {

    }

    @Override
    public void onAuthentSuccess() {
        AppsterApplication.mAppPreferences.setLoginFacebook(true);
//        typeShare = -1;

    }


    @Override
    public void onCompleteLogin() {
    }


    @Override
    public void loginWithFacebookInfo(LoginFacebookRequestModel requestLogin) {

    }

    void disableClickSharing() {
        if (bundleMedia == null && type == CommonDefine.TYPE_QUOTES) {
            llShareInstagram.setEnabled(false);

        } else if (bundleMedia != null && bundleMedia.getType() == CommonDefine.TYPE_QUOTES) {
            llShareInstagram.setEnabled(false);
        }
    }

    void showBitmapEditSharing() {

        if (bundleMedia.getUriPath().contains("http")) {

            ImageLoaderUtil.displayMediaImage(this, bundleMedia.getUriPath(), imageVideos);
        }
    }

    EditPostRequestModel getEditPostRequestModel() {
        int leng = edtPostDescription.getText().toString().length();
        EditPostRequestModel model = new EditPostRequestModel();
        if (!TextUtils.isEmpty(address)) model.setAddress(address);
        if (mCurrentLat != UNKNOWN_LOCATION && mCurrentLon != UNKNOWN_LOCATION) {
            model.setLatitude(mCurrentLat);
            model.setLongitude(mCurrentLon);
        }

        if (leng > 0) {
            model.setTitle(StringUtil.encodeString(edtPostDescription.getText().toString()));
            model.setTaggedUsers(edtPostDescription.getTaggedUsersId());
        } else
            model.setTitle("");
        model.setPost_id(Integer.parseInt(bundleMedia.getPostId()));
        return model;
    }

    void sendEditPost() {
        showDialog(this, getString(R.string.post_updating));
        mPresenter.editPost(getEditPostRequestModel());
    }
}
