package com.appster.features.stream;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.appster.R;
import com.appster.customview.CircularColorItem;
import com.appster.dialog.ImmersiveDialogFragment;
import com.apster.common.Utils;
import com.apster.common.key_broad_detection.KeyboardHeightObserver;
import com.apster.common.key_broad_detection.KeyboardHeightProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.apster.common.Utils.getNavBarHeight;

/**
 * Created by linh on 13/05/2017.
 */

public class ComposeWaterMarkText extends ImmersiveDialogFragment implements KeyboardHeightObserver {

    public final static String CURRENT_LIVE_TITLE = "CURRENT_LIVE_TITLE";
    public final static String CURRENT_LIVE_TITLE_COLOR = "CURRENT_LIVE_TITLE_COLOR";

    @Bind(R.id.edt)
    EditText mEdt;
    @Bind(R.id.btn_ok)
    Button mBtnOk;
    @Bind(R.id.ll_color_palette)
    LinearLayout mLlColorPalette;

    OnComposeWaterMarkTextListener mListener;
    View.OnClickListener mOnPaletteItemClickListener;
    private KeyboardHeightProvider keyboardHeightProvider;
    int mTextColor;
    int mSoftNavigationBarHeight = 0;

    public void setListener(OnComposeWaterMarkTextListener listener) {
        mListener = listener;
    }

    public static ComposeWaterMarkText newInstance(String currentLiveTitle, int textColor) {
        Bundle args = new Bundle();
        args.putString(CURRENT_LIVE_TITLE, currentLiveTitle);
        args.putInt(CURRENT_LIVE_TITLE_COLOR, textColor);
        ComposeWaterMarkText fragment = new ComposeWaterMarkText();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getRootLayoutResource() {
        return R.layout.dialog_fragment_compose_water_mask_text;
    }

    @Override
    protected boolean isDimDialog() {
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        keyboardHeightProvider = new KeyboardHeightProvider(getActivity());

        if (Utils.hasNavBar(getResources())){
            mSoftNavigationBarHeight = getNavBarHeight(getResources());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        init();
        setupEditor();
        setupOnPaletteListener();
        addColorItem(rootView.getContext());
        rootView.post(() -> keyboardHeightProvider.start());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }

    @Override
    protected int getWindowAnimation() {
        return R.style.DialogFadeAnimation;
    }

    @OnClick(R.id.btn_ok)
    public void onOkButtonClicked(){
        String inputText = mEdt.getText().toString().trim();
        if (mListener != null){
            mListener.onOk(inputText, mTextColor);
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        if(mEdt!=null) mEdt.setCursorVisible(false);
        super.dismiss();
    }

    private void init(){
        mTextColor = getResources().getColor(R.color.stream_title_color_default);
    }

    private void setupEditor(){
        if (getArguments() != null){
            String title = getArguments().getString(CURRENT_LIVE_TITLE);
            mTextColor = getArguments().getInt(CURRENT_LIVE_TITLE_COLOR);
            mEdt.setText(title);
        }
        mEdt.setTextColor(mTextColor);
        mEdt.requestFocus();
        mEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                onOkButtonClicked();
            }
            return false;
        });
    }

    private void setupOnPaletteListener(){
        mOnPaletteItemClickListener = v -> {
            mTextColor = (int) v.getTag();
            mEdt.setTextColor(mTextColor);
            Timber.d("hex color %s", String.format("#%06X", (0xFFFFFF & mTextColor)));//0xff000000 | Integer.parseInt(hexString, 16)
        };
    }

    private void addColorItem(Context context){
        int[] colorList = getResources().getIntArray(R.array.stream_title_color_list);
        int margin = Utils.dpToPx(3);
        for (int color : colorList) {
            CircularColorItem colorItem = new CircularColorItem(context, color, margin, margin);
            colorItem.setTag(color);
            colorItem.setOnClickListener(mOnPaletteItemClickListener);
            mLlColorPalette.addView(colorItem);
        }
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        Timber.d("onKeyboardHeightChanged %d", height);
        if (height > 400) {
            int translate = height + mSoftNavigationBarHeight - ((FrameLayout.LayoutParams)mLlColorPalette.getLayoutParams()).bottomMargin;
            ((ViewGroup)mLlColorPalette.getParent()).animate().setDuration(100).translationY(-translate).start();
//            mLlColorPalette.animate().setDuration(100).translationY(-height).start();
            mEdt.animate().setDuration(100).translationY(-(translate * 0.3f)).start();
//            mBtnOk.animate().setDuration(100).translationY(height);
        }else{
            ((ViewGroup)mLlColorPalette.getParent()).animate().setDuration(100).translationY(0).start();
//            mLlColorPalette.animate().setDuration(100).translationY(0).start();
            mEdt.animate().setDuration(100).translationY(0).start();
        }
        if (getDialog()!= null) {
            setWindowMode(getDialog().getWindow());
        }
    }

    public interface OnComposeWaterMarkTextListener {
        void onOk(String str, int color);
        void onCancel();
    }
}