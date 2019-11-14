package com.appster.features.user_liked;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.adapters.BaseRecyclerViewLoadMore;
import com.appster.customview.CircleImageView;
import com.appster.domain.LikedUsersItemModel;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;



/**
 * Created by ThanhBan on 9/27/2016.
 */

public class LikedUserAdapter extends BaseRecyclerViewLoadMore<LikedUserAdapter.LikedUserViewHolder, LikedUsersItemModel> implements View.OnClickListener {

    private Context mContext;

    OnLikedUserItemClickListener recyclerItemCallBack;

    public LikedUserAdapter(Context context, RecyclerView recyclerView, ArrayList<LikedUsersItemModel> mModels) {
        super(recyclerView, mModels);
        mContext = context.getApplicationContext();
    }

    @Override
    public void handleItem(LikedUserViewHolder viewHolder, LikedUsersItemModel item, int postiotn) {
        viewHolder.btnFollow.setTag(viewHolder);
        viewHolder.userImage.setTag(viewHolder);
        viewHolder.userName.setTag(viewHolder);
        viewHolder.displayName.setTag(viewHolder);
        viewHolder.bindView(item);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(mContext).inflate(
                    R.layout.liked_user_item, parent, false);
            LikedUserViewHolder holder = new LikedUserViewHolder(v);
            holder.btnFollow.setOnClickListener(this);
            holder.userImage.setOnClickListener(this);
            holder.userName.setOnClickListener(this);
            holder.displayName.setOnClickListener(this);
            return holder;
        } else {
            View v = getProgressBarLayout(parent);

            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LikedUserViewHolder) {

            final LikedUsersItemModel item = mModels.get(position);
            handleItem((LikedUserViewHolder) holder, item, position);

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void addNewList(List<LikedUsersItemModel> likedUsersItemModels) {
        int lastItemPos = getItemCount();
        mModels.addAll(likedUsersItemModels);
        notifyDataSetChanged();

    }

    void clearData(){
        mModels.clear();
        notifyDataSetChanged();
    }

    public void notifyFollowChanged(boolean isFollow, int position) {
        mModels.get(position).setIs_follow(isFollow ? Constants.IS_FOLLOWING_USER : Constants.UN_FOLLOW_USER);
        notifyItemChanged(position);
    }
    public void updateFollow(FollowStatusChangedEvent followStatusChangedEvent){
        if(followStatusChangedEvent!=null){
            if(mModels!=null && mModels.size()>0){
                for(int i= 0;i< mModels.size();i++){
                    if(followStatusChangedEvent.getUserId().equalsIgnoreCase(mModels.get(i).getUserId())){
                        mModels.get(i).setIs_follow(followStatusChangedEvent.getFollowType());
                    }
                }
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        LikedUserViewHolder holder = (LikedUserViewHolder) v.getTag();
        if(v.getId()==R.id.btnFollow) {
            recyclerItemCallBack.onFollowClick(holder.getLikedUserModel(), holder.getAdapterPosition());
        }else{
            recyclerItemCallBack.onProfileImageClick(holder.getLikedUserModel());
        }
    }

    public void setRecyclerItemCallBack(OnLikedUserItemClickListener recyclerItemCallBack) {
        this.recyclerItemCallBack = recyclerItemCallBack;
    }

    static class LikedUserViewHolder extends RecyclerView.ViewHolder {

//        @Bind(R.id.tvNumberOrder)
//        TextView numberOrder;
        @Bind(R.id.ivUserImage)
        CircleImageView userImage;
        @Bind(R.id.tvDisplayName)
        TextView displayName;
        @Bind(R.id.tvUserName)
        TextView userName;
        @Bind(R.id.btnFollow)
        ImageButton btnFollow;

        private LikedUsersItemModel itemModel;


        LikedUserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(LikedUsersItemModel usersItemModel) {
            itemModel = usersItemModel;


//            numberOrder.setText(getAdapterPosition() + 1 + "");
            ImageLoaderUtil.displayUserImage(itemView.getContext(), itemModel.getProfilePic(), userImage);
            displayName.setText(itemModel.getDisplayName());
            userName.setText(String.format("@%s", itemModel.getUserName()));

            if (itemModel.getIs_follow() == Constants.IS_FOLLOWING_USER) {
                btnFollow.setBackgroundResource(R.drawable.btn_following);
            } else {
                btnFollow.setBackgroundResource(R.drawable.btn_follow);
            }

            if(AppsterApplication.mAppPreferences.getUserModel()!=null) {
                if (AppsterApplication.mAppPreferences.getUserModel().getUserId().equalsIgnoreCase(usersItemModel.getUserId())) {
                    btnFollow.setVisibility(View.INVISIBLE);
                    userImage.setClickable(false);
                }
            }
        }

        LikedUsersItemModel getLikedUserModel() {
            return itemModel;
        }
    }

    public interface OnLikedUserItemClickListener{
       void onProfileImageClick(LikedUsersItemModel usersItemModel);
       void onFollowClick(LikedUsersItemModel usersItemModel,int position);
    }
}
