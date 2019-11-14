package com.data.entity.mapper;

import com.appster.domain.FriendSuggestionModel;
import com.appster.models.ContactModel;
import com.data.entity.MutualFriendEntity;
import com.data.entity.SocialFriendsNumEntity;
import com.data.entity.requests.ContactRequestEntity;
import com.domain.models.SocialFriendsNumModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanhbc on 12/26/17.
 */

public class SocialFriendsDataMapper {
    public List<FriendSuggestionModel> transform(List<MutualFriendEntity> mutualFriends) {

        List<FriendSuggestionModel> suggestionModels = new ArrayList<>();
        for (MutualFriendEntity mutualFriend : mutualFriends) {
            if(mutualFriend!=null) suggestionModels.add(transform(mutualFriend));
        }
        return suggestionModels;
    }

    public FriendSuggestionModel transform(MutualFriendEntity mutualFriend){
        FriendSuggestionModel model =null;
        if(mutualFriend!=null) {
            model = new FriendSuggestionModel();
            model.setDisplayName(mutualFriend.displayName);
            model.setUserName(mutualFriend.userName);
            model.setUserImage(mutualFriend.userImage);
            model.setIsFollow(mutualFriend.isFollow);
            model.setUserId(mutualFriend.userId);
            model.phoneNumber = mutualFriend.phoneNumber;
            model.normalizedPhone = mutualFriend.normalizedPhone;
            model.setWebProfileUrl(mutualFriend.webProfileUrl);
            model.setGender(mutualFriend.gender);
        }
        return model;
    }

    public List<ContactRequestEntity> transformToRequestModel(List<ContactModel> contacts) {

        List<ContactRequestEntity> requestEntities = new ArrayList<>();
        for (ContactModel contact : contacts) {
                ContactRequestEntity requestEntity = transform(contact);
                if(requestEntity!=null) requestEntities.add(requestEntity);
        }
        return requestEntities;
    }

    public ContactRequestEntity transform(ContactModel contact) {
        ContactRequestEntity requestEntity = null;
        if (contact != null && !contact.getFirstPhoneNumCountryCode().isEmpty()) {
            requestEntity = new ContactRequestEntity();
            requestEntity.id = contact.id;
            requestEntity.address = contact.address;
            requestEntity.birthDay = contact.birthDay;
            requestEntity.rawData = contact.rawData;
            requestEntity.email = contact.email;
            requestEntity.phoneNumber = contact.getFirstPhoneNumWithoutCountryCode();
            requestEntity.countryCode = contact.getFirstPhoneNumCountryCode();
        }
        return requestEntity;
    }

    public SocialFriendsNumModel transform(SocialFriendsNumEntity friendsNumEntity) {
        SocialFriendsNumModel socialFriendsNumModel = null;
        if (friendsNumEntity != null) {
            socialFriendsNumModel = new SocialFriendsNumModel();
            socialFriendsNumModel.contactFriends = friendsNumEntity.contactFriends;
            socialFriendsNumModel.facebookFriends = friendsNumEntity.facebookFriends;
            socialFriendsNumModel.instagramFriends = friendsNumEntity.instagramFriends;
            socialFriendsNumModel.twitterFriends = friendsNumEntity.twitterFriends;
        }
        return socialFriendsNumModel;
    }
}
