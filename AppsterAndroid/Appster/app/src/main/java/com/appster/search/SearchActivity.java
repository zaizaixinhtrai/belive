package com.appster.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.UserProfileActivity;
import com.appster.adapters.SearchUserAdapter;
import com.appster.customview.CustomFontEditText;
import com.appster.customview.CustomFontTextView;
import com.appster.features.contacts.ContactFriendsActivity;
import com.appster.features.fb_friends.FacebookFriendSuggestionActivity;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.utility.ConstantBundleKey;
import com.apster.common.Constants;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.Utils;
import com.domain.models.SearchUserModel;
import com.pack.utility.CheckNetwork;
import com.spacenavigationview.BadgeHelper;
import com.spacenavigationview.BadgeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

/**
 * Created by User on 10/15/2015.
 */
public class SearchActivity extends BaseToolBarActivity implements SearchContract.SearchView, AdapterSearchItemCallBack {

    @Bind(R.id.edt_input_search)
    CustomFontEditText edt_input_search;
    @Bind(R.id.tv_no_data)
    TextView mTxtNoData;
    @Bind(R.id.txt_fb_fiends)
    CustomFontTextView mTxtFbFriends;
    @Bind(R.id.tvContacts)
    CustomFontTextView tvContacts;

    @Bind(R.id.badgeContainerFacebook)
    RelativeLayout mBadgeFacebookContainer;
    @Bind(R.id.badgeContainerContact)
    RelativeLayout mBadgeContactContainer;

    @Bind(R.id.llFacebookFriendContainer)
    LinearLayout llFacebookFriendContainer;

    @Bind(R.id.llContactFriendContainer)
    LinearLayout llContactFriendContainer;
    @Bind(R.id.imv_clear_text)
    ImageButton imvClearText;
    @Bind(R.id.rcvListUsers)
    RecyclerView rcvListUsers;

    private Timer timer;
    private static final long DELAY = 1000; // in ms
    private String textInput = "";
    private int nextIndex = 0;

    private ArrayList<SearchUserModel> listUsers = new ArrayList();
    private SearchUserAdapter advanceAdapter;
    @Inject
    SearchContract.UserActions presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        // Show keybroad
        if (getWindow() != null)
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        if (mTxtFbFriends != null)
//            mTxtFbFriends.setVisibility(isFacebookLogin() ? View.VISIBLE : View.GONE);
//        checkToShowFacebookInvitation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleTurnoffMenuSliding();
        setTxtTitleAsAppName();
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
        if (presenter != null) presenter.getSocialFriends();
    }

    @Override
    public void onBackPressed() {
        Utils.hideSoftKeyboard(this);
        finish();
        super.onBackPressed();

    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_search;
    }

    @Override
    public void init() {
        imvClearText.setVisibility(View.GONE);
        timer = new Timer();

        // Set adapter list user
        advanceAdapter = new SearchUserAdapter(null, listUsers, this);
        rcvListUsers.setLayoutManager(new LinearLayoutManager(this));
        rcvListUsers.setAdapter(advanceAdapter);
        rcvListUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Utils.hideSoftKeyboard(SearchActivity.this);
            }
        });

        onSearchListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQUEST_CODE_VIEW_USER_PROFILE:
                if (resultCode == RESULT_OK && data != null) {
                    FollowStatusChangedEvent event = data.getParcelableExtra(ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY);

                    if (event != null) {
                        updateFollowUser(event);
                    }

                    if (data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
                        refreshSearchResult();
                        Intent intent = getIntent();
                        intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true);
                        setResult(Activity.RESULT_OK, intent);
                    }
                }
        }
    }

    private void onSearchListener() {
        edt_input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // Cancel Timer Task
                timer.cancel();
                hideFooterListView();
                nextIndex = 0;

                listUsers.clear();
                advanceAdapter.notifyDataSetChanged();

                textInput = edt_input_search.getText().toString();

                // If input text null, clear data
                if (textInput.trim().length() == 0) {
                    mTxtNoData.setVisibility(View.GONE);
                    llFacebookFriendContainer.setVisibility(View.VISIBLE);
                    llContactFriendContainer.setVisibility(View.VISIBLE);
                    imvClearText.setVisibility(View.GONE);
                    return;
                } else {
                    llFacebookFriendContainer.setVisibility(View.GONE);
                    llContactFriendContainer.setVisibility(View.GONE);
                    imvClearText.setVisibility(View.VISIBLE);
                }

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if (presenter != null)
                            presenter.searchUser(textInput, nextIndex);
                    }

                }, DELAY);
            }
        });
    }

    //================ event listeners =============================================================
    @OnClick(R.id.llFacebookFriendContainer)
    void onFbButtonClicked() {
        startActivity(FacebookFriendSuggestionActivity.newIntent(this));
    }

    @OnClick(R.id.llContactFriendContainer)
    void onContactsClicked() {
        startActivity(ContactFriendsActivity.createIntent(this));
    }

    @OnClick(R.id.imv_clear_text)
    void onClearText() {
        edt_input_search.setText("");
        timer.cancel();
        hideFooterListView();
        nextIndex = 0;
        listUsers.clear();
        advanceAdapter.notifyDataSetChanged();
    }

    @Override
    public void showResult(List<SearchUserModel> listUser) {
        if (nextIndex == 0) {
            listUsers.clear();
        }
        if (listUser == null) return;
        hideFooterListView();
        listUsers.addAll(listUser);
        advanceAdapter.notifyDataSetChanged();
    }

    @Override
    public void getPagingResult(int nextPage, boolean isEnd) {
        nextIndex = nextPage;
    }

    @Override
    public void addFooterListView() {

        SearchUserModel item = new SearchUserModel();
        item.setTypeModel(SearchUserTypeItems.FOOTER_VIEW);
        listUsers.add(item);
        advanceAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideFooterListView() {
        if (!listUsers.isEmpty()) {
            if (listUsers.get(listUsers.size() - 1).getTypeModel() == SearchUserTypeItems.FOOTER_VIEW) {
                listUsers.remove(listUsers.size() - 1);
                advanceAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void handleTextNoData() {
        if (listUsers.isEmpty()) {
            hideFooterListView();
            mTxtNoData.setVisibility(View.VISIBLE);
        } else {
            mTxtNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void changeFollowUser(int position, boolean isFollow) {
        if (listUsers != null && listUsers.size() > position) {
            listUsers.get(position).setFollow(isFollow ? Constants.IS_FOLLOWING_USER : Constants.UN_FOLLOW_USER);
            advanceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateFollowUser(FollowStatusChangedEvent event) {

        if (listUsers != null) {
            SearchUserModel item;
            for (int i = 0; i < listUsers.size(); i++) {
                item = listUsers.get(i);
                if (item.getUserId().equals(event.getUserId())) {
                    listUsers.get(i).setFollow(event.getFollowType());
                    advanceAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void onFacebookMutualFriendsReceived(int numOfFriends) {
        if (numOfFriends != 0 && mBadgeFacebookContainer != null) {
            BadgeItem badgeItem = new BadgeItem(0, numOfFriends, ContextCompat.getColor(this, R.color.color_ff5167));
            mBadgeFacebookContainer.setBackground(BadgeHelper.makeShapeDrawableWithoutBorder(badgeItem.getBadgeColor()));
            BadgeHelper.showBadge(
                    mBadgeFacebookContainer,
                    new BadgeItem(0, numOfFriends, ContextCompat.getColor(this, R.color.color_ff5167)),
                    true, R.id.tvFacebookFriendsNum);
        }
    }

    @Override
    public void onContactMutualFriendsReceived(int numOfFriends) {
        if (numOfFriends != 0 && mBadgeContactContainer != null) {
            BadgeItem badgeItem = new BadgeItem(0, numOfFriends, ContextCompat.getColor(this, R.color.color_ff5167));
            mBadgeContactContainer.setBackground(BadgeHelper.makeShapeDrawableWithoutBorder(badgeItem.getBadgeColor()));
            BadgeHelper.showBadge(
                    mBadgeContactContainer,
                    badgeItem,
                    true, R.id.tvContactFriendsNum);
        }
    }

    @Override
    public void onInstagramMutualFriendsReceived(int numOfFriends) {

    }

    @Override
    public void onTwitterMutualFriendsReceived(int numOfFriends) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.hideSoftKeyboard(this);
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {
        handleError(errorMessage, code);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showUserProfile(int position, SearchUserModel itemModelClass) {
        startActivityProfile(itemModelClass.getUserId(), itemModelClass.getDisplayName());
    }

    @Override
    public void followUser(int position, SearchUserModel itemModelClass) {

        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            goingLoginScreen();
            return;
        }

        if (!CheckNetwork.isNetworkAvailable(this)) {
            utility.showMessage("", getString(R.string.no_internet_connection), this);
            return;
        }
        presenter.followUser(position, itemModelClass);
    }

    @Override
    public void onFollowRequirePass(int position, SearchUserModel itemModelClass) {
        new DialogbeLiveConfirmation.Builder()
                .title(getString(R.string.enter_password))
                .setPasswordBox(true)
                .confirmText(getString(R.string.verify))
                .onEditTextValue(value -> presenter.followUserWithPass(position, itemModelClass, value))
                .build().show(this);
    }

    @Override
    public void onClickLoadMore() {
        presenter.searchUser(textInput, nextIndex);
    }

    private void refreshSearchResult() {
        nextIndex = 0;
        presenter.searchUser(textInput, nextIndex);
    }

}
