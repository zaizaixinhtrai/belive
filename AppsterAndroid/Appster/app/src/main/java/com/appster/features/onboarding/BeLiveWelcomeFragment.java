package com.appster.features.onboarding;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.appster.R;
import com.stephentuso.welcome.WelcomeFinisher;
import com.stephentuso.welcome.WelcomePage.OnChangeListener;
import com.stephentuso.welcome.WelcomeUtils;

/**
 * Created by thanhbc on 8/11/17.
 */

public class BeLiveWelcomeFragment extends Fragment implements OnChangeListener {
    public static final String KEY_DRAWABLE_ID = "drawable_id";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TITLE = "title";
    public static final String KEY_SHOW_ANIM = "show_anim";
    public static final String KEY_HEADER_TYPEFACE_PATH = "header_typeface";
    public static final String KEY_DESCRIPTION_TYPEFACE_PATH = "description_typeface";
    public static final String KEY_HEADER_COLOR = "header_color";
    public static final String KEY_DESCRIPTION_COLOR = "description_color";
    public static final String KEY_IS_LAST_PAGE = "last_page";

    private ImageView imageView = null;
    private TextView titleView = null;
    private TextView descriptionView = null;
    //    private Button doneView = null;
    private boolean showParallaxAnim = true;
    private TextView skip;

    public static BeLiveWelcomeFragment newInstance(@DrawableRes int drawableId,
                                                    String title,
                                                    String description,
                                                    boolean showParallaxAnim,
                                                    String headerTypefacePath,
                                                    String descriptionTypefacePath,
                                                    @ColorInt int headerColor,
                                                    @ColorInt int descriptionColor,
                                                    boolean isLastPage) {
        Bundle args = new Bundle();
        args.putInt(KEY_DRAWABLE_ID, drawableId);
        args.putString(KEY_TITLE, title);
        args.putString(KEY_DESCRIPTION, description);
        args.putBoolean(KEY_SHOW_ANIM, showParallaxAnim);
        args.putString(KEY_HEADER_TYPEFACE_PATH, headerTypefacePath);
        args.putString(KEY_DESCRIPTION_TYPEFACE_PATH, descriptionTypefacePath);
        args.putInt(KEY_HEADER_COLOR, headerColor);
        args.putInt(KEY_DESCRIPTION_COLOR, descriptionColor);
        args.putBoolean(KEY_IS_LAST_PAGE, isLastPage);
        BeLiveWelcomeFragment fragment = new BeLiveWelcomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.belive_wel_fragment, container, false);

        Bundle args = getArguments();

        imageView = (ImageView) view.findViewById(R.id.wel_image);
        titleView = (TextView) view.findViewById(R.id.wel_title);
        descriptionView = (TextView) view.findViewById(R.id.wel_description);
        skip = (TextView) view.findViewById(R.id.tv_skip);
//        doneView = (Button) view.findViewById(R.id.wel_button_done);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            doneView.setLetterSpacing(0.27f);
            titleView.setLetterSpacing(0.27f);
        }
        if (args == null)
            return view;

        showParallaxAnim = args.getBoolean(KEY_SHOW_ANIM, showParallaxAnim);

        imageView.setImageResource(args.getInt(KEY_DRAWABLE_ID));

        if (args.getString(KEY_TITLE) != null)
            titleView.setText(args.getString(KEY_TITLE));

        if (args.getString(KEY_DESCRIPTION) != null)
            descriptionView.setText(args.getString(KEY_DESCRIPTION));

//        if (args.getBoolean(KEY_IS_LAST_PAGE)) {
//            doneView.setVisibility(View.VISIBLE);
//        }

        int headerColor = args.getInt(KEY_HEADER_COLOR, WelcomeUtils.NO_COLOR_SET);
        if (headerColor != WelcomeUtils.NO_COLOR_SET)
            titleView.setTextColor(headerColor);

        int descriptionColor = args.getInt(KEY_DESCRIPTION_COLOR, WelcomeUtils.NO_COLOR_SET);
        if (descriptionColor != WelcomeUtils.NO_COLOR_SET)
            descriptionView.setTextColor(descriptionColor);

        if (args.getString(KEY_TITLE) != null) {
            if (getString(R.string.onboarding_title3).equals(args.getString(KEY_TITLE))) {
                skip.setVisibility(View.GONE);
            }
        }

        WelcomeUtils.setTypeface(skip, "fonts/opensanslight.ttf", getActivity());
        WelcomeUtils.setTypeface(titleView, args.getString(KEY_HEADER_TYPEFACE_PATH), getActivity());
        WelcomeUtils.setTypeface(descriptionView, args.getString(KEY_DESCRIPTION_TYPEFACE_PATH), getActivity());
//        WelcomeUtils.setTypeface(doneView, "fonts/helveticaneuebold.ttf", getActivity());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.wel_button_done).setOnClickListener(v -> new WelcomeFinisher(BeLiveWelcomeFragment.this).finish());
        view.findViewById(R.id.tv_skip).setOnClickListener(v -> new WelcomeFinisher(BeLiveWelcomeFragment.this).finish());
    }

    @Override
    public void onWelcomeScreenPageScrolled(int pageIndex, float offset, int offsetPixels) {
        if (showParallaxAnim && imageView != null) {
            imageView.setTranslationX(-offsetPixels * 0.8f);
        }
    }

    @Override
    public void onWelcomeScreenPageSelected(int pageIndex, int selectedPageIndex) {
        //Not used
    }

    @Override
    public void onWelcomeScreenPageScrollStateChanged(int pageIndex, int state) {
        //Not used
    }
}
