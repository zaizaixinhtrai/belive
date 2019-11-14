package com.appster.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.appster.AppsterApplication;
import com.appster.comments.ItemClassComments;
import com.appster.main.MainActivity;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.ListenerEventModel;
import com.appster.models.NewCommentEventModel;
import com.appster.models.NewLikeEventModel;
import com.appster.models.ReportEvent;
import com.appster.profile.FragmentMe;
import com.appster.utility.ConstantBundleKey;
import com.apster.common.Constants;

import java.util.ArrayList;

/**
 * Created by sonnguyen on 10/13/15.
 */
public class UserProfileActivity extends BaseToolBarActivity {

    public static final String ARG_USER_BLOCKED = "ARG_USER_BLOCKED";

    String userID = "";
    String mUserName = "";
    String displayName = "";
    FragmentMe fragmentMe;
    private ArrayList<NewCommentEventModel> arrNewCommentEvnt;
    private ArrayList<NewLikeEventModel> arrNewLikeEvent;
    private ArrayList<ReportEvent> arrReport;
    private FollowStatusChangedEvent followStatusChangedEvent;

    public static Intent newIntent(Context context, String userId, String displayName) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(Constants.USER_PROFILE_ID, userId);
        intent.putExtra(Constants.USER_PROFILE_DISPLAYNAME, displayName);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBundle();
        if (savedInstanceState == null) {
            fragmentMe = FragmentMe.getInstance(userID, mUserName, false, displayName);
            showFullFragmentScreen(fragmentMe);
        }
        arrNewCommentEvnt = new ArrayList<>();
        arrNewLikeEvent = new ArrayList<>();
        arrReport = new ArrayList<>();

        if (AppsterApplication.mAppPreferences.getUserModel() != null && AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(userID))
            setImageEditProfile();
    }

    private void initBundle() {
        Bundle bundle = getIntent().getExtras();
        displayName = bundle.getString(Constants.USER_PROFILE_DISPLAYNAME);
        userID = bundle.getString(Constants.USER_PROFILE_ID);
        mUserName = bundle.getString(Constants.ARG_USER_NAME);
    }

    @Override
    public int getLayoutContentId() {
        return 0;
    }

    @Override
    public void init() {
        goneNotify(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        handleToolbar(true);
        handleTurnoffMenuSliding();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_MEDIA_PLAYER_STREAM && fragmentMe != null) {
            boolean goHome = false;
            if (data != null) {
                goHome = data.getBooleanExtra(ConstantBundleKey.BUNDLE_GO_HOME, false);
            }
            if (goHome) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(ConstantBundleKey.BUNDLE_GO_HOME, true);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }

        if (resultCode == RESULT_CANCELED)
            return;

        if (fragmentMe != null && resultCode == RESULT_OK) {
            fragmentMe.onActivityResult(requestCode, resultCode, data);
        }

        switch (requestCode) {
            case Constants.COMMENT_REQUEST:
                if (data != null) {
                    getComment(data);
                }
                break;

            case Constants.REQUEST_CODE_VIEW_POST_DETAIL:

                if (data != null) {
                    followStatusChangedEvent = data.getParcelableExtra(ConstantBundleKey.BUNDLE_PROFILE_CHANGE_FOLLOW_USER);
                }
                break;
        }
    }

    private void getComment(Intent data) {

        Bundle extras = data.getExtras();
        if (extras != null) {

            String idPost = String.valueOf(extras.getInt(ConstantBundleKey.BUNDLE_POST_ID_KEY));
            ArrayList<ItemClassComments> arrComment = extras.getParcelableArrayList(ConstantBundleKey.BUNDLE_LIST_COMMENT);

            if (arrComment != null && arrComment.size() > 0) {
                NewCommentEventModel newCommentEventModel = new NewCommentEventModel();
                newCommentEventModel.setArrComment(arrComment);
                newCommentEventModel.setPostId(idPost);
                arrNewCommentEvnt.add(newCommentEventModel);
            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = getIntent();

        if (arrNewLikeEvent.size() > 0) {
            intent.putParcelableArrayListExtra(ConstantBundleKey.BUNDLE_DATA_LIST_LIKE_FROM_PROFILE_ACTIVITY, arrNewLikeEvent);
        }

        if (arrNewCommentEvnt.size() > 0) {
            intent.putParcelableArrayListExtra(ConstantBundleKey.BUNDLE_DATA_LIST_COMMENT_FROM_PROFILE_ACTIVITY, arrNewCommentEvnt);
        }

        if (followStatusChangedEvent != null) {
            intent.putExtra(ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY, followStatusChangedEvent);
        }
        if (arrReport.size() > 0) {
            intent.putExtra(ConstantBundleKey.BUNDLE_DATA_REPORT_FROM_PROFILE_ACTIVITY, arrReport);
        }
        if (arrNewLikeEvent.size() > 0 || arrNewCommentEvnt.size() > 0 || followStatusChangedEvent != null || arrReport.size() > 0) {
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void eventChange(ListenerEventModel listenerEventModel) {

        if (listenerEventModel == null) {
            return;
        }

        if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.NEW_LIKE) {
            NewLikeEventModel newLikeEventModel = listenerEventModel.getNewLikeEventModel();
            arrNewLikeEvent.add(newLikeEventModel);
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.FOLLOW_USER) {
            followStatusChangedEvent = listenerEventModel.getFollowStatusChangedEvent();
        } else if (listenerEventModel.getTypeEvent() == ListenerEventModel.TypeEvent.EVENT_REPORT) {
            ReportEvent reportEvent = listenerEventModel.getReportEvent();
            arrReport.add(reportEvent);

        }
    }
}
