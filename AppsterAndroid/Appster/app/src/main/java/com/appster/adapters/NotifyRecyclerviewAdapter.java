package com.appster.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.PostDetailActivity;
import com.appster.activity.TopFanActivity;
import com.appster.activity.UserProfileActivity;
import com.appster.customview.CircleImageView;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.models.NotificationItemModel;
import com.appster.models.UserModel;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Constants;
import com.pack.utility.CheckNetwork;
import com.pack.utility.SetDateTime;
import com.pack.utility.StringUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by User on 5/4/2016.
 */
public class NotifyRecyclerviewAdapter extends BaseRecyclerViewLoadMore<NotifyRecyclerviewAdapter.NotifyHolder, NotificationItemModel> {

    private Context context;
    private int typeFragment;
    Typeface americanFont;

    public NotifyRecyclerviewAdapter(Context context, RecyclerView recyclerView,
                                     List<NotificationItemModel> mModels,
                                     int typeFragment) {
        super(recyclerView, mModels);
        this.context = context;
        this.typeFragment = typeFragment;
        americanFont = Typeface.createFromAsset(context.getAssets(),
                "fonts/opensansbold.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.cardview_notify, parent, false);

            vh = new NotifyHolder(v);
        } else {
            View v = getProgressBarLayout(parent);

            vh = new ProgressViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NotifyHolder) {

            final NotificationItemModel item = mModels.get(position);
            handleItem((NotifyHolder) holder, item, position);

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public void handleItem(NotifyHolder viewHolder, NotificationItemModel model, final int postiotn) {

        String message;

        if (model.getMessage() == null) {
            message = "";
        } else {
            message = model.getMessage();
        }

        ImageLoaderUtil.displayUserImage(context, model.getActionUser().getUserImage(),
                viewHolder.ivNotifyAvatar);

        viewHolder.tvNotifyTime.setText(SetDateTime.partTimeNotification(model.getCreated(), context));

        String displayName = model.getActionUser().getDisplayName();
        String actionUserDisplayName = model.getReceiver() != null ? model.getReceiver().getDisplayName() : "";

        if (model.isIsRead()) {

            if (!StringUtil.isNullOrEmptyString(message)) {

                message = StringUtil.decodeString(message);

                viewHolder.tvNotifyUser.setTypeface(null, Typeface.NORMAL);
                SpannableString spannablecontent = new SpannableString(message);

                if (!StringUtil.isNullOrEmptyString(displayName) && message.contains(displayName)) {
                    spannablecontent.setSpan(getCustomTypefaceSpanForName(),
                            message.indexOf(displayName), message.indexOf(displayName) + displayName.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannablecontent.setSpan(getForegroundColorSpanForName(),
                            message.indexOf(displayName), message.indexOf(displayName) + displayName.length()
                            , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (!StringUtil.isNullOrEmptyString(actionUserDisplayName) && message.contains(actionUserDisplayName)) {
                    spannablecontent.setSpan(getCustomTypefaceSpanForName(),
                            message.indexOf(actionUserDisplayName), message.indexOf(actionUserDisplayName) + actionUserDisplayName.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannablecontent.setSpan(getForegroundColorSpanForName(),
                            message.indexOf(actionUserDisplayName), message.indexOf(actionUserDisplayName) + actionUserDisplayName.length()
                            , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                viewHolder.tvNotifyUser.setText(spannablecontent);
            }

        } else {
            viewHolder.tvNotifyUser.setTypeface(null, Typeface.BOLD);
            viewHolder.tvNotifyUser.setText(StringUtil.decodeString(message));
        }

        viewHolder.cardView.setOnClickListener(v -> onClickViewPost(model, postiotn));
        viewHolder.ivNotifyAvatar.setOnClickListener(view -> {
            if (model.getActionUser() != null && !model.getActionUser().getUserName().equalsIgnoreCase("admin")) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.push_in_to_right, R.anim.push_in_to_left);
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(Constants.USER_PROFILE_DISPLAYNAME, model.getActionUser().getDisplayName());
                intent.putExtra(Constants.USER_PROFILE_ID, Integer.toString(model.getActionUser().getUserId()));
                context.startActivity(intent, options.toBundle());
            }
        });

    }

    private CustomTypefaceSpan getCustomTypefaceSpanForName() {

        return new CustomTypefaceSpan("sans-serif", Typeface.create(americanFont, Typeface.BOLD));
    }

    private ForegroundColorSpan getForegroundColorSpanForName() {
        return new ForegroundColorSpan(Color.parseColor("#6b6c6e"));
    }

    private void onClickViewPost(NotificationItemModel entity, int position) {
        if (!CheckNetwork.isNetworkAvailable(context)) {

            ((BaseActivity) context).toastTextWhenNoInternetConnection("");

            return;
        }

        mModels.get(position).setIsRead(true);
        notifyItemChanged(position);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent;
        Timber.e("Type = " + entity.getNotificationType());
        switch (entity.getNotificationType()) {
            case Constants.NOTIFYCATION_TYPE_COMMENT:
            case Constants.NOTIFYCATION_TYPE_LIKE:
            case Constants.NOTIFYCATION_USER_TAGGED_IN_POST:
            case Constants.NOTIFYCATION_USER_TAGGED_IN_POST_COMMENT:
                intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID, String.valueOf(entity.getPostId()));
                ((Activity) context).startActivityForResult(intent, Constants.REQUEST_VIEW_NOTIFY, options.toBundle());
                break;

            case Constants.NOTIFYCATION_TYPE_FOLLOW:
                if (entity.getActionUser() != null) {
                    ((BaseToolBarActivity) context).startActivityProfile(String.valueOf(entity.getActionUser().getUserId()),
                            entity.getActionUser().getDisplayName());
                }
                break;

            case Constants.NOTIFYCATION_TYPE_RECEIVE_GIFT:
                if (typeFragment == Constants.NOTIFYCATION_TYPE_YOU && AppsterApplication.mAppPreferences.isUserLogin()) {
                    intent = new Intent(context, TopFanActivity.class);
                    intent.putExtra(ConstantBundleKey.BUNDLE_PROFILE, AppsterApplication.mAppPreferences.getUserModel());
                    context.startActivity(intent, options.toBundle());

                }
                if (typeFragment == Constants.NOTIFYCATION_TYPE_FOLLOWING && entity.getReceiver() != null) {
                    UserModel mUserProfileDetails = new UserModel();
                    mUserProfileDetails.setUserId(entity.getReceiver().getUserId());
                    intent = new Intent(context, TopFanActivity.class);
                    intent.putExtra(ConstantBundleKey.BUNDLE_PROFILE, mUserProfileDetails);
                    context.startActivity(intent, options.toBundle());
                }
                break;

            case Constants.NOTIFYCATION_TYPE_COMISSION:
                if (typeFragment == Constants.NOTIFYCATION_TYPE_YOU && entity.getActionUser() != null) {
                    intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra(Constants.USER_PROFILE_DISPLAYNAME, entity.getActionUser().getDisplayName());
                    intent.putExtra(Constants.USER_PROFILE_ID, Integer.toString(entity.getActionUser().getUserId()));
                    context.startActivity(intent, options.toBundle());
                }
                break;

            case Constants.NOTIFYCATION_TYPE_LIKE_STREAM:
                if (entity.getShortStreamInfoViewModel() != null) {
                    String streamUserImage = "";
                    if (entity.getReceiver() != null) {
                        streamUserImage = entity.getReceiver().getUserImage();
                    } else if (typeFragment == Constants.NOTIFYCATION_TYPE_YOU) {
                        streamUserImage = AppsterApplication.mAppPreferences.getUserModel().getUserImage();
                    }

                    ((BaseToolBarActivity) context).openViewLiveStream(entity.getShortStreamInfoViewModel().getStreamUrl(), entity.getShortStreamInfoViewModel().getSlug(), streamUserImage, entity.getShortStreamInfoViewModel().isRecorded());
                } else {
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.message_stream_remove), Toast.LENGTH_SHORT).show();
                }
                break;

            case Constants.NOTIFYCATION_USER_TAGGED_IN_STREAM_COMMENT:
            case Constants.NOTIFYCATION_TYPE_COMMENT_STREAM:
                if (entity.getShortStreamInfoViewModel() != null) {
                    intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_SLUG_STREAM, entity.getShortStreamInfoViewModel().getSlug());
                    context.startActivity(intent, options.toBundle());
                }
                break;
        }
    }


    static class NotifyHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivNotifyAvatar)
        CircleImageView ivNotifyAvatar;
        @Bind(R.id.tvNotifyUser)
        TextView tvNotifyUser;
        @Bind(R.id.tvNotifyTime)
        TextView tvNotifyTime;
        @Bind(R.id.ivNotifyMessage)
        ImageView ivNotifyMessage;
        @Bind(R.id.card_view)
        RelativeLayout cardView;

        public NotifyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
