package com.appster.comments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.UserProfileActivity;
import com.appster.customview.CustomListView;
import com.appster.customview.taggableedittext.TaggableEditText;
import com.appster.utility.ConstantBundleKey;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.AddCommentRequestModel;
import com.appster.webservice.request_models.CommentListRequestModel;
import com.appster.webservice.request_models.DeleteCommentRequestModel;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.Constants;
import com.apster.common.Constants.COMMENT_TYPE;
import com.apster.common.DialogManager;
import com.apster.common.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.pack.utility.CheckNetwork;
import com.pack.utility.DialogInfoUtility;
import com.pack.utility.SetDateTime;
import com.pack.utility.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.OnClick;
import rx.Subscriber;
import timber.log.Timber;


/**
 * Created by User on 10/8/2015.
 */
public class CommentActivity extends BaseToolBarActivity implements AdapterListComments.CommentCallback{

    private CustomListView lv_listComments;
    private TaggableEditText edt_input_comments;
    private AdapterListComments adapter;
    private ArrayList<ItemClassComments> listComment = new ArrayList<ItemClassComments>();
    private ArrayList<ItemClassComments> listNewComment = new ArrayList<>();
    private int postId;
    private String mSlug;
    private int mCommentType = Constants.COMMENT_TYPE_POST;
    //    private GetComments getMomments;
    private FrameLayout chat_send_btn;
    private String commentsString = "";
    private int positionOnListview;
    private boolean isTheEnd = false;
    private int indexPage = 0;
    private int commentCounts;
    private boolean mHasGivenComment;
    private String mUserIdOwnerPost;

    public static Intent createIntent(Context context, int postID, String slug, int position, @COMMENT_TYPE int commentType,
                                      String userNameOwnerPost) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(ConstantBundleKey.BUNDLE_POST_ID_KEY, postID);
        intent.putExtra(ConstantBundleKey.BUNDLE_POST_STREAM_SLUG, slug);
        intent.putExtra(ConstantBundleKey.BUNDLE_COMMENT_POSITION, position);
        intent.putExtra(ConstantBundleKey.BUNDLE_COMMENT_TYPE, commentType);
        intent.putExtra(ConstantBundleKey.BUNDLE_COMMENT_OWNER_ID, userNameOwnerPost);

        return intent;
    }
//    private FragmentMeNew fragmentUser;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_in_to_right,
                R.anim.push_in_to_left);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setTopBarTile(getString(R.string.comment));

    }

    @Override
    public int getLayoutContentId() {
        return R.layout.dialog_comments;
    }

    @Override
    public void init() {

        intId();
        intData();
        if (mBeLiveThemeHelper != null && mBeLiveThemeHelper.isTransparentStatusBarRequired()) {
            Window window = getWindow();
            if (window != null) window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        preventDownLineFirst();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBarTile(getString(R.string.comment));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataResult();
            }
        });
        handleTurnoffMenuSliding();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED)
            return;

        switch (requestCode) {
            case Constants.REQUEST_CODE_VIEW_USER_PROFILE:
                if (resultCode == RESULT_OK && data != null) {
                    if (data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
                        refreshChatList();
                        Intent intent = getIntent();
                        intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true);
                        setResult(Activity.RESULT_OK, intent);
                    }
                }
        }
    }

    @Override
    public void onBackPressed() {
        setDataResult();
    }

    private void intId() {

        lv_listComments = (CustomListView) findViewById(R.id.lv_listComments);
        edt_input_comments = (TaggableEditText) findViewById(R.id.edt_input_comments);
        chat_send_btn = (FrameLayout) findViewById(R.id.chat_send_btn);
//        edt_input_comments.setAnchorView((View) edt_input_comments.getParent());
    }

    private void intData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            positionOnListview = extras.getInt(ConstantBundleKey.BUNDLE_COMMENT_POSITION);
            postId = extras.getInt(ConstantBundleKey.BUNDLE_POST_ID_KEY);
            mSlug = extras.getString(ConstantBundleKey.BUNDLE_POST_STREAM_SLUG);
            mCommentType = extras.getInt(ConstantBundleKey.BUNDLE_COMMENT_TYPE);
            mUserIdOwnerPost = extras.getString(ConstantBundleKey.BUNDLE_COMMENT_OWNER_ID);
        }

//        getMomments = new GetComments(this, postId);
        utility = new DialogInfoUtility();

        adapter = new AdapterListComments(this, listComment, mUserIdOwnerPost);
        lv_listComments.setAdapter(adapter);
        adapter.setOnShowUserProfile(position -> startActivityProfile(listComment.get(position).getUser_id(), listComment.get(position).getDisplay_name()));
        adapter.setCommentCallback(this);

        lv_listComments.setMode(PullToRefreshBase.Mode.BOTH);
        lv_listComments.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (isTheEnd) {
                    scrollBottomOfListView(lv_listComments);
                    toastTextOnTheEndListListener("");
                    return;
                }

                if (CheckNetwork.isNetworkAvailable(CommentActivity.this)) {
                    getData(indexPage, false);
                } else {
                    lv_listComments.onRefreshComplete();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshChatList();
            }
        });

        // get Comments list
        if (CheckNetwork.isNetworkAvailable(CommentActivity.this)) {
            getData(indexPage, true);
        } else {
            utility.showMessage(CommentActivity.this.getString(R.string.app_name),
                    getString(R.string.no_internet_connection),
                    CommentActivity.this);
        }

        chat_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentsString = edt_input_comments.getText().toString().trim();
                if (!AppsterApplication.mAppPreferences.isUserLogin()) {
                    goingLoginScreen();
                } else if (!CheckNetwork.isNetworkAvailable(CommentActivity.this)
                        || commentsString == null
                        || commentsString.equals("")) {
                } else if (!CheckNetwork.isNetworkAvailable(CommentActivity.this)) {
                    utility.showMessage(
                            getString(R.string.app_name),
                            getResources().getString(
                                    R.string.no_internet_connection), CommentActivity.this);
                } else {

                    // Update Comment
                    commentsString = StringUtil.encodeString(commentsString);

                    ItemClassComments itemAdd = new ItemClassComments();
                    itemAdd.setMessage(commentsString);
                    itemAdd.setUser_id(AppsterApplication.mAppPreferences.getUserModel().getUserId());
                    itemAdd.setDisplay_name(AppsterApplication.mAppPreferences.getUserModel().getDisplayName());
                    itemAdd.setId(postId);
                    itemAdd.setUser_image(AppsterApplication.mAppPreferences.getUserModel().getUserImage());
                    Date currentDate = new Date();
                    long currentDateTime = currentDate.getTime();
                    itemAdd.setTimestamp(String.valueOf(currentDateTime));
                    itemAdd.setCreated(SetDateTime.getCurrentGMTTime());

                    listComment.add(itemAdd);
                    itemAdd.setGender(AppsterApplication.mAppPreferences.getUserModel().getGender());
                    adapter.notifyDataSetChanged();
                    scrollBottomListView();
//                    listNewComment.add(itemAdd);
                    mHasGivenComment = true;
                    // Add comment
                    addComment();
                }
            }
        });
    }

    private void preventDownLineFirst() {

        edt_input_comments.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Timber.e("text= %s", editable);

                int i = editable.toString().indexOf("\n");
                if ( i == 0 ) {
                    editable.replace(i, i+1, "");
                }
            }
        });
    }

    void refreshChatList() {
        if (CheckNetwork.isNetworkAvailable(CommentActivity.this)) {
            indexPage = 0;
            getData(indexPage, false);
        } else {
            lv_listComments.onRefreshComplete();
        }
    }

    private void scrollBottomListView() {

        lv_listComments.post(() -> lv_listComments.getRefreshableView().setSelection(adapter.getCount()));
    }

    private void setDataResult() {
        ArrayList<ItemClassComments> arrResult = new ArrayList<>();

        int count = listComment.size();
        if (count > Constants.NUMBER_COMMENT_SHOW) {
            count = Constants.NUMBER_COMMENT_SHOW;
        }

//        if (mHasGivenComment) {
        for (int i = listComment.size() - count; i < listComment.size() && i >= 0; i++) {
            arrResult.add(listComment.get(i));
        }
//        }

        Intent intent = getIntent();
        intent.putExtra(ConstantBundleKey.BUNDLE_COMMENT_POSITION, positionOnListview);
        intent.putExtra(ConstantBundleKey.BUNDLE_COMMENT_COUNT, commentCounts);
        intent.putParcelableArrayListExtra(ConstantBundleKey.BUNDLE_LIST_COMMENT, arrResult);
        intent.putExtra(ConstantBundleKey.BUNDLE_POST_ID_KEY, postId);
        intent.putExtra(ConstantBundleKey.BUNDLE_COMMENT_TYPE, mCommentType);

        setResult(RESULT_OK, intent);

        Utils.hideSoftKeyboard(CommentActivity.this);
        finish();

    }

    private void addComment() {
        AddCommentRequestModel request = new AddCommentRequestModel();
//        if (mCommentType == Constants.COMMENT_TYPE_STREAM){
//            request.setStreamId(Integer.parseInt(postId));
//        }else {
        request.setPostId(postId);
//        }
        request.setMessage(commentsString);
        request.setType(mCommentType);
        request.setTaggedUsers(edt_input_comments.getStringTaggedUsersIdAndClear());

        mCompositeSubscription.add(AppsterWebServices.get().addComment("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(addCommentResponseModel -> {
                    if (addCommentResponseModel == null) {
                        return;
                    }

                    if (addCommentResponseModel.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        handleError(addCommentResponseModel.getMessage(), addCommentResponseModel.getCode());

                        return;
                    }

                    commentCounts = addCommentResponseModel.getData().getCommentCount();
                    if (!listComment.isEmpty()) {
                        listComment.get(listComment.size() - 1).setId(addCommentResponseModel.getData().commentId);
                    }
                }, error -> handleError(error.getMessage(), Constants.RETROFIT_ERROR)));
    }

    public void getData(int index, final boolean isViewProgressGetData) {

        if (isViewProgressGetData) {
            DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));
        }

        final Subscriber<BaseResponse<BaseDataPagingResponseModel<ItemClassComments>>> subscriber =
                new Subscriber<BaseResponse<BaseDataPagingResponseModel<ItemClassComments>>>() {
                    @Override
                    public void onCompleted() {
                        if (lv_listComments != null) {
                            lv_listComments.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewProgressGetData) {
                            DialogManager.getInstance().dismisDialog();
                        }
                        if (lv_listComments != null) {
                            lv_listComments.onRefreshComplete();
                        }
                        onGetDataFailed(e.getMessage(), Constants.RETROFIT_ERROR);
                    }

                    @Override
                    public void onNext(BaseResponse<BaseDataPagingResponseModel<ItemClassComments>> commentListResponseModel) {
                        if (isViewProgressGetData) {
                            DialogManager.getInstance().dismisDialog();
                        }

                        if (commentListResponseModel.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            onGetDataFailed(commentListResponseModel.getMessage(), commentListResponseModel.getCode());

                        } else {
                            onGetDataSuccessfully(commentListResponseModel.getData().getResult(),
                                    commentListResponseModel.getData().getNextId(),
                                    commentListResponseModel.getData().isEnd(),
                                    commentListResponseModel.getData().getTotalRecords());
                        }
                    }
                };

        if (mCommentType == Constants.COMMENT_TYPE_STREAM) {
            mCompositeSubscription.add(AppsterWebServices.get().getRecordedCommentList("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), mSlug, index, Constants.PAGE_LIMITED)
                    .subscribe(subscriber));
        } else {
            CommentListRequestModel request = new CommentListRequestModel();
            request.setNextId(index);
            request.setPostId(postId);
            mCompositeSubscription.add(AppsterWebServices.get().getCommentList("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                    .subscribe(subscriber));
        }

    }

    private void deleteComment(int position) {
        DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));
        DeleteCommentRequestModel requestModel = new DeleteCommentRequestModel();
        requestModel.commentId = listComment.get(position).getId();
        mCompositeSubscription.add(AppsterWebServices.get().deleteComment("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), requestModel)
                .subscribe(deleteCommentResponseModel -> {
                    DialogManager.getInstance().dismisDialog();
                    if (deleteCommentResponseModel == null) {
                        return;
                    }

                    if (deleteCommentResponseModel.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        handleError(deleteCommentResponseModel.getMessage(), deleteCommentResponseModel.getCode());
                        return;
                    }

                    listComment.remove(position);
                    adapter.notifyDataSetChanged();
                    mHasGivenComment = true;
                    commentCounts--;

                }, error -> {
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                    DialogManager.getInstance().dismisDialog();
                }));
    }

    private void onGetDataSuccessfully(List<ItemClassComments> arrComments, int index, boolean isEnd, int commentCount) {
        if (arrComments != null && !arrComments.isEmpty()) {
            if (indexPage == 0) {
                listComment.clear();
            }
            listComment.addAll(0, arrComments);
            adapter.notifyDataSetChanged();
            scrollBottomListView();
            indexPage = index;
            isTheEnd = isEnd;
            commentCounts = commentCount;
        }
    }

    private void onGetDataFailed(String errorMessage, int errorCode) {
        handleError(errorMessage, errorCode);
    }

    @Override
    public void onDeleteComment(int position) {
        if (listComment != null && !listComment.isEmpty()) {
            deleteComment(position);
        }
    }

    @Override
    public void onRowClick(int position) {
        Utils.hideSoftKeyboard(CommentActivity.this);
    }

    @OnClick(R.id.ll_owner)
    public void onListViewClick(View view){
        Utils.hideSoftKeyboard(CommentActivity.this);
    }
}
