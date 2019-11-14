package com.appster.features.onboarding;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import com.stephentuso.welcome.BasicPage;

/**
 * Created by thanhbc on 8/11/17.
 */

public class BeLivePage extends BasicPage {
    /**
     * A page with a large image, header, and description
     *
     * @param drawableResId Resource id of drawable to show
     * @param title         Title, shown in large font
     * @param description   Description, shown beneath title
     */
    public BeLivePage(@DrawableRes int drawableResId, String title, String description) {
        super(drawableResId, title, description);
    }


    @Override
    public Fragment fragment() {
        return BeLiveWelcomeFragment.newInstance(drawableResId,
                title,
                description,
                showParallax,
                headerTypefacePath,
                descriptionTypefacePath,
                headerColor,
                descriptionColor,isLastPage);
    }
}
