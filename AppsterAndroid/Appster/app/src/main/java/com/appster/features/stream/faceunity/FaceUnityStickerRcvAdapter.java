package com.appster.features.stream.faceunity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.adapters.BaseRecyclerViewLoadMore;
import com.appster.customview.CustomFontTextView;
import com.appster.layout.SquareImageView;
import com.appster.models.FaceUnityStickerModel;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Constants;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by linh on 06/07/2017.
 */

public class FaceUnityStickerRcvAdapter extends BaseRecyclerViewLoadMore<FaceUnityStickerRcvAdapter.FaceUnityStickerViewHolder, FaceUnityStickerModel> {
    private Context mContext;
    private List<FaceUnityStickerModel> mStickerModelList;
    private int pageNumber;

    OnStickerItemSelectedListener mListener;
    private FaceUnityStickerModel mCurrentSelectedSticker;

    public FaceUnityStickerRcvAdapter(Context context, RecyclerView recyclerView, List<FaceUnityStickerModel> objects, int pageNumber, OnStickerItemSelectedListener listener) {
        super(recyclerView, objects);
        this.mContext = context;
        this.mStickerModelList = objects;
        this.pageNumber = pageNumber;
        this.mListener = listener;
        Timber.e("pageNumber ->" + pageNumber);
    }

    @Override
    public FaceUnityStickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FaceUnityStickerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_face_unity_sticker, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FaceUnityStickerViewHolder) {
            FaceUnityStickerViewHolder viewHolder = (FaceUnityStickerViewHolder) holder;
//            int currentPosition = holder.getAdapterPosition();
            FaceUnityStickerModel itemModel = mStickerModelList.get(position);
            ImageLoaderUtil.displayMediaImage(mContext, itemModel.getImage(), viewHolder.imgStickerThumb);
            int type = itemModel.getType();
            if (type == FaceUnityStickerModel.TYPE_DUMP) {
                viewHolder.txtComingSoon.setVisibility(View.INVISIBLE);
                viewHolder.imgStickerThumb.setVisibility(View.INVISIBLE);
                viewHolder.ivDownloadIndicator.setVisibility(View.GONE);
//                viewHolder.imgStickerThumb.setImageResource();
            } else if (type == FaceUnityStickerModel.TYPE_NONE) {
                viewHolder.txtComingSoon.setVisibility(View.INVISIBLE);
                viewHolder.imgStickerThumb.setVisibility(View.VISIBLE);
                viewHolder.ivDownloadIndicator.setVisibility(View.GONE);
                viewHolder.imgStickerThumb.setAlpha(1f);
                viewHolder.imgStickerThumb.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_sticker_none));
                if (itemModel.isSelected()) {
                    viewHolder.itemView.setBackgroundResource(R.drawable.border_image_sendgift_transparent);
                } else {
                    viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
                }
                viewHolder.itemView.setOnClickListener(v -> {
                    if (mListener != null) {
                        mListener.onGiftItemSelected(itemModel, pageNumber, position);
                    }
                    itemModel.setSelected(true);
                    notifyItemChanged(position);
                });
            } else {
                viewHolder.imgStickerThumb.setVisibility(View.VISIBLE);
                if (itemModel.isAvailable()) {
                    if (!itemModel.getFile().isEmpty()) {
                        viewHolder.ivDownloadIndicator.setVisibility(isDownloadedSticker(itemModel.getFile()) ? View.GONE : View.VISIBLE);
                    }
                    viewHolder.txtComingSoon.setVisibility(View.INVISIBLE);
                    viewHolder.imgStickerThumb.setAlpha(1f);
                    if (itemModel.isSelected()) {
                        viewHolder.itemView.setBackgroundResource(R.drawable.border_image_sendgift_transparent);
                    } else {
                        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
                    }
                    viewHolder.itemView.setOnClickListener(v -> {
                        if (mListener != null) {
                            mListener.onGiftItemSelected(itemModel, pageNumber, position);
                        }
                        itemModel.setSelected(true);
                        notifyItemChanged(position);
                    });
                } else {
                    viewHolder.txtComingSoon.setVisibility(View.VISIBLE);
                    viewHolder.imgStickerThumb.setAlpha(0.2f);
                }

            }

        }
    }

    private boolean isDownloadedSticker(String file) {
        return new File(Constants.FILE_CACHE_FOLDER, getFileName(file)).exists();
    }

    private String getFileName(String file) {
        return file.substring(file.lastIndexOf("/"));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mStickerModelList.size();
    }

    public void unSelectItem(int previousSelection) {
        mStickerModelList.get(previousSelection).setSelected(false);
        notifyItemChanged(previousSelection);
    }

    public static class FaceUnityStickerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_sticker_thumb)
        ImageView imgStickerThumb;
        @Bind(R.id.txt_coming_soon)
        CustomFontTextView txtComingSoon;
        @Bind(R.id.ivDownloadIndicator)
        SquareImageView ivDownloadIndicator;

        public FaceUnityStickerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnStickerItemSelectedListener {
        void onGiftItemSelected(FaceUnityStickerModel item, int pageNumber, int position);
    }
}
