package com.data.repository;

import com.appster.domain.PhoneVerification;
import com.appster.webservice.request_models.PhoneLoginForgotPasswordRequestModel;
import com.appster.webservice.request_models.PhoneLoginRequestModel;
import com.appster.webservice.request_models.PhoneLoginResetPasswordRequest;
import com.appster.webservice.request_models.PhoneVerificationRequest;
import com.appster.webservice.request_models.PhoneVerifyVerificationCodeRequest;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.LoginResponseModel;
import com.appster.webservice.response.PhoneLoginForgotPasswordResponse;
import com.data.repository.datasource.cloud.CloudForgotPasswordDataSource;
import com.data.repository.datasource.cloud.CloudLoginDataSource;
import com.data.repository.datasource.cloud.CloudPhoneSignInResetPasswordDataSource;
import com.data.repository.datasource.cloud.CloudVerifyPhoneNumberDataSource;
import com.data.repository.datasource.cloud.CloudPhoneVerifyVerificationCodeDataSource;
import com.data.repository.datasource.cloud.CloudRequestPhoneVerificationCodeDataSource;

import rx.Observable;

/**
 * Created by linh on 25/10/2017.
 */

public class PhoneLoginRepository {
    private final CloudVerifyPhoneNumberDataSource mCloudVerifyPhoneNumberDataSource;
    private final CloudRequestPhoneVerificationCodeDataSource mDataSource;
    private final CloudPhoneVerifyVerificationCodeDataSource mCloudPhoneVerifyVerificationCodeDataSource;
    private final CloudLoginDataSource mCloudLoginDataSource;
    private final CloudForgotPasswordDataSource mCloudForgotPasswordDataSource;
    private final CloudPhoneSignInResetPasswordDataSource mCloudPhoneSignInResetPasswordDataSource;

    public PhoneLoginRepository(CloudVerifyPhoneNumberDataSource dataSource, CloudRequestPhoneVerificationCodeDataSource dataSource1, CloudPhoneVerifyVerificationCodeDataSource cloudPhoneVerifyVerificationCodeDataSource, CloudLoginDataSource cloudLoginDataSource, CloudForgotPasswordDataSource cloudForgotPasswordDataSource, CloudPhoneSignInResetPasswordDataSource cloudPhoneSignInResetPasswordDataSource) {
        mCloudVerifyPhoneNumberDataSource = dataSource;
        mDataSource = dataSource1;
        mCloudPhoneVerifyVerificationCodeDataSource = cloudPhoneVerifyVerificationCodeDataSource;
        mCloudLoginDataSource = cloudLoginDataSource;
        mCloudForgotPasswordDataSource = cloudForgotPasswordDataSource;
        mCloudPhoneSignInResetPasswordDataSource = cloudPhoneSignInResetPasswordDataSource;
    }

    public Observable<BaseResponse<PhoneVerification>> verifyPhoneNumber(PhoneVerificationRequest request) {
        return mCloudVerifyPhoneNumberDataSource.verifyPhoneNumber(request);
    }

    public Observable<BaseResponse<Boolean>> verifyVerificationCde(PhoneVerifyVerificationCodeRequest request){
        return mCloudPhoneVerifyVerificationCodeDataSource.verify(request);
    }

    public Observable<BaseResponse<Boolean>> requestVerificationCode(String otp){
        return mDataSource.requestVerificationCode(otp);
    }

    public Observable<BaseResponse<LoginResponseModel>> loginWithPhoneNumber(PhoneLoginRequestModel request){
        return mCloudLoginDataSource.loginWithPhoneNumber(request);
    }

    public Observable<BaseResponse<PhoneLoginForgotPasswordResponse>> forgotPassword(PhoneLoginForgotPasswordRequestModel request) {
        return mCloudForgotPasswordDataSource.forgotPassword(request);
    }

    public Observable<BaseResponse<Boolean>> resetPassword(PhoneLoginResetPasswordRequest request) {
        return mCloudPhoneSignInResetPasswordDataSource.resetPassword(request);
    }
}
