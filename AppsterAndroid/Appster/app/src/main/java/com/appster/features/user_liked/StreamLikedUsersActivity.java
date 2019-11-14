package com.appster.features.user_liked;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.UserProfileActivity;
import com.appster.domain.LikedUsersItemModel;
import com.appster.interfaces.OnLoadMoreListenerRecyclerView;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.utility.ConstantBundleKey;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ThanhBan on 9/27/2016.
 */

public class StreamLikedUsersActivity extends BaseToolBarActivity implements LikedUserAdapter.OnLikedUserItemClickListener,
        StreamLikedScreenContract.StreamLikedScreenView {


    @Bind(R.id.rcv_liked_users)
    RecyclerView rcvLikedUsers;

    LikedUserAdapter likedUserAdapter;
    StreamLikedScreenContract.UserActions presenter;
    boolean isStream;
    int postId;
    String slug;
    private static final String POST_ID = "post_id";
    private static final String SLUG = "slug";
    private static final String IS_STREAM = "is_stream";

    public static Intent createIntent(Context context, int postId,boolean isStream,String Slug) {
        Intent intent = new Intent(context, StreamLikedUsersActivity.class);
        intent.putExtra(POST_ID, postId);
        intent.putExtra(SLUG, Slug);
        intent.putExtra(IS_STREAM, isStream);
        return intent;
    }
    public static Intent createIntent(Context context, int postId) {
        Intent intent = new Intent(context, StreamLikedUsersActivity.class);
        intent.putExtra(POST_ID, postId);
        intent.putExtra(SLUG, "");
        intent.putExtra(IS_STREAM, false);
        return intent;
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.list_liked_users;
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleTurnoffMenuSliding();
        setTopBarTile(getString(R.string.likes_title));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void init() {
        ButterKnife.bind(this);
        postId = getIntent().getExtras().getInt(POST_ID);
        isStream = getIntent().getExtras().getBoolean(IS_STREAM);
        slug = getIntent().getExtras().getString(SLUG);
        presenter = new LikedUserPresenter(this, AppsterWebServices.get(),isStream,slug);
        likedUserAdapter = new LikedUserAdapter(this, rcvLikedUsers, new ArrayList<LikedUsersItemModel>());
        rcvLikedUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        likedUserAdapter.setRecyclerItemCallBack(this);
        rcvLikedUsers.setAdapter(likedUserAdapter);
//        int space = (int) getResources().getDimension(R.dimen.chat_list_divider);
//        rcvLikedUsers.addItemDecoration(new UiUtils.ListSpacingItemDecoration(space, false));
        likedUserAdapter.setOnLoadMoreListener(new OnLoadMoreListenerRecyclerView() {
            @Override
            public void onLoadMore() {
                presenter.getLikedUsers(likedUserAdapter.getTimeDelay(), postId);
            }
        });


        presenter.getLikedUsers(0, postId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        presenter.detachView();
    }

    @Override
    public void likedUsers(List<LikedUsersItemModel> likedUsers) {
        likedUserAdapter.addNewList(likedUsers);
        likedUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadingItem() {
        likedUserAdapter.addProgressItem();
    }

    @Override
    public void dismissLoadingItem() {
        likedUserAdapter.removeProgressItem();
        likedUserAdapter.setLoaded();
    }

    @Override
    public void followChanged(boolean isFollow, int position) {
        likedUserAdapter.notifyFollowChanged(isFollow, position);
    }

    @Override
    public Context getViewContext() {
        return getApplicationContext();
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
    public void onProfileImageClick(LikedUsersItemModel usersItemModel) {
        startActivityProfile(usersItemModel.getUserId(), usersItemModel.getDisplayName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK){
            if(requestCode== Constants.REQUEST_CODE_VIEW_USER_PROFILE){
                if(data!=null){
                    FollowStatusChangedEvent followStatusChangedEvent = data.getParcelableExtra(ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY);
                    if(followStatusChangedEvent!=null){
                        likedUserAdapter.updateFollow(followStatusChangedEvent);
                    }
                }

            }

            if (data != null && data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)){
                refreshData();
                Intent intent = getIntent();
                intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true);
                setResult(Activity.RESULT_OK, intent);
            }
        }
    }

    @Override
    public void onFollowClick(LikedUsersItemModel usersItemModel, int position) {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) (mActivity)).goingLoginScreen();
            return;
        }
        presenter.followButtonClicked(usersItemModel, position);
    }

    private void refreshData(){
        likedUserAdapter.clearData();
        presenter.refreshData(postId);
    }
}
