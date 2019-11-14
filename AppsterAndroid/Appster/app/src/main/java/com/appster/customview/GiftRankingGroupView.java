package com.appster.customview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.appster.R;
import com.appster.models.DailyTopFanModel;
import com.appster.models.UserModel;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ImageLoaderUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by linh on 26/01/2018.
 */

public class GiftRankingGroupView extends LinearLayout {
    @Bind(R.id.rl_Rank_1)
    RelativeLayout mRlRank1;
    @Bind(R.id.img_rank_1)
    CircleImageView mImgRank1;
    @Bind(R.id.txt_display_name_rank_1)
    CustomFontTextView mTxtDisplayNameRank1;
    @Bind(R.id.txt_stars_rank_1)
    CustomFontTextView mTxtStarsRank1;

    @Bind(R.id.rl_Rank_2)
    RelativeLayout mRlRank2;
    @Bind(R.id.img_rank_2)
    CircleImageView mImgRank2;
    @Bind(R.id.txt_display_name_rank_2)
    CustomFontTextView mTxtDisplayNameRank2;
    @Bind(R.id.txt_stars_rank_2)
    CustomFontTextView mTxtStarsRank2;

    @Bind(R.id.rl_Rank_3)
    RelativeLayout mRlRank3;
    @Bind(R.id.img_rank_3)
    CircleImageView mImgRank3;
    @Bind(R.id.txt_display_name_rank_3)
    CustomFontTextView mTxtDisplayNameRank3;
    @Bind(R.id.txt_stars_rank_3)
    CustomFontTextView mTxtStarsRank3;

    DailyTopFanModel mRank1;
    DailyTopFanModel mRank2 = null;
    DailyTopFanModel mRank3 = null;

    RankingClickListener mListener;


    public GiftRankingGroupView(Context context) {
        super(context);
        constructor(context, null);
    }

    public GiftRankingGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        constructor(context, attrs);
    }

    public GiftRankingGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructor(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GiftRankingGroupView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        constructor(context, attrs);
    }

    private void constructor(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_group_gift_ranking, this, true);
        ButterKnife.bind(this);
        setVisibility(GONE);
    }

    public void updateRankingList(List<DailyTopFanModel> dailyTopFansList) {
        if (dailyTopFansList == null || dailyTopFansList.isEmpty()) {
//            setVisibility(INVISIBLE);
            return;
        }
        if (dailyTopFansList.size() > 2) updateRank3(dailyTopFansList.get(2));
        if (dailyTopFansList.size() > 1) updateRank2(dailyTopFansList.get(1));
        updateRank1(dailyTopFansList.get(0));
        setVisibility(VISIBLE);
    }

    @SuppressLint("RxLeakedSubscription")
    public void updateRank1(DailyTopFanModel rank1) {
        if (rank1 == null) return;
        if (mRank1 != null && mRank1.userName.equalsIgnoreCase(rank1.userName) && mRank1.giftGold >= rank1.giftGold)
            return;
        updateViewRank1(rank1);
    }

    @SuppressLint("RxLeakedSubscription")
    public void updateRank2(DailyTopFanModel rank2) {
        if (rank2 == null) return;
        if (mRank2 != null && mRank2.userName.equalsIgnoreCase(rank2.userName) && mRank2.giftGold >= rank2.giftGold)
            return;
        updateViewRank2(rank2);
    }

    @SuppressLint("RxLeakedSubscription")
    public void updateRank3(DailyTopFanModel rank3) {
        if (rank3 == null) return;
        if (mRank3 != null && mRank3.userName.equalsIgnoreCase(rank3.userName) && mRank3.giftGold >= rank3.giftGold)
            return;
        updateViewRank3(rank3);
//        Observable.fromCallable(() -> isUserAlreadyInStream(rank3.userName))
//                .observeOn(Schedulers.computation())
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .filter(alreadyInStream -> alreadyInStream)
//                .subscribe(aBoolean -> {
//                    mRank3 = rank3;
//                    updateViewRank3(rank3);
//                });
    }

//    private boolean isUserAlreadyInStream(String userName){
//        if (TextUtils.isEmpty(userName)) return false;
//        for (String usr : mChatManager.getArrayCurrentUserInStream()){
//            if (userName.equalsIgnoreCase(usr)) return true;
//        }
//        return false;
//    }

    public void updateViewRank1(DailyTopFanModel rank1) {
        if (rank1 == null) {
            mRlRank1.setVisibility(GONE);
            mImgRank1.setImageResource(0);
            mTxtDisplayNameRank1.setText("");
            mTxtStarsRank1.setText("0");
        } else if (mRank1 != null && mRank1.userName.equalsIgnoreCase(rank1.userName)) {
            mTxtStarsRank1.setText(rank1.giftGoldString);
            mRlRank1.setVisibility(VISIBLE);
        } else {
            mRlRank1.setVisibility(GONE);
            ImageLoaderUtil.displayUserImage(getContext(), UserModel.getUserImageThumbByUserNameAndTime(rank1.userName), mImgRank1);
            mTxtDisplayNameRank1.setText(rank1.displayName);
            mTxtStarsRank1.setText(rank1.giftGoldString);
            mRlRank1.setVisibility(VISIBLE);
        }
        mRank1 = rank1;
    }

    public void updateViewRank2(DailyTopFanModel rank2) {
        if (rank2 == null) {
            mRlRank2.setVisibility(GONE);
            mImgRank2.setImageResource(0);
            mTxtDisplayNameRank2.setText("");
            mTxtStarsRank2.setText("0");

        } else if (mRank2 != null && mRank2.userName.equalsIgnoreCase(rank2.userName)) {
            mTxtStarsRank2.setText(rank2.giftGoldString);
            mRlRank2.setVisibility(VISIBLE);
        } else {
            mRlRank2.setVisibility(GONE);
            ImageLoaderUtil.displayUserImage(getContext(), UserModel.getUserImageThumbByUserNameAndTime(rank2.userName), mImgRank2);
            mTxtDisplayNameRank2.setText(rank2.displayName);
            mTxtStarsRank2.setText(rank2.giftGoldString);
            mRlRank2.setVisibility(VISIBLE);
        }
        mRank2 = rank2;
    }

    public void updateViewRank3(DailyTopFanModel rank3) {
        if (rank3 == null) {
            mRlRank3.setVisibility(GONE);
            mImgRank3.setImageResource(0);
            mTxtDisplayNameRank3.setText("");
            mTxtStarsRank3.setText("0");
        } else if (mRank3 != null && mRank3.userName.equalsIgnoreCase(rank3.userName)) {
            mTxtStarsRank3.setText(rank3.giftGoldString);
            mRlRank3.setVisibility(VISIBLE);
        } else {
            mRlRank3.setVisibility(GONE);
            ImageLoaderUtil.displayUserImage(getContext(), UserModel.getUserImageThumbByUserNameAndTime(rank3.userName), mImgRank3);
            mTxtDisplayNameRank3.setText(rank3.displayName);
            mTxtStarsRank3.setText(rank3.giftGoldString);
            mRlRank3.setVisibility(VISIBLE);
        }
        mRank3 = rank3;
    }

    public void onUserJoinedStream(String username) {
        if (TextUtils.isEmpty(username)) return;
        if (mRank1 != null && username.equalsIgnoreCase(mRank1.userName)) {
            updateViewRank1(mRank1);
        } else if (mRank2 != null && username.equalsIgnoreCase(mRank2.userName)) {
            updateViewRank2(mRank2);
        } else if (mRank3 != null && username.equalsIgnoreCase(mRank3.userName)) {
            updateViewRank3(mRank3);
        }
    }

    public void onUserLeftStream(String username) {
        if (TextUtils.isEmpty(username)) return;
        if (mRank1 != null && username.equalsIgnoreCase(mRank1.userName)) {
            mRank1 = null;
            updateViewRank1(mRank1);
        } else if (mRank2 != null && username.equalsIgnoreCase(mRank2.userName)) {
            mRank2 = null;
            updateViewRank2(mRank2);
        } else if (mRank3 != null && username.equalsIgnoreCase(mRank3.userName)) {
            mRank3 = null;
            updateViewRank3(mRank3);
        }
    }

    public void clearRanks() {
        mRank1 = null;
        mRank2 = null;
        mRank3 = null;
        updateViewRank1(mRank1);
        updateViewRank2(mRank2);
        updateViewRank3(mRank3);
    }

    public void setListener(RankingClickListener listener) {
        mListener = listener;
    }

    @OnClick({R.id.img_rank_1, R.id.img_rank_2, R.id.img_rank_3})
    public void onViewClicked(View view) {
        if (mListener == null) return;
        AppsterUtility.temporaryLockView(view);
        switch (view.getId()) {
            case R.id.img_rank_1:
                if (mRank1 != null) mListener.onRankingProfileClicked(mRank1.userName);
                break;
            case R.id.img_rank_2:
                if (mRank2 != null) mListener.onRankingProfileClicked(mRank2.userName);
                break;
            case R.id.img_rank_3:
                if (mRank3 != null) mListener.onRankingProfileClicked(mRank3.userName);
                break;
            default:
                break;
        }
    }

    public interface RankingClickListener {
        void onRankingProfileClicked(String userName);
    }
}
