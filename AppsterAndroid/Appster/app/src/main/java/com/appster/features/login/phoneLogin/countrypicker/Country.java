package com.appster.features.login.phoneLogin.countrypicker;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by GODARD Tuatini on 07/05/15.
 */
public class Country implements Parcelable{
    @SerializedName("name") @Expose
    private String name;
    @SerializedName("code") @Expose
    private String isoCode;
    @SerializedName("dial_code") @Expose
    private String dialingCode;
    private int flag = -1;

    public Country() {

    }

    public Country(String code, String name, String dialCode) {
        this.isoCode = code;
        this.name = name;
        this.dialingCode = dialCode;
    }

    public Country(String code, String name, String dialCode, int flag) {
        this.isoCode = code;
        this.name = name;
        this.dialingCode = dialCode;
        this.flag = flag;
    }

    protected Country(Parcel in) {
        name = in.readString();
        isoCode = in.readString();
        dialingCode = in.readString();
        flag = in.readInt();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getDialingCode() {
        return dialingCode;
    }

    public String getDialingCodeWithoutPlusSign() {
        if (TextUtils.isEmpty(dialingCode)){
            return "";
        }
       return (dialingCode.startsWith("+")) ? dialingCode.substring(1) : dialingCode;
    }

    public void setDialingCode(String dialingCode) {
        this.dialingCode = dialingCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(isoCode);
        dest.writeString(dialingCode);
        dest.writeInt(flag);
    }
}
