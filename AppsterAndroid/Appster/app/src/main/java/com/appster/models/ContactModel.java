package com.appster.models;

import com.appster.core.adapter.DisplayableItem;
import com.appster.features.login.phoneLogin.countrypicker.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by thanhbc on 12/22/17.
 */

public class ContactModel implements DisplayableItem {

    public long id;

    public String displayName;
    public List<PhoneNumber> phoneNumbers;
    public String photoUri;
    public String rawData;
    public String birthDay;
    public String email;
    public String address;

//    public ContactModel(Contact contact) {
//
//    }

    private ContactModel(Builder builder) {
        id = builder.id;
        displayName = builder.displayName;
        phoneNumbers = builder.phoneNumbers;
        photoUri = builder.photoUri;
        rawData = builder.rawData;
        birthDay = builder.birthDay;
        email = builder.email;
        address = builder.address;
    }


    public String getFirstNomalizedPhoneNum() {
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            return phoneNumbers.get(0).normalizedNumber;
        }
        return null;
    }

    public String getFirstPhoneNumWithoutCountryCode() {
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            return phoneNumbers.get(0).number;
        }
        return null;
    }

    public String getFirstPhoneNumCountryCode() {
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            return phoneNumbers.get(0).countryCode;
        }
        return null;
    }

    private static class PhoneNumber {
        public String number;
        public String normalizedNumber;
        public String countryCode;

        @Override
        public String toString() {
            return "PhoneNumber{" +
                    "number='" + number + '\'' +
                    ", normalizedNumber='" + normalizedNumber + '\'' +
                    ", countryCode='" + countryCode + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ContactModel{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", phoneNumbers=" + phoneNumbers +
                ", photoUri='" + photoUri + '\'' +
                ", rawData='" + rawData + '\'' +
                ", birthDay='" + birthDay + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public static final class Builder {
        private long id;
        private String displayName;
        private List<PhoneNumber> phoneNumbers;
        private String photoUri;
        private String rawData;
        private String birthDay;
        private String email;
        private String address;

        public Builder() {
        }

        private List<PhoneNumber> parsePhoneNumbers(List<com.contacts.PhoneNumber> phoneNumbers) {
            List<PhoneNumber> numbers = null;
            if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
                numbers = new ArrayList<>();
                for (com.contacts.PhoneNumber phoneNumber : phoneNumbers) {
                    PhoneNumber number = new PhoneNumber();
                    String normalizedNumber = phoneNumber.getNormalizedNumber();
                    if(normalizedNumber==null) normalizedNumber = phoneNumber.getNumber();
                    number.number = Utils.getPhoneNumWithoutCountryCode(normalizedNumber);
                    number.normalizedNumber = normalizedNumber;
                    number.countryCode = Utils.getCountryCodeByPhoneNum(normalizedNumber);
                    numbers.add(number);
                }
            }
            return numbers != null ? numbers : Collections.EMPTY_LIST;
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder displayName(String val) {
            displayName = val;
            return this;
        }

        public Builder phoneNumbers(List<com.contacts.PhoneNumber> val) {
            phoneNumbers = parsePhoneNumbers(val);
            return this;
        }

        public Builder photoUri(String val) {
            photoUri = val;
            return this;
        }

        public Builder rawData(String val) {
            rawData = val;
            return this;
        }

        public Builder birthDay(String val) {
            birthDay = val;
            return this;
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public ContactModel build() {
            return new ContactModel(this);
        }


    }
}
