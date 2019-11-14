package com.appster.features.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.main.MainActivity;
import com.appster.utility.ConstantBundleKey;
import com.appster.webservice.request_models.EditProfileRequestModel;
import com.apster.common.DialogManager;
import com.apster.common.Utils;
import com.pack.utility.CheckNetwork;
import com.pack.utility.EmailUtil;
import com.pack.utility.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 2/26/2016.
 */
public class InputUserEmailActivity extends BaseActivity {

    @Bind(R.id.pageTitle)
    TextView pageTitle;
    @Bind(R.id.inputUserOrEmail)
    EditText inputUserOrEmail;
    @Bind(R.id.inputUserOrEmail_inval)
    TextView inputUserOrEmailInval;
    @Bind(R.id.btn_next)
    Button btnNext;
    @Bind(R.id.skip)
    TextView skip;
    @Bind(R.id.inputReferer)
    EditText inputReferer;

    String email = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_email);
        ButterKnife.bind(this);

        inti();

        getDataBundle();
    }

    private void getDataBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra(ConstantBundleKey.BUNDLE_LOGIN_EMAIL);
            inputUserOrEmail.setText(email);
        }
    }

    private void inti() {
        inputUserOrEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputReferer.setVisibility(View.GONE);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputUserOrEmail.getText().toString();

                if (StringUtil.isNullOrEmptyString(email)) {

                    inputUserOrEmailInval.setText(R.string.create_profile_please_input_email);
                    inputUserOrEmailInval.setVisibility(View.VISIBLE);

                } else {

                    EmailUtil checkEmail = new EmailUtil();
                    if (!checkEmail.isEmail(email)) {
                        inputUserOrEmailInval.setText(R.string.invalid_email);
                        inputUserOrEmailInval.setVisibility(View.VISIBLE);

                        return;
                    }

                    updateProfile();
                }

            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goingHomeScreen();
            }
        });

        inputUserOrEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                inputUserOrEmailInval.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateProfile() {

        if (!CheckNetwork.isNetworkAvailable(InputUserEmailActivity.this)) {
            utility.showMessage("", getString(R.string.no_internet_connection), InputUserEmailActivity.this);
            return;
        }

        DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));

        EditProfileRequestModel request = new EditProfileRequestModel("", "", "", email, "",
                "", "", null, "", "", AppsterApplication.mAppPreferences.getDevicesUDID());

//        AppsterWebServices.get().editProfile("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), "close", request,
//                new Callback<BaseResponse<EditProfileResponseModel>>() {
//                    @Override
//                    public void success(BaseResponse<EditProfileResponseModel> repostResponseModel, Response response) {
//
//                        DialogManager.getInstance().dismisDialog();
//                        if (repostResponseModel == null) return;
//                        if (repostResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
//                            AppsterApplication.mAppPreferences.getUserModel().setEmail(email);
//
//                            goingHomeScreen();
//
//                        } else {
//
//                            handleError(repostResponseModel.getMessage(), repostResponseModel.getCode());
//                        }
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        DialogManager.getInstance().dismisDialog();
//                        handleError(error.getMessage(), Constants.RETROFIT_ERROR);
//                    }
//                });
    }

    @Override
    public void onBackPressed() {

        goingHomeScreen();
    }

    public void goingHomeScreen() {

        Utils.hideSoftKeyboard(this);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(InputUserEmailActivity.this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(InputUserEmailActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent, options.toBundle());

        finish();
    }
}
