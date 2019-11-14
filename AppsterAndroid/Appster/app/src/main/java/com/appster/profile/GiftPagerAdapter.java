package com.appster.profile;

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
import com.appster.sendgift.GiftItemModel;
import com.apster.common.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThanhBan on 12/20/2016.
 */

public class GiftPagerAdapter extends PagerAdapter implements GiftRecyclerViewAdapter.OnGiftSelectedListener {
    public static final int MAX_ITEM_PER_PAGE = 8;
    private int MAX_ITEM_POSITION_IN_PAGE = MAX_ITEM_PER_PAGE -1;
    private int GRID_COLUMN_COUNT = 4;
    private LayoutInflater layoutInflater;
    private List<GiftItemModel> arrayList_send;
    private int countPager;
    private int orderItemGift = 0;
    private Context mContext;
    GiftRecyclerViewAdapter adapterSendGift;
    private boolean mIsTransparentBackground = true;
    private GiftItemModel mSelectedGift;
    private boolean mIsPrivateChat;
    private int mSelectPageNumber, mSelectedPosition;
    private SparseArray<GiftRecyclerViewAdapter> mAdapterSparseArray;
    public GiftPagerAdapter(Context context, int countPager, List<GiftItemModel> array_send) {
        initData(context,countPager, array_send);
    }

    private void initData(Context context, int countPager, List<GiftItemModel> array_send) {
        this.arrayList_send = array_send;
        this.mContext=context;
        this.countPager = countPager;
        mAdapterSparseArray = new SparseArray<>();
    }

    public GiftPagerAdapter(Context context, int countPager, List<GiftItemModel> array_send, boolean isPrivateChat) {
        initData(context,countPager, array_send);
        this.mIsPrivateChat=isPrivateChat;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_dialog_sendgift,
                container, false);

        ArrayList<GiftItemModel> array_send = new ArrayList<>();

        for (int i = 0; i <= MAX_ITEM_POSITION_IN_PAGE; i++) {
            if (arrayList_send.size() > orderItemGift) {
                array_send.add(arrayList_send.get(orderItemGift));
                orderItemGift++;
            }
        }
        RecyclerView grid_send_gift = (RecyclerView) view.findViewById(R.id.grid_send_gift);
        int space = (int) mContext.getResources().getDimension(R.dimen.gift_item_offset);
        UiUtils.GridSpacingItemDecoration gridSpacingItemDecoration = new UiUtils.GridSpacingItemDecoration(MAX_ITEM_PER_PAGE, space, true);
        if(!mIsPrivateChat) gridSpacingItemDecoration.setNoTopEdge(true);
        gridSpacingItemDecoration.setupBorderPaint(mContext.getResources().getColor(R.color.gift_grid_divider), space);
        int pageNumber = (orderItemGift / MAX_ITEM_PER_PAGE) -1;//start counting from 0
        if (orderItemGift % MAX_ITEM_PER_PAGE > 0){
            pageNumber++;
        }
        if(mIsTransparentBackground){
            grid_send_gift.setBackground(ContextCompat.getDrawable(mContext,R.drawable.live_gift_recyclerview_border_bg));
        }
        adapterSendGift = new GiftRecyclerViewAdapter(mContext, grid_send_gift, array_send, pageNumber);
        adapterSendGift.setBackgroundTransparent(mIsTransparentBackground);
        adapterSendGift.setOnItemSelectedListener(this);
        mAdapterSparseArray.put(pageNumber,adapterSendGift);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, GRID_COLUMN_COUNT);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return adapterSendGift.isViewProgress(position-1) ? gridLayoutManager.getSpanCount() : 1;
//            }
//        });
        if(!mIsTransparentBackground) {
            grid_send_gift.addItemDecoration(gridSpacingItemDecoration);
        }
        grid_send_gift.setAdapter(adapterSendGift);
        grid_send_gift.setLayoutManager(gridLayoutManager);

        container.addView(view);

        return view;
    }

    public void setBackgroudTransparent(boolean transparent){
       mIsTransparentBackground =transparent;
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

    public GiftItemModel getSelectedItem() {
        return mSelectedGift;
    }

    @Override
    public void onGiftItemSelected(GiftItemModel item, int pageNumber, int position) {
        mSelectedGift =item;
        unSelectItem(mSelectPageNumber,mSelectedPosition);
        mSelectPageNumber = pageNumber;
        mSelectedPosition = position;
    }

    private void unSelectItem(int previousPageNumber, int previousSelection) {
        mAdapterSparseArray.get(previousPageNumber).unSelectItem(previousSelection);
    }

    public void notifySelectedAdapteItemChanged(){
        mAdapterSparseArray.get(mSelectPageNumber).notifyItemChanged(mSelectedPosition);
    }
}