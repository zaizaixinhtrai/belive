package com.appster.features.blocked_screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.domain.BlockedUserModel;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.UiUtils;
import com.pack.utility.CheckNetwork;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by linh on 27/12/2016.
 */

public class BlockedUserActivity extends BaseToolBarActivity implements BlockedScreenContract.BlockedUserView, BlockedUserAdapter.OnLikedUserItemClickListener {
    public static final String ARG_UNBLOCK_USER = "ARG_UNBLOCK_USER";
    @Bind(R.id.rcv_liked_users)
    RecyclerView rcvLikedUsers;
    @Bind(R.id.txt_empty)
    TextView txtEmpty;

    private BlockedUserAdapter adapter;
    private BlockedUserPresenter presenter;

    //=========== inherited methods ================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTopBarTile(getString(R.string.blocked_screen_title));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> finish());
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.list_liked_users;
    }

    @Override
    public void init() {
        ButterKnife.bind(this);
        presenter = new BlockedUserPresenter(this, AppsterWebServices.get());
        adapter = new BlockedUserAdapter(this, rcvLikedUsers, new ArrayList<>());
        rcvLikedUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvLikedUsers.addItemDecoration(new UiUtils.ListSpacingItemDecoration((int) getResources().getDimension(R.dimen.chat_list_divider), false));
        adapter.setRecyclerItemCallBack(this);
        rcvLikedUsers.setAdapter(adapter);
//        int space = (int) getResources().getDimension(R.dimen.chat_list_divider);
//        rcvLikedUsers.addItemDecoration(new UiUtils.ListSpacingItemDecoration(space, false));
        adapter.setOnLoadMoreListener(() -> presenter.getBlockedUsers());
        if (!CheckNetwork.isNetworkAvailable(this)) {
            utility.showMessage(getString(R.string.app_name),
                    getResources().getString(
                            R.string.no_internet_connection),
                    this);
            return;
        } else {
            presenter.getBlockedUsers();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {
        handleError(errorMessage, Constants.RETROFIT_ERROR);
    }

    @Override
    public void showProgress() {
        DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));
    }

    @Override
    public void hideProgress() {
        DialogManager.getInstance().dismisDialog();
    }

    @Override
    public void onBlockedListResponse(List<BlockedUserModel> blockedUsers) {
        adapter.addNewList(blockedUsers);
        checkAdapterNoData();
    }

    //=========== implemented methods ==============================================================
    @Override
    public void onProfileImageClick(BlockedUserModel usersItemModel) {
//        startActivityProfile(usersItemModel.getUserId(), usersItemModel.getDisplayName());
    }

    @Override
    public void onBlockButtonClick(BlockedUserModel usersItemModel, int position) {
        showConfirmDialog(usersItemModel, position);
    }

    private void showConfirmDialog(BlockedUserModel usersItemModel, int position) {
        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(mActivity.getString(R.string.unblock_this_user))
                .message(mActivity.getString(R.string.unblock_confirmation_content))
                .confirmText(mActivity.getString(R.string.string_unblock))
                .onConfirmClicked(() -> presenter.unblockUser(usersItemModel, position))
                .build().show(mActivity);
    }


    @Override
    public void onUnblockUserSuccessfully(int position) {
        adapter.removeItem(position);
        checkAdapterNoData();
        Toast.makeText(this, getString(R.string.unblock), Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();
        intent.putExtra(BlockedUserActivity.ARG_UNBLOCK_USER, true);
        setResult(Activity.RESULT_OK, intent);
    }

    //=========== inner methods ====================================================================
    private void checkAdapterNoData() {
        if (adapter.getItemCount() <= 0) {
            txtEmpty.setVisibility(View.VISIBLE);
        } else {
            txtEmpty.setVisibility(View.INVISIBLE);
        }
    }

    //=========== inner classes ====================================================================
}
