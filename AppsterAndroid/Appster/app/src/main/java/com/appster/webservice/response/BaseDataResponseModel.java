package com.appster.webservice.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 10/12/2015.
 */
public class BaseDataResponseModel {
    @SerializedName("error") @Expose
    private int mError;
    @SerializedName("errorCode") @Expose
    private int mErrorCode;
    @SerializedName("errorMessage") @Expose
    private String mErrorMessage;
    @SerializedName("Message") @Expose
    private String mMessage;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public int getError() {
        return mError;
    }

    public void setError(int error) {
        this.mError = error;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int errorCode) {
        this.mErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.mErrorMessage = errorMessage;
    }


    public String toString(){
        return  new Gson().toJson(this);
    }
}
