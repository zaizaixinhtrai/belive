package com.appster.features.stream.faceunity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.appster.R;
import com.appster.dialog.ImmersiveDialogFragment;
import com.appster.layout.WrapContentHeightViewPager;
import com.appster.models.FaceUnityStickerModel;
import com.appster.utility.RxUtils;
import com.apster.common.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by linh on 06/07/2017.
 */

public class FaceUnityDialogPicker extends ImmersiveDialogFragment {
    @Bind(R.id.viewPager)
    WrapContentHeightViewPager mViewPager;
    @Bind(R.id.viewPagerCountDots)
    LinearLayout dotsLayout;

    FaceUnityPagerAdapter mViewPagerAdapter;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener;

    private ImageView[] dots;
    private int dotsCount;
    List<FaceUnityStickerModel> mFaceUnityStickerList;
    FaceUnityPagerAdapter.OnStickerItemSelectedListener mListener;
    Subscription mSubscription;


    protected
    @Nullable
    FaceUnityStickerModel mSelectedStickerModel;

    public static FaceUnityDialogPicker newInstance() {
        Bundle args = new Bundle();
        FaceUnityDialogPicker fragment = new FaceUnityDialogPicker();
        fragment.setArguments(args);
        return fragment;
    }

    public FaceUnityDialogPicker() {
        mFaceUnityStickerList = new ArrayList<>();
    }

    public void setFaceUnityStickerList(List<FaceUnityStickerModel> faceUnityStickerList) {
        mFaceUnityStickerList = faceUnityStickerList;
        prepareItems();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewPagerItemsWithAdapter();
        setUiPageViewController();
    }

    private void prepareItems() {
        //non Item
        FaceUnityStickerModel item = new FaceUnityStickerModel();
        item.setType(FaceUnityStickerModel.TYPE_NONE);
        item.setAvailable(true);
        mFaceUnityStickerList.add(0, item);

        int size = mFaceUnityStickerList.size() % FaceUnityPagerAdapter.MAX_ITEM_PER_PAGE;

        int requireItems = FaceUnityPagerAdapter.MAX_ITEM_PER_PAGE;
        if (size > 0 && size < requireItems) {
            for (int i = size; i < requireItems; i++) {
                item = new FaceUnityStickerModel();
                item.setType(FaceUnityStickerModel.TYPE_DUMP);
                mFaceUnityStickerList.add(item);
            }
        }
    }

    public void setSelectedStickerModel(@Nullable FaceUnityStickerModel selectedStickerModel) {
        mSelectedStickerModel = selectedStickerModel;
    }

    private void setUiPageViewController() {
        if (dotsCount <= 1) return;
        dots = new ImageView[dotsCount];

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        params.height = 5;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(Utils.dpToPx(5), 0, 0, 0);

        for (int i = 0; i < dotsCount; i++) {
            if (getActivity() == null) return;
            dots[i] = new ImageView(getActivity());
            dots[i].setLayoutParams(lp);
            dots[i].setBackgroundResource(R.drawable.circle_dot_gift);
            dotsLayout.addView(dots[i]);
        }

        dots[0].setBackgroundResource(R.drawable.circle_dot_gift_red);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if(isAdded()) return;
        super.show(manager, tag);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume");
    }

    @Override
    public void dismiss() {
        if(mViewPager!=null) mViewPager.setCurrentItem(0);
        super.dismiss();
        RxUtils.unsubscribeIfNotNull(mSubscription);
    }

    @Override
    protected int getRootLayoutResource() {
        return R.layout.dialog_picker_face_unity;
    }

    @Override
    protected boolean isDimDialog() {
        return true;
    }

    @Override
    protected int getWindowAnimation() {
        return R.style.DialogFadeAnimation;
    }

    //======== event handlers ======================================================================
    @OnClick(R.id.root_view)
    public void onRootViewClicked() {
        dismiss();
    }

    //========== inner methods =====================================================================
    public void setListener(FaceUnityPagerAdapter.OnStickerItemSelectedListener listener) {
        mListener = listener;
    }

    private void setViewPagerItemsWithAdapter() {
        if (mViewPagerAdapter == null && viewPagerPageChangeListener == null) {
            viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < dotsCount; i++) {
                        dots[i].setBackgroundResource(R.drawable.circle_dot_gift);
                    }
                    dots[position].setBackgroundResource(R.drawable.circle_dot_gift_red);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            };
        }

        int listSize = mFaceUnityStickerList.size();
        int residual = listSize % 8;
        if (residual == 0) {
            dotsCount = listSize / 8;
        } else {
            dotsCount = listSize / 8 + 1;
        }
        mViewPagerAdapter = new FaceUnityPagerAdapter(getActivity(), dotsCount, mFaceUnityStickerList, mListener, mSelectedStickerModel);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        mViewPager.setOffscreenPageLimit(dotsCount);

    }

    public void notifyDataChanged(int stickerId) {
        if(mViewPagerAdapter!=null) mViewPagerAdapter.notifySelectedAdapteItemChanged();
    }
}
