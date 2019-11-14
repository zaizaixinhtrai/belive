package com.appster.features.home.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.appster.R;
import com.appster.features.home.StreamCategory;
import com.appster.layout.SquareImageView;
import com.appster.models.TagListLiveStreamModel;
import com.appster.utility.ImageLoaderUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 6/2/17.
 */

public class CategoriesViewHolder extends ViewHolder {

    public interface OnClickListener {
        void onCategoryItemClicked(TagListLiveStreamModel categoryItem);
    }

    @Bind(R.id.llCategories)
    LinearLayout llCategories;


    public CategoriesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public static CategoriesViewHolder create(@NonNull ViewGroup parent) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.categories_container, parent, false);
        return new CategoriesViewHolder(itemView);
    }

    public void bindTo(@Nullable StreamCategory item, @Nullable OnClickListener listener) {
        if (item == null || item.categories.isEmpty()) return;
        llCategories.removeAllViews();
        for (final TagListLiveStreamModel category : item.categories) {
            View categoryView = LayoutInflater.from(itemView.getContext()).inflate(
                    R.layout.category_item, llCategories, false);
            llCategories.addView(categoryView);
            SquareImageView categoryImage = (SquareImageView) categoryView.findViewById(R.id.ivCategoryImage);
            TextView categoryTitle = (TextView) categoryView.findViewById(R.id.tvCategoryTitle);
            ImageLoaderUtil.displayUserImage(itemView.getContext(), category.getTagImage(), categoryImage);
            categoryTitle.setText(category.getTagName());

            categoryView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onCategoryItemClicked(category);
                }
            });
        }

    }
}
