package com.appster.features.contacts;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.UserProfileActivity;
import com.appster.core.expanableadapter.ParentListItem;
import com.appster.domain.FriendSuggestionModel;
import com.appster.features.contacts.adapters.ContactGroupAdapter;
import com.appster.features.contacts.adapters.InviteFriendItemViewHolder;
import com.appster.features.friend_suggestion.viewholder.FriendOnBeliveViewHolder;
import com.appster.models.BeLiveFriendParent;
import com.appster.models.ContactModel;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.InviteFriendParent;
import com.appster.models.UserModel;
import com.appster.models.mappers.ContactModelMapper;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DialogUtil;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;
import com.contacts.Contact;
import com.contacts.Contacts;
import com.contacts.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.appster.AppsterApplication.mAppPreferences;
import static com.apster.common.Constants.IS_FOLLOWING_USER;

public class ContactFriendsActivity extends BaseToolBarActivity implements ContactFriendsContract.View,
        InviteFriendItemViewHolder.InviteFriendViewHolderListener,
        FriendOnBeliveViewHolder.FriendOnBeLiveViewHolderListener {

    ContactFriendsContract.UserActions presenter;
    ContactGroupAdapter mFriendGroupAdapter;
    @Bind(R.id.rcvInviteFriend)
    RecyclerView rcvInviteFriend;
    @Bind(R.id.tvEmpty)
    TextView tvEmpty;
    List<?> suggestionModels = new ArrayList<>();
    List<ParentListItem> mContactGroupContainer = new ArrayList<>();
    UserModel mUserModel;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ContactFriendsActivity.class);
        return intent;
    }

    //region-------activity life cycle-------
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserModel = mAppPreferences.getUserModel();
        presenter = new ContactFriendsPresenter(AppsterWebServices.get());
        presenter.attachView(this);
        mCompositeSubscription.add(mRxPermissions.request(Manifest.permission.READ_CONTACTS)
                .filter(granted -> isActivityRunning())
                .subscribe(isGranted -> {
                    if (isGranted) {
                        showProgress();
                        queryContacts();
                    } else {
                        finish();
                    }
                }));
    }

    private void queryContacts() {
        mCompositeSubscription.add(Observable.defer(() -> Observable.just(readPhoneContacts()))
                .flatMap(contacts -> {
                    Timber.e("size %d - detail: %s", contacts.size(), contacts.toString());
                    ContactModelMapper mapper = new ContactModelMapper();
                    return Observable.just(mapper.transform(contacts));
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(contacts -> isActivityRunning() && contacts != null)
                .subscribe(contacts -> {
                    if (presenter != null) presenter.eliminateMutualFriends(contacts);

                    Timber.e("size %d - detail: %s", contacts.size(), contacts.toString());
                }, Timber::e));
    }

    private List<Contact> readPhoneContacts() {
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        if (mUserModel != null && !mUserModel.getPhoneNumber().isEmpty())
            q.whereNotEqualTo(Contact.Field.PhoneNormalizedNumber, mUserModel.getNormalizedPhone());
        return q.find();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleTurnoffMenuSliding();
        setTopBarTile(getString(R.string.contacts_title));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_contacts;
    }

    @Override
    public void init() {

    }

    //endregion -------activity life cycle-------

    //region -------inheritance methods-------
    //endregion -------inheritance methods-------

    //region -------implement methods-------
    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {
        handleError(errorMessage, 9999);
    }

    @Override
    public void showProgress() {
        showDialog(this, getResources().getString(R.string.fetching_contacts));
    }

    @Override
    public void hideProgress() {
        dismisDialog();
    }

    @Override
    public void onMutualFriendsListReceived(List<?> mutualFriends) {
        suggestionModels = mutualFriends;
        if (!suggestionModels.isEmpty()) {
            BeLiveFriendParent beLiveFriendParent = new BeLiveFriendParent(getString(R.string.friend_on_belive), new ArrayList<>(suggestionModels));
            mContactGroupContainer.add(beLiveFriendParent);
        }

    }

    @Override
    public void onGuestFriendsListReceived(List<?> contacts) {
        if (isActivityRunning()) {
            if (!contacts.isEmpty()) {
                InviteFriendParent inviteFriends = new InviteFriendParent(getString(R.string.invite_friends), contacts);
                mContactGroupContainer.add(inviteFriends);
            }
            if(!mContactGroupContainer.isEmpty()) {
                displayOnScreen();
            }else{
                tvEmpty.setText(getString(R.string.nothing_here));
            }
        }
        hideProgress();
    }

    @Override
    public void onChangeFollowStatusSuccessfully(String userId, int status) {
        changeFollowStatus(userId, status);
    }

    @Override
    public void onChangeFollowStatusError(int code, String message) {

    }

    @Override
    public void onChangeUnFollowStatusSuccessfully(String userId, int status) {
        changeFollowStatus(userId, status);
    }

    @Override
    public void onChangeUnFollowStatusError(int code, String message) {

    }

    private void displayOnScreen() {
        if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
        mFriendGroupAdapter = new ContactGroupAdapter(mContactGroupContainer, !suggestionModels.isEmpty());
        mFriendGroupAdapter.setFriendOnBeLiveViewHolderListener(this);
        mFriendGroupAdapter.setInviteFriendViewHolderListener(this);
        rcvInviteFriend.setLayoutManager(new LinearLayoutManager(this));
        rcvInviteFriend.setAdapter(mFriendGroupAdapter);
    }

    @Override
    public void onInviteClicked(View v, ContactModel contact, int position) {
        if (mUserModel != null && isActivityRunning()) {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(String.format(Locale.US, "smsto:%s", contact.getFirstNomalizedPhoneNum())));
            smsIntent.putExtra("sms_body", Constants.INVITE_FRIENDS_CONTENT.isEmpty() ? getString(R.string.invite_sns_message, mUserModel.getReferralId()) : Constants.INVITE_FRIENDS_CONTENT);
            startActivity(smsIntent);
        }
    }

    @Override
    public void onFollowButtonClicked(View v, FriendSuggestionModel userItemModel, int position) {
        if (userItemModel.getIsFollow() == IS_FOLLOWING_USER) {
            showConfirmDialog(userItemModel.getUserId(), userItemModel.getDisplayName());
        } else {
            presenter.followUser(userItemModel.getUserId());
        }
    }

    private void showConfirmDialog(String userId, String displayName) {
        DialogUtil.showConfirmUnFollowUser(this, displayName,
                () -> presenter.unFollowUser(userId));
    }

    @Override
    public void onAvatarImgClicked(View v, FriendSuggestionModel userItemModel, int position) {
        gotoUserProfileScreen(userItemModel.getUserId(), userItemModel.getDisplayName());
    }

    @Override
    public void onItemClicked(View v, FriendSuggestionModel userItemModel, int position) {
        gotoUserProfileScreen(userItemModel.getUserId(), userItemModel.getDisplayName());
    }

    private void gotoUserProfileScreen(String userId, String displayName) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        startActivityForResult(UserProfileActivity.newIntent(this, userId, displayName),
                Constants.REQUEST_CODE_VIEW_USER_PROFILE, options.toBundle());
    }

    void changeFollowStatus(String userId, int status) {
        if (suggestionModels != null && !suggestionModels.isEmpty()) {
            mCompositeSubscription.add(Observable.from(suggestionModels)
                    .map(displayableItemObservable -> (FriendSuggestionModel) displayableItemObservable)
                    .filter(friendSuggestionModel -> friendSuggestionModel.getUserId().equalsIgnoreCase(userId) && friendSuggestionModel.getIsFollow() != status)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(friendSuggestionModel -> {
                        friendSuggestionModel.setIsFollow(status);
                        if (mFriendGroupAdapter != null)
                            mFriendGroupAdapter.notifyChildItemChanged(mFriendGroupAdapter.getParentItemList().indexOf(findMutualFriendParentIndex()), suggestionModels.indexOf(friendSuggestionModel));
                    }, Timber::e));
        }
    }

    private ParentListItem findMutualFriendParentIndex() {
        /* since only happened with BeLiveFriendParent so we will find BeLiveFriendParent instance */
        if (mFriendGroupAdapter != null && mFriendGroupAdapter.getItemCount() > 0) {
            for (ParentListItem parentListItem : mFriendGroupAdapter.getParentItemList()) {
                if (parentListItem instanceof BeLiveFriendParent) {
                    return parentListItem;
                }
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_VIEW_USER_PROFILE:
                onResultFromProfileScreen(data);
                break;

            default:
                break;
        }
    }

    private void onResultFromProfileScreen(Intent data) {
        if (data == null) {
            return;
        }

        FollowStatusChangedEvent followStatusChangedEvent = data.getParcelableExtra
                (ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY);

        if (followStatusChangedEvent == null) return;
        changeFollowStatus(followStatusChangedEvent.getUserId(), followStatusChangedEvent.getFollowType());
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    //endregion -------inner methods-------


    //region -------inner class-------

    //endregion -------inner class-------
}
