package com.appster.customview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appster.R;
import com.appster.interfaces.GiftComboGroupViewListener;
import com.appster.message.ChatItemModelClass;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.Utils;

import java.util.Locale;

import timber.log.Timber;

import static com.pack.utility.StringUtil.subStringWithPresetMaxLength;

/**
 * Created by linh on 21/11/2016.
 */

public class GiftComboGroupView extends LinearLayout {

    GiftComboGroupViewListener mGiftComboGroupViewListener;

    public GiftComboGroupView(Context context) {
        super(context);
    }

    public GiftComboGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GiftComboGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GiftComboGroupView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void addGift(ChatItemModelClass chatItemModelClass) {
        View v = isGiftExist(chatItemModelClass.getUserName(), chatItemModelClass.getGiftId());
        if (v != null && !((GiftView) v).isDetaching()) {
            GiftView giftView = (GiftView) v;
            giftView.showComboGift();

        } else {
            GiftView giftView = new GiftView(getContext(), this);
            giftView.setAvatarUrl(chatItemModelClass.getProfilePic());
            giftView.setGiftImgUrl(chatItemModelClass.getGiftImage());
            giftView.setDisplayName(chatItemModelClass.getChatDisplayName());
            giftView.attachToParent(chatItemModelClass.getUserName(), chatItemModelClass.getGiftId());
            giftView.setComboCompletedListener(times -> {
                if (mGiftComboGroupViewListener != null) {
                    chatItemModelClass.setGiftCombo(times);
//                   String msg = getResources().getQuantityString(gifts,times,chatItemModelClass.getMsg().replace(getContext().getString(R.string.message_sent_a),"").replace("!",""));
//                   chatItemModelClass.setMsg(msg);
                    mGiftComboGroupViewListener.onDetachView(chatItemModelClass);
                }
            });
        }
    }

    public void addTopFanJoined(ChatItemModelClass chatItem) {
        Timber.e("addTopFanJoined");
        TopFanView topFanView = new TopFanView(getContext(), this);
        topFanView.load(chatItem.getProfilePic(),subStringWithPresetMaxLength(chatItem.getChatDisplayName()), chatItem.getTopFanDrawable());
    }

    private View isGiftExist(String username, String giftId) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof GiftView) {
                GiftComboGroupView.TagWrapper tag = (GiftComboGroupView.TagWrapper) view.getTag();
                if (tag.username.equals(username) && tag.giftId.equals(giftId)) {
                    return view;
                }
            }
        }
        return null;
    }

    public void setGiftComboGroupViewListener(GiftComboGroupViewListener mGiftComboGroupViewListener) {
        this.mGiftComboGroupViewListener = mGiftComboGroupViewListener;
    }


    //region inner classes
    static class GiftView extends LinearLayout {
        private static final int DELAYED_TIME_TO_DETACH = 4000;
        private static final int DURATION_SLIDE_ANIMATION = 500;
        private static final int DURATION_COMBO_GIFT_ANIMATION = 1200;
        private static final String TRANLATION_X = "translationX";
        TextView txtTimes;
        private TextView txtDisplayName;
        ViewGroup parentView;
        private ImageView imgAvatar;
        private ImageView imgGiftImage;

        private Runnable autoDetachRunnable;
        int currentTime;
        private String avatarUrl;
        private String giftImgUrl;
        private String displayName;

        private boolean isDetaching;

        private AnimatorSet comboAnimSet;
        OnComboCompletedListener comboCompletedListener;
        final int imgGiftSize = Utils.dpToPx(45);

        //=================== constructors =============================================================
        public GiftView(Context context, ViewGroup parent) {
            super(context);
            parentView = parent;
            init(context);
        }

        public GiftView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public GiftView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public GiftView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init(context);
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public void setGiftImgUrl(String giftImgUrl) {
            this.giftImgUrl = giftImgUrl;
        }

        public boolean isDetaching() {
            return isDetaching;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = subStringWithPresetMaxLength(displayName);
        }

        //=================== inherited methods ========================================================
        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            txtDisplayName.setText(displayName);
//            imgAvatar.setImageBitmap(avatarBitmap);
//            imgGiftImage.setImageBitmap(giftBitmap);
            slideAnimation();
            postDelayed(autoDetachRunnable, DELAYED_TIME_TO_DETACH);
        }

        //====================== inner methods =========================================================

        public void setComboCompletedListener(OnComboCompletedListener comboCompletedListener) {
            this.comboCompletedListener = comboCompletedListener;
        }

        private void init(Context context) {
            setClipChildren(false);
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View root = inflater.inflate(R.layout.view_gift_compound, this, true);
            txtTimes = (TextView) root.findViewById(R.id.txt_times);
            txtDisplayName = (TextView) root.findViewById(R.id.txt_display_name);
            imgAvatar = (ImageView) root.findViewById(R.id.img_avatar);
            imgGiftImage = (ImageView) findViewById(R.id.img_gift_image);


            autoDetachRunnable = this::detachFromParent;
        }

        public void attachToParent(String username, String giftId) {
            if (parentView == null) {
                Timber.e("the parent view must be not null");
                return;
            }
            GiftComboGroupView.TagWrapper tag = new GiftComboGroupView.TagWrapper();
            tag.username = username;
            tag.giftId = giftId;
            setTag(tag);
            ImageLoaderUtil.displayUserImage(getContext(), avatarUrl, imgAvatar, imgGiftSize, imgGiftSize, null);
            ImageLoaderUtil.displayUserImage(getContext(), giftImgUrl, imgGiftImage, imgGiftSize, imgGiftSize, null);
            //addGift this view into parent
            parentView.addView(GiftView.this, 0);

        }

        private void detachFromParent() {
            isDetaching = true;
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(GiftView.this, TRANLATION_X, 0f, -getWidth());
            objectAnimator.setDuration(DURATION_SLIDE_ANIMATION);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.start();
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    GiftView.this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    parentView.removeView(GiftView.this);
                    GiftView.this.setLayerType(View.LAYER_TYPE_NONE, null);
                    if (comboCompletedListener != null) {
                        comboCompletedListener.onComboAnimationCompleted(currentTime);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    //do not thing
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    //do not thing
                }
            });
        }

        public void slideAnimation() {
            int translate = Utils.dpToPx(150);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(GiftView.this, TRANLATION_X, -translate, 0f);
            objectAnimator.setDuration(DURATION_SLIDE_ANIMATION);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.start();
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    //do nothing
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    showComboGift();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    //do nothing
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    //do nothing
                }
            });
        }

        public void showComboGift() {
            if (isDetaching) {
                return;
            }
            removeCallbacks(autoDetachRunnable);

            if (comboAnimSet == null) {
                initComboAnimSet();
            } else {
                comboAnimSet.cancel();
            }
            comboAnimSet.start();

            postDelayed(autoDetachRunnable, DELAYED_TIME_TO_DETACH);
        }

        private void initComboAnimSet() {
            comboAnimSet = new AnimatorSet();
            int translate = Utils.dpToPx(100);
            ObjectAnimator translateX = ObjectAnimator.ofFloat(txtTimes, TRANLATION_X, translate, 0);
            ObjectAnimator translateY = ObjectAnimator.ofFloat(txtTimes, "translationY", -translate, 0);
            ObjectAnimator zoomOutX = ObjectAnimator.ofFloat(txtTimes, "scaleX", 3, 1);
            ObjectAnimator zoomOutY = ObjectAnimator.ofFloat(txtTimes, "scaleY", 3, 1);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(txtTimes, "alpha", 0.1f, 1f);

            comboAnimSet.setDuration(DURATION_COMBO_GIFT_ANIMATION);
            comboAnimSet.setInterpolator(new BounceInterpolator());
            comboAnimSet.setupStartValues();
            comboAnimSet.playTogether(translateX, translateY, zoomOutX, zoomOutY, alpha);
            comboAnimSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    txtTimes.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    String times = String.format(Locale.US, "x%s", String.valueOf(++currentTime));
                    txtTimes.setText(times);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    txtTimes.setLayerType(View.LAYER_TYPE_NONE, null);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    //do nothing
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //do nothing
                }
            });
        }
    }

    private static class TagWrapper {
        String username;
        String giftId;

        TagWrapper() {
        }
    }

    public interface OnComboCompletedListener {
        void onComboAnimationCompleted(int times);
    }
    //endregion
}
