package com.appster.comments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.customview.autolinktextview.AutoLinkTextView;
import com.appster.customview.autolinktextview.AutoLinkUtil;
import com.appster.utility.DialogUtil;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.CopyTextUtils;
import com.pack.utility.SetDateTime;
import com.pack.utility.StringUtil;

import java.util.ArrayList;
import java.util.Timer;

import timber.log.Timber;

/**
 * Created by Ngoc on 9/4/2015.
 */
public class AdapterListComments extends BaseAdapter {

    ArrayList<ItemClassComments> listComment;
    private Context activity;
    private OnShowUserProfile onShowUserProfile;
    private SpannableString spannablecontent;
    private String mUserIdOwnerPost;
    private CommentCallback mCommentCallback;

    public AdapterListComments(Context activity, ArrayList<ItemClassComments> listComment, String userIdOwnerPost) {

        this.activity = activity;
        this.listComment = listComment;
        this.mUserIdOwnerPost = userIdOwnerPost;
    }

    public void setOnShowUserProfile(OnShowUserProfile onShowUserProfile) {
        this.onShowUserProfile = onShowUserProfile;
    }

    public void setCommentCallback(CommentCallback mCommentCallback) {
        this.mCommentCallback = mCommentCallback;
    }

    @Override
    public int getCount() {
        if (listComment != null) {
            return listComment.size();
        }
        return 0;
    }

    @Override
    public ItemClassComments getItem(int position) {
        if (listComment != null && listComment.size() > 0) {
            return listComment.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ItemClassComments item = listComment.get(position);

        CommentsAdapterHolderView holder = null;

        if (convertView == null) {
            LayoutInflater mLayoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mLayoutInflater.inflate(
                    R.layout.adapter_comment_row, parent, false);

            holder = new CommentsAdapterHolderView(item);

            holder.imageView_user = (ImageView) convertView
                    .findViewById(R.id.userImage);
            holder.txt_displayname = (AutoLinkTextView) convertView
                    .findViewById(R.id.tvSenderDisplayName);
            holder.txt_timeAgo = (TextView) convertView.findViewById(R.id.txt_timeAgo);
            holder.rootView = convertView.findViewById(R.id.root_view);
            holder.fmPadding = convertView.findViewById(R.id.fm_padding);

            convertView.setTag(holder);
        } else {
            holder = (CommentsAdapterHolderView) convertView.getTag();
        }
        AutoLinkUtil.addAutoLinkMode(holder.txt_displayname);
        holder.txt_displayname.setText("");
        ClickableSpan clickablePP = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                if (preventShowUserProfile(item)) {

                    return;
                }

                onShowUserProfile.onShow(position);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#6b6c6e"));
//                ds.bgColor
            }
        };
        Typeface opensansbold = Typeface.createFromAsset(activity.getAssets(),
                "fonts/opensansbold.ttf");
        String contentComment = StringUtil.decodeString(item.getMessage());
        String displayName = StringUtil.decodeString(item.getDisplay_name());
        ArrayList<int[]> hashtagSpans = StringUtil.getSpans(contentComment, '#');
        SpannableString commentsContentSpan =
                new SpannableString(contentComment);

        StringUtil.setSpanComment(commentsContentSpan, hashtagSpans, activity);
        String message = displayName + " ";
        spannablecontent = new SpannableString(message);
        int start = message.indexOf(item.getDisplay_name());
        int end = message.indexOf(item.getDisplay_name()) + item.getDisplay_name().length();
        spannablecontent.setSpan(new RelativeSizeSpan(1.0f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannablecontent.setSpan(new CustomTypefaceSpan("sans-serif", Typeface.create(opensansbold, Typeface.BOLD)),
                start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannablecontent.setSpan(clickablePP, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        StringBuilder builder = new StringBuilder();
        builder.append(spannablecontent);

        holder.txt_displayname.append(spannablecontent);
        holder.txt_displayname.appendAutoLinkText(contentComment);
        holder.txt_displayname.setAutoLinkOnClickListener(AutoLinkUtil.newListener((BaseActivity) activity));
        holder.txt_displayname.setMovementMethod(LinkMovementMethod.getInstance());

        boolean showDeleteOption = AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(mUserIdOwnerPost);
        holder.txt_displayname.setOnLongClickListener(v -> {
            showStreamOption(v, contentComment, position, showDeleteOption);
            return true;
        });

        // set User Image
        ImageLoaderUtil.displayUserImage(activity, item.getUser_image(),
                holder.imageView_user);

        holder.txt_timeAgo.setText(SetDateTime.partTimeForFeedItem(item
                .getCreated(), activity));

        holder.imageView_user.setOnClickListener(v -> {
            if (preventShowUserProfile(item)) {
                return;
            }
            onShowUserProfile.onShow(position);
        });

        holder.rootView.setOnClickListener(view -> {
            if (mCommentCallback != null) mCommentCallback.onRowClick(position);
        });

        if (position == 0) {
            holder.fmPadding.setVisibility(View.VISIBLE);
        } else {
            holder.fmPadding.setVisibility(View.GONE);
        }

        return convertView;
    }

    private CustomTypefaceSpan getCustomTypefaceSpanForName() {
        return new CustomTypefaceSpan("sans-serif", Typeface.create(Typeface.createFromAsset(activity.getAssets(),
                "fonts/opensansbold.ttf"), Typeface.BOLD));
    }

    private void showStreamOption(View view, String textCopy, int position, boolean showDeleteOption) {
        PopupMenu popupMenuOption = new PopupMenu(activity, view);
        popupMenuOption.getMenu().add(0, 0, 0, activity.getString(R.string.copy_stream_comment));
        if (showDeleteOption) {
            popupMenuOption.getMenu().add(0, 0, 1, activity.getString(R.string.stream_comment_delete_text));
        }
        popupMenuOption.setOnMenuItemClickListener(item -> {
            switch (item.getOrder()) {
                case 0:
                    CopyTextUtils.CopyClipboard(activity, textCopy, "");
                    break;
                case 1:
                    if (showDeleteOption) {
                        DialogUtil.showConfirmDialog((Activity) activity,
                                activity.getString(R.string.delete_comment_title),
                                activity.getString(R.string.delete_comment_body),
                                activity.getString(R.string.delete_comment_delete_button),
                                () -> {
                                    if (mCommentCallback != null)
                                        mCommentCallback.onDeleteComment(position);
                                }
                        );
                    }
                    break;
            }
            return true;
        });

        popupMenuOption.show();
    }

    private boolean preventShowUserProfile(ItemClassComments item) {

        if (AppsterApplication.mAppPreferences.isUserLogin()) {

            if (AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(item.getUser_id())) {
                return true;
            }

        }

        return false;
    }

    private class CommentsAdapterHolderView {

        private ImageView imageView_user;
        private AutoLinkTextView txt_displayname;
        private TextView txt_timeAgo;
        private ItemClassComments item;
        private RelativeLayout rootView;
        private FrameLayout fmPadding;

        CommentsAdapterHolderView(ItemClassComments item) {
            this.item = item;
        }

    }

    public interface OnShowUserProfile {
        void onShow(int position);
    }

    public interface CommentCallback {
        void onDeleteComment(int position);

        void onRowClick(int position);
    }
}