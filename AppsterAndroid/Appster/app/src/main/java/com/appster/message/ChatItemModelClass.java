package com.appster.message;

import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import com.appster.R;
import com.appster.core.adapter.DisplayableItem;
import com.appster.customview.ExpensiveGift;
import com.appster.models.DailyTopFanModel;
import com.appster.models.UserModel;
import com.appster.webservice.response.SubStreamData;
import com.apster.common.CommonDefine;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class ChatItemModelClass implements DisplayableItem {
    public static final String INIT_BOT_LIST = "init_bot_list";
    public static final String CHAT_TYPE_MESSAGE = "message";
    public static final String CHAT_TYPE_LIKE = "like";
    public static final String CHAT_TYPE_GIFT = "gift";
    public static final String CHAT_TYPE_END = "end";
    public static final String CHAT_TYPE_ADMIN_MESSAGE = "adminMessage";
    public static final String CHAT_TYPE_DURATION = "duration";
    public static final String CHAT_TYPE_BOT_JOIN_LIST = "botUserJoinList";
    public static final String CHAT_TYPE_USER_JOIN_LIST = "userJoinList";
    public static final String CHAT_TYPE_SHARE_STREAM = "shareLiveStream";
    public static final String CHAT_TYPE_BLOCK = "block";
    public static final String CHAT_TYPE_MUTE = "mute";
    public static final String CHAT_TYPE_UNMUTE = "unmute";
    public static final String CHAT_TYPE_LUCKY_WHEEL_SHOW = "lucky_wheel_show";
    public static final String CHAT_TYPE_LUCKY_WHEEL_START = "lucky_wheel_start";
    public static final String CHAT_TYPE_STREAM_PAUSE = "message_pause";
    public static final String CHAT_TYPE_STREAM_RESTART = "message_restart";
    public static final String CHAT_TYPE_FOLLOW = "followHost";
    public static final String TYPE_STEAM_TITLE_STICKER = "textSticker";
    public static final String CHAT_TYPE_KICK = "kick";
    public static final String CHAT_TYPE_UNKICK = "unkick";
    public static final String CHAT_TYPE_STATISTIC = "statistic";
    public static final String CHAT_TYPE_FOLLOW_HOST_SUGGESTION = "CHAT_TYPE_FOLLOW_HOST_SUGGESTION";
    public static final String CHAT_TYPE_LIVE_COMMERCE_SUGGESTION = "commerceSuggestion";
    public static final String CHAT_TYPE_LIVE_COMMERCE_ANNOUNCEMENT = "commerceAnnouncement";

    @StringDef({CHAT_TYPE_MESSAGE,
            CHAT_TYPE_LIKE,
            CHAT_TYPE_GIFT,
            CHAT_TYPE_END,
            CHAT_TYPE_ADMIN_MESSAGE,
            CHAT_TYPE_BOT_JOIN_LIST,
            CHAT_TYPE_USER_JOIN_LIST,
            CHAT_TYPE_SHARE_STREAM,
            CHAT_TYPE_BLOCK,
            CHAT_TYPE_MUTE,
            CHAT_TYPE_UNMUTE,
            CHAT_TYPE_LUCKY_WHEEL_SHOW,
            CHAT_TYPE_LUCKY_WHEEL_START,
            CHAT_TYPE_STREAM_PAUSE,
            CHAT_TYPE_STREAM_RESTART,
            CHAT_TYPE_FOLLOW,
            TYPE_STEAM_TITLE_STICKER,
            CHAT_TYPE_KICK,
            CHAT_TYPE_STATISTIC,
            CHAT_TYPE_FOLLOW_HOST_SUGGESTION,
            CHAT_TYPE_LIVE_COMMERCE_SUGGESTION,
            CHAT_TYPE_LIVE_COMMERCE_ANNOUNCEMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChatStringType {
    }

    public boolean isGroup = false;
    @SerializedName("UserId")
    String userId = "";
    @SerializedName("UserName")
    @Expose
    private String mUserName; //Phu Tang added
    private String Message;
    private String DisplayName = "";
    @ChatStringType
    @SerializedName("MessageType")
    @Expose
    String messageType;
    @SerializedName("ProfilePic")
    @Expose
    private String UserImage = "";
    private String Gender;
    private String MessageId;
    private String Created;
    private String GiftId = "";
    @SerializedName("IsExpensive")
    private boolean isExpensive;
    private String GiftImageLink = "";
    private String ReceiverStars;
    private long DurationTime;
    private long TotalViewers;
    private int TotalLikes;
    private long TotalReceivedStars;
    private String mutedUserId;
    private String blockUserId;
    private int luckyResult = -1;
    private int VotingScores;

    private boolean IsLiked = false;
    private int mGiftCombo;
    @SerializedName("ProfileColor")
    private String ProfileColor;

    @SerializedName("x")
    @Expose
    public float mStreamTitleStickerX;
    @SerializedName("y")
    @Expose
    public float mStreamTitleStickerY;
    @SerializedName("content")
    @Expose
    public String mStreamTitleStickerContent;
    @SerializedName("Slug")
    @Expose
    public String slug;
    @SerializedName("streamTitleColor")
    @Expose
    public String mStreamTitleColorCode = "";

    @SerializedName("GiftName")
    @Expose
    public String mGiftName = "";

    @SerializedName("RankingList")
    public List<String> topFanList = new ArrayList<>();
    @SerializedName("DailyTopFans")
    @Expose
    public List<DailyTopFanModel> dailyTopFansList;

    /**
     * 0: top 1
     * 1: top 2
     * 2: top 3
     */
    @SerializedName("Rank")
    public int rank = -1;
    private List<String> chatBotWatcherUserModels;
    @SerializedName("SubStream")
    public SubStreamData subStreamData;

    @SerializedName("GiftColor")
    @Expose
    public int giftColor;

    public String getProfileColor() {
        return ProfileColor;
    }

    public void setProfileColor(String profileColor) {
        ProfileColor = profileColor;
    }

    public int getGiftCombo() {
        return mGiftCombo;
    }

    public void setGiftCombo(int mGiftCombo) {
        this.mGiftCombo = mGiftCombo;
    }

    public String getReceiverStars() {
        return ReceiverStars;
    }

    public void setReceiverStars(String receiverStars) {
        ReceiverStars = receiverStars;
    }

    public String getGiftImage() {
        return GiftImageLink;
    }

    public void setGiftImage(String giftImage) {
        this.GiftImageLink = giftImage;
    }

    public String getGiftId() {
        return GiftId;
    }

    public void setGiftId(String giftId) {
        GiftId = giftId;
    }

    public boolean getIsExpensive() {
        return isExpensive;
    }

    public void setIsExpensive(boolean isExpensive) {
        this.isExpensive = isExpensive;
    }

    public boolean isExpensiveGift() {
        if (isExpensive) {
            return true;
        }
        isExpensive = ExpensiveGift.checkExpensiveGiftByMessage(getGiftId());
        return isExpensive;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public String getChatDisplayName() {
        return DisplayName;
    }

    public void setChatDisplayName(String chatDisplayName) {
        this.DisplayName = chatDisplayName;
    }

    public String getProfilePic() {
        if (UserImage == null) UserImage = "";
        if (UserImage.isEmpty()) {
            UserImage = UserModel.getUserImageByUserName(mUserName);
        }
        return UserImage;
    }

    public void setProfilePic(String profilePic) {
        UserImage = profilePic;
    }

    public String getUserIdSend() {
        return userId;
    }

    public void setUserIdSend(String id) {
        this.userId = id;
    }

    public String getMsg() {
        return Message;
    }

    public void setMsg(String msg) {
        this.Message = msg;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String username) {
        this.mUserName = username;
    }

    @ChatStringType
    public String getType() {
        return messageType;
    }

    public void setMessageType(@ChatStringType String messageType) {
        this.messageType = messageType;
    }

    public boolean isGroupMessage() {
        return isGroup;
    }

    public void setIsGroupMessage(boolean isGroup) {
        this.isGroup = isGroup;
    }

    @DrawableRes
    public int getTopFanDrawable() {
        switch (rank) {
            case 0:
                return R.drawable.ic_topfan_1;
            case 1:
                return R.drawable.ic_topfan_2;
            case 2:
                return R.drawable.ic_topfan_3;
            default:
                return -1;
        }
    }

    public Date getTime() {
        if (!TextUtils.isEmpty(Message) && Message.contains(CommonDefine.KEY_USER_SEND_TIME)) {
            String[] timeMessage = Message.split(CommonDefine.KEY_USER_SEND_TIME);
            if (timeMessage.length > 1) {
                String time = timeMessage[0];

                if (time.contains(".")) {
                    String[] created = time.split("\\.");
                    return getChatDate(created[0]);

                } else {
                    return getChatDate(time);
                }
            } else {
                return new Date();
            }
        } else {
            return new Date();
        }
    }

    @NonNull
    private Date getChatDate(String time) {
        long dv = 0;
        try {
            dv = Long.valueOf(time) * 1000;// its need to be in milisecond
        } catch (NumberFormatException e) {
            Timber.e(e);
        }
        return new Date(dv);
    }

    public List<String> getChatBotUserModels() {
        return chatBotWatcherUserModels;
    }

    public void setChatBotUserModels(List<String> chatBotUserModels) {
        this.chatBotWatcherUserModels = chatBotUserModels;
    }


    public long getDurationTime() {
        return DurationTime;
    }

    public void setDurationTime(long durationTime) {
        DurationTime = durationTime;
    }

    public long getTotalViewers() {
        return TotalViewers;
    }

    public void setTotalViewers(long totalViewers) {
        TotalViewers = totalViewers;
    }

    public int getTotalLikes() {
        return TotalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        TotalLikes = totalLikes;
    }

    public long getTotalReceivedStars() {
        return TotalReceivedStars;
    }

    public void setTotalReceivedStars(long totalReceivedStars) {
        TotalReceivedStars = totalReceivedStars;
    }

    public boolean isLiked() {
        return IsLiked;
    }

    public void setLiked(boolean liked) {
        IsLiked = liked;
    }

    public String getMutedUserId() {
        return mutedUserId;
    }

    public void setMutedUserId(String mutedUserId) {
        this.mutedUserId = mutedUserId;
    }

    public String getBlockUserId() {
        return blockUserId;
    }

    public void setBlockUserId(String blockUserId) {
        this.blockUserId = blockUserId;
    }

    public int getLuckyResult() {
        return luckyResult;
    }

    public void setLuckyResult(int luckyResult) {
        this.luckyResult = luckyResult;
    }

    public int getVotingScores() {
        return VotingScores;
    }

    public void setVotingScores(int votingScores) {
        VotingScores = votingScores;
    }

    public float getStreamTitleStickerX() {
        return mStreamTitleStickerX;
    }

    public void setStreamTitleStickerX(float streamTitleStickerX) {
        mStreamTitleStickerX = streamTitleStickerX;
    }

    public float getStreamTitleStickerY() {
        return mStreamTitleStickerY;
    }

    public void setStreamTitleStickerY(float streamTitleStickerY) {
        mStreamTitleStickerY = streamTitleStickerY;
    }

    public String getGiftName() {
        return mGiftName;
    }

    public void setGiftName(String giftName) {
        mGiftName = giftName;
    }

    public String getStreamTitleStickerContent() {
        return mStreamTitleStickerContent;
    }

    public void setStreamTitleStickerContent(String streamTitleStickerContent) {
        mStreamTitleStickerContent = streamTitleStickerContent;
    }


    @Override
    public int hashCode() {
        return Objects.hash(userId, messageType, Message);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChatItemModelClass) {
            ChatItemModelClass secondItem = (ChatItemModelClass) o;
            if (userId.equals(secondItem.userId)) {
                if (CHAT_TYPE_BOT_JOIN_LIST.equals(messageType) && CHAT_TYPE_BOT_JOIN_LIST.equals(secondItem.messageType)) {
                    Timber.d("equals %s %s", mUserName, userId.equals(secondItem.userId));
                    return true;
                } else {
                    Timber.d("equals %s false inner", mUserName);
                    Timber.d("type %s - %s", messageType, secondItem.messageType);
                }
            }
        } else {
            Timber.d("equals %s false outer", mUserName);
        }
        return super.equals(o);
    }
}
