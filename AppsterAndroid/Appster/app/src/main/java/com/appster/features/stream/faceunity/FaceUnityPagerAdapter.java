package com.appster.features.stream.faceunity;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.appster.R;
import com.appster.models.FaceUnityStickerModel;
import com.apster.common.UiUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by linh on 06/07/2017.
 */

public class FaceUnityPagerAdapter extends PagerAdapter implements FaceUnityStickerRcvAdapter.OnStickerItemSelectedListener {
    public static final int MAX_ITEM_PER_PAGE = 8;
    private static int MAX_ITEM_POSITION_IN_PAGE = MAX_ITEM_PER_PAGE - 1;
    private static int GRID_COLUMN_COUNT = 4;
    private List<FaceUnityStickerModel> items;
    private int countPager;
    private int orderItemGift = 0;
    private Context mContext;
    FaceUnityStickerRcvAdapter adapterSendGift;
    private boolean mIsTransparentBackground = true;
    private FaceUnityStickerModel mSelectedSticker;
    private int mSelectPageNumber, mSelectedPosition;
    private SparseArray<FaceUnityStickerRcvAdapter> mAdapterSparseArray;
    OnStickerItemSelectedListener mListener;

    public FaceUnityPagerAdapter(Context context, int countPager, List<FaceUnityStickerModel> list, OnStickerItemSelectedListener listener, FaceUnityStickerModel selectedSticker) {
        this.items = list;
        this.mContext = context;
        this.countPager = countPager;
        mAdapterSparseArray = new SparseArray<>();
        this.mListener = listener;
        this.mSelectedSticker = selectedSticker;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_dialog_sendgift, container, false);

        List<FaceUnityStickerModel> unityStickerModels = new ArrayList<>();
        for (int i = 0; i <= MAX_ITEM_POSITION_IN_PAGE; i++) {
            if (items.size() > orderItemGift) {
                unityStickerModels.add(items.get(orderItemGift));
                orderItemGift++;
            }
        }
        RecyclerView stickerRecyclerView = (RecyclerView) view.findViewById(R.id.grid_send_gift);
        int space = (int) mContext.getResources().getDimension(R.dimen.gift_item_offset);
        UiUtils.GridSpacingItemDecoration gridSpacingItemDecoration = new UiUtils.GridSpacingItemDecoration(MAX_ITEM_PER_PAGE, space, true);
//        if(!mIsPrivateChat) gridSpacingItemDecoration.setNoTopEdge(true);
        gridSpacingItemDecoration.setupBorderPaint(mContext.getResources().getColor(R.color.gift_grid_divider), space);
        int pageNumber = (orderItemGift / MAX_ITEM_PER_PAGE);//start counting from 0
        if (orderItemGift % MAX_ITEM_PER_PAGE > 0) {
            pageNumber++;
        }
        if (mIsTransparentBackground) {
            stickerRecyclerView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.live_gift_recyclerview_border_bg));
        }
        adapterSendGift = new FaceUnityStickerRcvAdapter(mContext, stickerRecyclerView, unityStickerModels, pageNumber, this);
        mAdapterSparseArray.put(pageNumber, adapterSendGift);
        highLightSelectedItem(unityStickerModels, pageNumber);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, GRID_COLUMN_COUNT);
        stickerRecyclerView.addItemDecoration(gridSpacingItemDecoration);
        stickerRecyclerView.setAdapter(adapterSendGift);
        stickerRecyclerView.setLayoutManager(gridLayoutManager);

        container.addView(view);

        return view;
    }

    private void highLightSelectedItem(List<FaceUnityStickerModel> unityStickerModels, int pageNumber) {
        final int listSize = unityStickerModels != null ? unityStickerModels.size() : 0;
        if (mSelectedSticker == null) {
            for (int i = 0; i < listSize; i++) {
                final FaceUnityStickerModel sticker = unityStickerModels.get(i);
                if (sticker.getType() == FaceUnityStickerModel.TYPE_NONE) {
                    sticker.setSelected(true);
                    mSelectPageNumber = pageNumber;
                    mSelectedPosition = i;
                    break;
                }
            }
        } else {
            for (int i = 0; i < listSize; i++) {
                final FaceUnityStickerModel sticker = unityStickerModels.get(i);
                if (mSelectedSticker.getId() == sticker.getId() && sticker.getType() == mSelectedSticker.getType()) {
                    sticker.setSelected(true);
                    mSelectPageNumber = pageNumber;
                    mSelectedPosition = i;
                    break;
                }
            }
        }
    }

    public void setBackgroudTransparent(boolean transparent) {
        mIsTransparentBackground = transparent;
    }

    @Override
    public int getCount() {
        return countPager;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void onGiftItemSelected(FaceUnityStickerModel item, int pageNumber, int position) {
        mSelectedSticker = item;
        unSelectItem(mSelectPageNumber, mSelectedPosition);
        mSelectPageNumber = pageNumber;
        mSelectedPosition = position;
        if (mListener != null) {
            mListener.onStickerItemSelected(item);
        }
    }

    public FaceUnityStickerModel getSelectedItem() {
        return mSelectedSticker;
    }

    private void unSelectItem(int previousPageNumber, int previousSelection) {
        Timber.e("previousPageNumber %d - previousSelection %d", previousPageNumber, previousSelection);
        mAdapterSparseArray.get(previousPageNumber).unSelectItem(previousSelection);
    }

    public void notifySelectedAdapteItemChanged() {
        final FaceUnityStickerRcvAdapter faceUnityStickerRcvAdapter = mAdapterSparseArray.get(mSelectPageNumber);
        if (faceUnityStickerRcvAdapter != null)
            faceUnityStickerRcvAdapter.notifyDataSetChanged();
    }

    public interface OnStickerItemSelectedListener {
        void onStickerItemSelected(FaceUnityStickerModel item);
    }
}
