package com.appster.models.mappers;

import com.appster.core.adapter.DisplayableItem;
import com.appster.models.ContactModel;
import com.contacts.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by thanhbc on 12/22/17.
 */

public class ContactModelMapper {
    public List<DisplayableItem> transform(List<Contact> contacts) {
        List<DisplayableItem> contactList = null;
        if (contacts != null && !contacts.isEmpty()) {
            contactList = new ArrayList<>();
            for (Contact contact : contacts) {
                ContactModel contactModel = transform(contact);
                if(contactModel!=null) contactList.add(contactModel);
            }
        }
        return contactList != null ? contactList : Collections.emptyList();
    }

    public ContactModel transform(Contact contact) {
        ContactModel model = null;
        if (contact != null) {
            model = new ContactModel.Builder()
                    .displayName(contact.getDisplayName())
                    .id(contact.getId())
                    .photoUri(contact.getPhotoUri())
                    .rawData(contact.toString())
                    .birthDay(contact.getBirthday() != null ? contact.getBirthday().getStartDate() : "")
                    .address(!contact.getAddresses().isEmpty() ? contact.getAddresses().get(0).getFormattedAddress() : "")
                    .email(!contact.getEmails().isEmpty() ? contact.getEmails().get(0).getAddress() : "")
                    .phoneNumbers(contact.getPhoneNumbers())
                    .build();
        }
        return model;
    }

}
