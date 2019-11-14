package com.appster.features.edit_profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.customview.CircleImageView;
import com.appster.models.SettingResponse;
import com.appster.models.UserModel;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DialogUtil;
import com.appster.utility.ImageLoaderUtil;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.EditProfileRequestModel;
import com.apster.common.Constants;
import com.apster.common.DateHelper;
import com.apster.common.DialogManager;
import com.apster.common.UiUtils;
import com.apster.common.Utils;
import com.pack.utility.CheckNetwork;
import com.pack.utility.SetDateTime;
import com.pack.utility.StringUtil;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.appster.AppsterApplication.mAppPreferences;
import static com.apster.common.FileUtility.MEDIA_TYPE_IMAGE_CROPPED;

public class ActivityEditProfile extends BaseActivity implements OnClickListener, EditProfileContract.EditProfileView {
    private final int MAX_DISPLAY_NAME_LENGTH = 16;
    private final int MAX_ID_NAME_LENGTH = 16;
    private final int MAX_STATUS_LENGTH = 80;

    @Bind(R.id.upload_image)
    CircleImageView uploadImage;
    @Bind(R.id.user_name_et)
    EditText edtIdName;

    @Bind(R.id.edit_dob)
    TextView editDob;

    @Bind(R.id.btn_right_toolbar)
    Button btnDone;
    @Bind(R.id.display_name)
    EditText edtDisplayName;
    @Bind(R.id.edt_status)
    EditText edtStatus;

    @Bind(R.id.txt_display_name_letter_count)
    TextView txtDisplayNameLetterCount;
    @Bind(R.id.txt_id_name_letter_count)
    TextView txtIdNameLetterCount;
    @Bind(R.id.txt_status_letter_count)
    TextView txtStatusLetterCount;

    @Bind(R.id.ll_main_content)
    LinearLayout llMainContent;
    @Bind(R.id.img_gender_male)
    ImageView imgGenderMale;
    @Bind(R.id.img_gender_female)
    ImageView imgGenderFemale;

    private Bitmap userAvatar;

    private String gender = ""; //Default will be not highlighted.
    private String displayName = "";
    private String dateOfBirth = "";
    private String nationality = "";
    private String email;

    private UserModel userInforModel;

    private String oldDisplayName = "";
    private String About = "";
    private String oldAbout = "";
    private String oldGender = "";
    private boolean isChangeUserImage = false;
    private boolean isChooseImage = false;

    private EditProfileContract.UserActions presenter;

    //=============== inherited methods ============================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_belive);
        ButterKnife.bind(this);

        intId();
        setTextChanged();
        presenter = new EditProfilePresenter(this, AppsterWebServices.get());
        if (mBeLiveThemeHelper != null && mBeLiveThemeHelper.isTransparentStatusBarRequired()) {
            Window window = getWindow();
            if (window != null) window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        Uri imageCroppedURI;
        try {
            imageCroppedURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE_CROPPED);
        } catch (NullPointerException e) {
            Timber.d(e);
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_PIC_FROM_LIBRARY:
                fileUri = data.getData();
                if (fileUri == null) {
                    return;
                }
                performCrop(fileUri, imageCroppedURI);
                break;
            case Constants.REQUEST_PIC_FROM_CAMERA:
                fileUri = data.getData();
                if (fileUri == null) {
                    return;
                }
                handleImageProfile(fileUri);
                isChooseImage = true;
                break;
            case Constants.REQUEST_PIC_FROM_CROP:
                handleImageProfile(imageCroppedURI);
                isChooseImage = true;
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        Utils.hideSoftKeyboard(this);
        UserModel userModel = mAppPreferences.getUserModel();
        if (isChangeUserImage) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(ConstantBundleKey.BUNDLE_CHANGE_PROFILE_IMAGE, true);
            setResult(RESULT_OK, resultIntent);
        } else if (!oldDisplayName.equals(userModel.getDisplayName()) || !oldAbout.equals(userModel.getAbout())
                || !oldGender.equalsIgnoreCase(gender)) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(ConstantBundleKey.BUNDLE_CHANGE_PROFILE_DISPLAY_NAME, true);
            setResult(RESULT_OK, resultIntent);
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        if (userAvatar != null && !userAvatar.isRecycled()) {
            userAvatar.recycle();
            userAvatar = null;
        }
        presenter.detachView();
        super.onDestroy();
    }

    //================ implemented methods =========================================================
    @Override
    public void onUpdateCompleted(SettingResponse userInfo) {
        // Fix issues Cache Image
        userInforModel.increaseAvatarVersion();
        userInforModel.setDisplayName(userInfo.getDisplayName());
        userInforModel.setEmail(userInfo.getEmail());
        userInforModel.setUserImage(userInfo.getUserImage());
        userInforModel.setGender(userInfo.getGender());
        userInforModel.setDoB(userInfo.getDoB());
        userInforModel.setNationality(userInfo.getNationality());
        userInforModel.setEmailVerified(userInfo.getEmailVerified());
        userInforModel.setStatus(userInfo.getStatus());
        userInforModel.setRefId(String.valueOf(userInfo.getRefId()));
        userInforModel.setReferralId(String.valueOf(userInfo.getReferralId()));
        userInforModel.setAbout(userInfo.getAbout());
        userInforModel.setWebProfileUrl(userInfo.getWebProfileUrl());

        mAppPreferences.saveUserInforModel(userInforModel);

        if (isChooseImage) {
            isChangeUserImage = true;
            //change cached time
//            mAppPreferences.setCachedTime(System.currentTimeMillis());
        }
        Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {
        handleError(errorMessage, code);
    }

    @Override
    public void showProgress() {
        DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));
    }

    @Override
    public void hideProgress() {
        DialogManager.getInstance().dismisDialog();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.upload_image:

                showPicPopUp();

                break;

            case R.id.btn_right_toolbar:

                checkData();

                break;

            case R.id.ll_main_content:
                UiUtils.hideSoftKeyboard(this);
                break;

            case R.id.img_gender_male:
                gender = Constants.GENDER_MALE;
                displayGender();
                break;
            case R.id.img_gender_female:
                gender = Constants.GENDER_FEMALE;
                displayGender();
                break;
        }
    }

    //============= inner methods ==================================================================
    private void intId() {
        userInforModel = mAppPreferences.getUserModel();

        dateOfBirth = SetDateTime.getDOB(userInforModel.getDoB(), this);
        gender = userInforModel.getGender();
        email = userInforModel.getEmail();
        nationality = userInforModel.getNationality();
        oldDisplayName = userInforModel.getDisplayName();
        oldAbout = userInforModel.getAbout();
        oldGender = gender;

        // Set Info
        edtIdName.setText(userInforModel.getUserName());
        edtIdName.setKeyListener(null);

        edtDisplayName.setText(userInforModel.getDisplayName());
        if (!TextUtils.isEmpty(dateOfBirth))
            editDob.setText(AppsterUtility.convertDateTime(dateOfBirth));
        edtStatus.setText(StringUtil.decodeString(userInforModel.getAbout()));
        // Image user
        if(!StringUtil.isNullOrEmptyString(userInforModel.getUserImage())) {
            ImageLoaderUtil.displayUserImage(this, userInforModel.getUserImage(), uploadImage);
        }

        uploadImage.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        llMainContent.setOnClickListener(this);
        imgGenderMale.setOnClickListener(this);
        imgGenderFemale.setOnClickListener(this);

        final DatePickerDialog.OnDateSetListener mDateListen = (view, year, monthOfYear, dayOfMonth) -> {

            dateOfBirth = Utils.checkDigit(dayOfMonth) + "/" + Utils.checkDigit(monthOfYear + 1) + "/" + year;
            userInforModel.setDoB(dateOfBirth);
            editDob.setText(AppsterUtility.convertDateTime(dateOfBirth));
        };
        editDob.setOnClickListener(v -> {

            if (preventMultiClicks()) {
                return;
            }

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            String dob = SetDateTime.partTimeNews(userInforModel.getDoB(), ActivityEditProfile.this);

            if (!StringUtil.isNullOrEmptyString(dob)) {
                if (dob.contains("/")) {
                    String[] arrDob = dob.split("/");

                    if (arrDob != null && arrDob.length == 3) {

                        year = Integer.parseInt(arrDob[2]);
                        month = Integer.parseInt(arrDob[1]) - 1;
                        day = Integer.parseInt(arrDob[0]);
                    }
                }
            }

            DatePickerDialog mDateDialog = new DatePickerDialog(ActivityEditProfile.this, mDateListen, year, month, day);
            mDateDialog.getDatePicker().setMaxDate(DateHelper.getMaxDateCalendar().getTimeInMillis());
            mDateDialog.getDatePicker().setMinDate(DateHelper.getMinDateCalendar().getTimeInMillis());
            mDateDialog.show();

            editDob.setClickable(true);
        });

        displayIdNameCount();
        displayNameCount();
        displayStatusCount();
        displayGender();
    }

    private void displayGender() {
        switch (gender) {
            case Constants.GENDER_MALE:
                ((ViewGroup) imgGenderMale.getParent()).setSelected(true);
                ((ViewGroup) imgGenderFemale.getParent()).setSelected(false);
                break;
            case Constants.GENDER_FEMALE:
                ((ViewGroup) imgGenderMale.getParent()).setSelected(false);
                ((ViewGroup) imgGenderFemale.getParent()).setSelected(true);
                break;
            default:
                break;
        }
    }

    private void displayNameCount() {
        String count = edtDisplayName.length() + "/" + MAX_DISPLAY_NAME_LENGTH;
        txtDisplayNameLetterCount.setText(count);
    }

    private void displayIdNameCount() {
        String count = edtIdName.length() + "/" + MAX_ID_NAME_LENGTH;
        txtIdNameLetterCount.setText(count);
    }

    private void displayStatusCount() {
        String count = edtStatus.length() + "/" + MAX_STATUS_LENGTH;
        txtStatusLetterCount.setText(count);
    }


    public void onClickBack(View view) {
        onBackPressed();
    }

    private void setTextChanged() {

        edtStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                displayStatusCount();
            }
        });

        edtDisplayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                displayNameCount();
            }
        });

        edtIdName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                displayIdNameCount();
            }
        });
    }

    private void handleImageProfile(Uri imageURI) {
        userAvatar = Utils.getBitmapFromURi(ActivityEditProfile.this, imageURI);
        ByteArrayOutputStream outputs = new ByteArrayOutputStream();
        userAvatar.compress(Bitmap.CompressFormat.PNG, 100, outputs);
        uploadImage.setImageBitmap(userAvatar);
    }

    private void checkData() {

        displayName = edtDisplayName.getText().toString().trim();
        About = edtStatus.getText().toString().trim();

        // check displayName
        if (!StringUtil.isNullOrEmptyString(displayName)) {
            if (displayName.length() > Constants.MAX_DISPLAY_NAME || edtDisplayName.length() < Constants.MIX_DISPLAY_NAME) {
                edtDisplayName.setError(getString(R.string.edit_pro_display_name_validation));
                return;
            }
        } else {
            edtDisplayName.setError(getString(
                    R.string.please_input_display_name));
            return;
        }

        //gender must not unselected
        if (TextUtils.isEmpty(gender) || Constants.GENDER_SECRET.equalsIgnoreCase(gender)) {
            String message = getString(R.string.edit_pro_error_unselected_gender);
            String okButton = getString(R.string.btn_text_ok);
            String title = getString(R.string.app_name);
            DialogUtil.showConfirmDialogSingleAction(this, title, message, okButton, null);
            return;
        }

        if (CheckNetwork.isNetworkAvailable(ActivityEditProfile.this)) {

            updateProfile();

        } else {

            utility.showMessage(getString(R.string.app_name), getString(
                    R.string.no_internet_connection), ActivityEditProfile.this);
        }

    }

    private void updateProfile() {
        String refIdInput = "0";
        if (refIdInput.equals(userInforModel.getRefId())) {
            refIdInput = "0";
        }

        EditProfileRequestModel request = new EditProfileRequestModel(gender, displayName, dateOfBirth, email, nationality,
                "", "", Utils.getFileFromBitMap(this, userAvatar), refIdInput, StringUtil.encodeString(About)
                , AppsterApplication.mAppPreferences.getDevicesUDID());

        presenter.updateProfile(request);
    }
}
