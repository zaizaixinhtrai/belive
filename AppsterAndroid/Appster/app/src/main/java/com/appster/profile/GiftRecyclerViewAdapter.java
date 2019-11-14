package com.appster.profile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.adapters.BaseRecyclerViewLoadMore;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.models.DailyTopFanModel;
import com.appster.sendgift.GiftItemModel;
import com.appster.utility.ImageLoaderUtil;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ThanhBan on 12/20/2016.
 */

public class GiftRecyclerViewAdapter extends BaseRecyclerViewLoadMore<GiftRecyclerViewAdapter.SendGiftHolderVew, GiftItemModel> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
//    private ViewPager viewPager;

    private ArrayList<GiftItemModel> array_send;
//    private int chosenPosition = -1;
    private int pageNumber;
    private int lastChosenPage = -1;

    private boolean itemTransparent = true;

    OnGiftSelectedListener mGiftSelectedListener;
    public GiftRecyclerViewAdapter(Context context, RecyclerView recyclerView, ArrayList<GiftItemModel> objects, int pageNumber) {
        super(recyclerView, objects);
        this.mContext = context;
//        this.viewPager = viewPager;
        this.array_send = objects;
        this.pageNumber = pageNumber;
        Timber.e("pageNumber ->" + pageNumber);
        insertMissingItems(array_send,pageNumber);
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (pageNumber == position){
//                    Timber.e("validateChosenItem %d",position);
//                    validateChosenItem(position);
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

    }

    private void insertMissingItems(ArrayList<GiftItemModel> array_send, int pageNumber) {
        int requireItems = GiftPagerAdapter.MAX_ITEM_PER_PAGE ;
        if(array_send.size()<requireItems){
            for (int i = array_send.size();i<requireItems;i++){
                array_send.add(new GiftItemModel());
            }
        }
    }

    public void setBackgroundTransparent(boolean itemTransparent) {
        this.itemTransparent = itemTransparent;
    }

    @Override
    public SendGiftHolderVew onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.send_gift_row_gridview, parent, false);

        return new SendGiftHolderVew(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SendGiftHolderVew) {
            SendGiftHolderVew giftHolderVew = (SendGiftHolderVew) holder;
            int currentPossition = holder.getAdapterPosition();

            GiftItemModel itemModel = array_send.get(currentPossition);

            if(TextUtils.isEmpty(itemModel.getGiftId())){
                giftHolderVew.txt_bean.setVisibility(View.INVISIBLE);
                if (itemTransparent) {
                    giftHolderVew.itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.live_gift_item_bg));
                }else{
                    giftHolderVew.itemView.setBackgroundColor(Color.WHITE);
                }
                giftHolderVew.itemView.setClickable(false);
                return;
            }
            ImageLoaderUtil.displayMediaImage(mContext, itemModel.getGiftImage(), giftHolderVew.imv_gird_image);



            if (itemModel.isChoose()) {
                if (itemTransparent){
                    giftHolderVew.itemView.setBackgroundResource(R.drawable.border_image_sendgift_transparent);
                }else{
                    giftHolderVew.itemView.setBackgroundResource(R.drawable.border_image_sendgift);
                }
            } else {
                if (itemTransparent) {
                    giftHolderVew.itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.live_gift_item_bg));
                }else{
                    giftHolderVew.itemView.setBackgroundColor(Color.WHITE);
                }
            }

            if(itemModel.getAmount() ==0){
                if(itemModel.getGiftType()!=0){
                    giftHolderVew.txt_bean.setVisibility(View.INVISIBLE);
                }else {
                    giftHolderVew.txt_bean.setVisibility(View.VISIBLE);
                    giftHolderVew.txt_bean.setCustomDrawableStart(R.drawable.refill_gem_icon);
                    giftHolderVew.txt_bean.setText(String.valueOf(itemModel.getCostBean()));
                    if (itemTransparent) {
                        giftHolderVew.txt_bean.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    }
                }

            }else{
                //free gift
                giftHolderVew.txt_bean.setCompoundDrawables(null,null,null,null);
                giftHolderVew.txt_bean.setText(getFreeText(itemModel.getAmount()));
            }


            giftHolderVew.imv_gird_image.setOnClickListener(v -> {


                mGiftSelectedListener.onGiftItemSelected(itemModel,pageNumber,currentPossition);

                itemModel.setChoose(true);
                lastChosenPage = pageNumber;
                notifyItemChanged(currentPossition);
                Timber.e("notified changed %s",currentPossition);
            });
        }
    }

    private CharSequence getFreeText(int amount) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(formatString(mContext.getString(R.string.label_free),Color.parseColor("#ffd460"),Typeface.BOLD))
                    .append(" ")
                    .append(formatString(String.format("x%s",amount),itemTransparent?Color.parseColor("#FFFFFF"):Color.parseColor("#9b9b9b"),Typeface.BOLD));
        return builder.subSequence(0,builder.length());
    }

    private SpannableString formatString(String text, int color,int style) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }

        SpannableString spannableString = new SpannableString(text);

        spannableString.setSpan(new ForegroundColorSpan(color), 0, text.length(), 0);
        spannableString.setSpan(new StyleSpan(style), 0, text.length(), 0);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/opensansbold.ttf");
            spannableString.setSpan(new CustomTypefaceSpan("", font), 0, text.length(), 0);
        return spannableString;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return array_send.size();
    }

    @Override
    public void handleItem(SendGiftHolderVew viewHolder, GiftItemModel item, int postiotn) {

    }

    public void setOnItemSelectedListener(OnGiftSelectedListener listener) {
        this.mGiftSelectedListener = listener;
    }

    public void unSelectItem(int previousSelection) {
        array_send.get(previousSelection).setChoose(false);
        notifyItemChanged(previousSelection);
    }

    public static class SendGiftHolderVew extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imv_gird_image;
        CustomFontTextView txt_bean;
        FrameLayout flGiftItemContainer;

        public SendGiftHolderVew(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imv_gird_image = (ImageView) itemView.findViewById(R.id.imv_gird_image);
            txt_bean = (CustomFontTextView) itemView.findViewById(R.id.txt_bean);
            flGiftItemContainer = (FrameLayout) itemView.findViewById(R.id.flGiftItemContainer);
        }
    }

    public interface CompleteSendGift {
        void onSendSuccess(GiftItemModel ItemSend, long senderTotalBean, long senderTotalGold, long receiverTotalBean, long receiverTotalGoldFans, int votingScores, List<String> topFanList, List<DailyTopFanModel> dailyTopFans);
    }

    public interface OnGiftSelectedListener{
        void onGiftItemSelected(GiftItemModel item, int pageNumber, int position);
    }
}


