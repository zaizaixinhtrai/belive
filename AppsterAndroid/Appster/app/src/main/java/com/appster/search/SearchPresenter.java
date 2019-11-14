package com.appster.search;

import android.app.Activity;

import com.appster.base.ActivityScope;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.manager.ShowErrorManager;
import com.appster.models.FollowUser;
import com.appster.utility.AppsterUtility;
import com.appster.utility.DialogUtil;
import com.appster.webservice.AppsterWebserviceAPI;
import com.apster.common.Constants;
import com.data.entity.requests.SearchUserRequestEntity;
import com.data.repository.SocialFriendsDataRepository;
import com.data.repository.datasource.SocialFriendsDataSource;
import com.data.repository.datasource.cloud.CloudSocialsFriendsDataSource;
import com.domain.interactors.contactInvite.GetSocialsFriendRepository;
import com.domain.interactors.searchUser.SearchUserUseCase;
import com.domain.models.SearchUserModel;
import com.domain.repository.SocialFriendsRepository;

import javax.inject.Inject;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by User on 11/30/2016.
 */
@ActivityScope
public class SearchPresenter extends BasePresenter<SearchContract.SearchView> implements SearchContract.UserActions {
    private static final int LIMIT_PAGE_SEARCH = 7;
    private final GetSocialsFriendRepository mSocialsFriendRepository;
    private SearchUserUseCase searchUserUseCase;

    @Inject
    public SearchPresenter(SearchContract.SearchView searchView, AppsterWebserviceAPI service, SearchUserUseCase searchUserUseCase) {
        attachView(searchView);
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        SocialFriendsDataSource socialFriendsDataSource = new CloudSocialsFriendsDataSource(service, AppsterUtility.getAuth());
        SocialFriendsRepository repository = new SocialFriendsDataRepository(socialFriendsDataSource);
        mSocialsFriendRepository = new GetSocialsFriendRepository(uiThread, ioThread, repository);
        this.searchUserUseCase = searchUserUseCase;
    }

    @Override
    public void searchUser(String textInput, int nextIndex) {
        SearchUserRequestEntity requests = new SearchUserRequestEntity();
        requests.setDisplayName(textInput);
        requests.setLimit(LIMIT_PAGE_SEARCH);
        requests.setNextId(nextIndex);
        addSubscription(searchUserUseCase.execute(requests)
                .filter(searchResponseModel -> getView() != null && searchResponseModel != null)
                .subscribe(searchResponseModel -> {
                    getView().showResult(searchResponseModel.getResult());
                    getView().getPagingResult(searchResponseModel.getNextId(), searchResponseModel.isEnd());
                    if (searchResponseModel.isEnd()) {
                        getView().hideFooterListView();
                    } else {
                        getView().addFooterListView();
                    }
                    getView().handleTextNoData();

                }, this::handleRetrofitError));
    }

    @Override
    public void followUser(int position, SearchUserModel itemModelClass) {
        if (getView() == null) return;
        boolean isFollow = itemModelClass.isFollow() == Constants.IS_FOLLOWING_USER;

        if (isFollow) {
            DialogUtil.showConfirmUnFollowUser((Activity) getView().getViewContext(), itemModelClass.getDisplayName(),
                    () -> executeFollowing(itemModelClass, position, false));
        } else {
            executeFollowing(itemModelClass, position, true);
        }
    }

    @Override
    public void followUserWithPass(int position, SearchUserModel itemModelClass, String pass) {
        if (getView() == null) return;
        boolean isFollow = itemModelClass.isFollow() == Constants.IS_FOLLOWING_USER;

        if (!isFollow) {
            executeFollowingWithPass(itemModelClass.getUserId(), position, true, pass);
        }
    }

    @Override
    public void getSocialFriends() {
        addSubscription(mSocialsFriendRepository.execute(null)
                .filter(socialFriendsNumModel -> getView() != null && socialFriendsNumModel != null)
                .subscribe(socialFriendsNumModel -> {
                    getView().onContactMutualFriendsReceived(socialFriendsNumModel.contactFriends);
                    getView().onFacebookMutualFriendsReceived(socialFriendsNumModel.facebookFriends);
                    getView().onInstagramMutualFriendsReceived(socialFriendsNumModel.instagramFriends);
                    getView().onTwitterMutualFriendsReceived(socialFriendsNumModel.twitterFriends);
                }, this::handleRetrofitError));
    }

    void executeFollowingWithPass(String userId, int position, boolean isFollow, String pass) {
        FollowUser followUser = new FollowUser(getView().getViewContext(), userId, isFollow);
        followUser.executeFollowWithPass(pass);
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                getView().changeFollowUser(position, isFollow);
            }

            @Override
            public void onError(int errorCode, String message) {
                getView().loadError(message, errorCode);
            }
        });
    }

    void executeFollowing(SearchUserModel userModel, int position, boolean isFollow) {
        FollowUser followUser = new FollowUser(getView().getViewContext(), userModel.getUserId(), isFollow);
        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                getView().changeFollowUser(position, isFollow);
            }

            @Override
            public void onError(int errorCode, String message) {
                if (errorCode == ShowErrorManager.pass_word_required) {
                    getView().onFollowRequirePass(position, userModel);
                } else {
                    getView().loadError(message, errorCode);
                }
            }
        });
    }


    @Override
    public void detachView() {
        super.detachView();
    }
}
