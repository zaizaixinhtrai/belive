package com.appster.features.contacts;

import com.appster.AppsterApplication;
import com.appster.domain.FriendSuggestionModel;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.models.ContactModel;
import com.appster.utility.AppsterUtility;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.SetFollowUserRequestModel;
import com.appster.webservice.request_models.SetUnfollowUserRequestModel;
import com.apster.common.Constants;
import com.data.repository.AppConfigsDataRepository;
import com.data.repository.SocialFriendsDataRepository;
import com.data.repository.datasource.SocialFriendsDataSource;
import com.data.repository.datasource.cloud.CloudAppConfigsDataSource;
import com.data.repository.datasource.cloud.CloudSocialsFriendsDataSource;
import com.domain.interactors.AppConfigsUseCase;
import com.domain.interactors.contactInvite.GetMutualFriendsRepository;
import com.domain.repository.SocialFriendsRepository;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ContactFriendsPresenter extends BasePresenter<ContactFriendsContract.View> implements ContactFriendsContract.UserActions {


    final GetMutualFriendsRepository mMutualFriendsRepository;
    private final AppConfigsUseCase mAppConfigsUseCase;
    List<?> mContactList;/* instance of ContactModel*/

    public ContactFriendsPresenter(AppsterWebserviceAPI service) {
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        SocialFriendsDataSource socialFriendsDataSource = new CloudSocialsFriendsDataSource(service, AppsterUtility.getAuth());
        SocialFriendsRepository repository = new SocialFriendsDataRepository(socialFriendsDataSource);
        mMutualFriendsRepository = new GetMutualFriendsRepository(uiThread, ioThread, repository);
        AppConfigsDataRepository appConfigsDataRepository = new AppConfigsDataRepository(new CloudAppConfigsDataSource(service,AppsterUtility.getAuth()));
        mAppConfigsUseCase = new AppConfigsUseCase(uiThread, ioThread, appConfigsDataRepository);
        addSubscription(mAppConfigsUseCase.execute(null)
                .subscribe(appConfigModelBaseResponse -> {
                    if (appConfigModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        Constants.INVITE_FRIENDS_CONTENT = appConfigModelBaseResponse.getData().inviteFriendMessageContent;
                    }
                },Timber::e)
        );
    }

    @Override
    public void eliminateMutualFriends(List<?> contacts) {
        checkViewAttached();
        mContactList = contacts;
        addSubscription(mMutualFriendsRepository.execute(GetMutualFriendsRepository.Params.from(contacts))
                .filter(items -> getView() != null)
                .subscribe(items -> {
                    Timber.e("items size %s",items.size());
                    getView().onMutualFriendsListReceived(items);
                    removeMutualFriends(items);
                }, this::handleRetrofitError));


    }

    @Override
    public void followUser(String userId) {
        SetFollowUserRequestModel request = new SetFollowUserRequestModel();
        request.setFollow_user_id(userId);
        addSubscription(AppsterWebServices.get().setFollowUser(AppsterUtility.getAuth(), request)
                .subscribe(setFollowUserResponseModel -> {
                    if (setFollowUserResponseModel == null) {
                        return;
                    }
                    if (setFollowUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        getView().onChangeFollowStatusSuccessfully(userId, Constants.IS_FOLLOWING_USER);
                        AppsterApplication.mAppPreferences.getUserModel().setFollowingCount(setFollowUserResponseModel.getData().getFollowingCount());

                    } else {
                        getView().onChangeFollowStatusError(setFollowUserResponseModel.getCode(), setFollowUserResponseModel.getMessage());
                    }
                },error -> getView().onChangeFollowStatusError(Constants.RETROFIT_ERROR, error.getMessage())));
    }

    @Override
    public void unFollowUser(String userId) {
        SetUnfollowUserRequestModel request = new SetUnfollowUserRequestModel();
        request.setFollow_user_id(userId);
        addSubscription(AppsterWebServices.get().setUnfollowUser(AppsterUtility.getAuth(), request)
                .subscribe(setFollowUserResponseModel -> {
                    if (setFollowUserResponseModel == null) {
                        return;
                    }
                    if (setFollowUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        getView().onChangeUnFollowStatusSuccessfully(userId, Constants.UN_FOLLOW_USER);
                        AppsterApplication.mAppPreferences.getUserModel().setFollowingCount(setFollowUserResponseModel.getData().getFollowingCount());

                    } else {
                        getView().onChangeUnFollowStatusError(setFollowUserResponseModel.getCode(), setFollowUserResponseModel.getMessage());
                    }
                },error -> getView().onChangeUnFollowStatusError(Constants.RETROFIT_ERROR, error.getMessage())));
    }


    @SuppressWarnings("unchecked")
    private void removeMutualFriends(List<FriendSuggestionModel> items) {
        if (mContactList != null && !mContactList.isEmpty()) {
            final List<ContactModel> contacts = (List) mContactList;
            addSubscription(Observable.from(contacts)
                    .observeOn(Schedulers.computation())
                    .flatMap(contactModel -> {
                        for (FriendSuggestionModel item : items) {
                            boolean isMatched = !item.phoneNumber.isEmpty() && contactModel.getFirstNomalizedPhoneNum().equalsIgnoreCase(item.normalizedPhone);
                            if (isMatched) {
                                return null;
                            }
                        }
                        return Observable.just(contactModel);
                    })
                    .filter(contactModel -> contactModel != null)
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(contactModels -> getView() != null)
                    .subscribe(finalContacts -> getView().onGuestFriendsListReceived(finalContacts),Timber::e));

        } else {
            if (getView() != null)
                getView().onGuestFriendsListReceived(new ArrayList<ContactModel>());
        }
    }
}
