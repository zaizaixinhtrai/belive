package com.appster.adapters.viewholder.chatGroupViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.message.ChatItemModelClass;

/**
 * Created by linh on 28/11/2017.
 */

public class LiveCommerceSuggestionHolder extends RecyclerView.ViewHolder {

    public LiveCommerceSuggestionHolder(View itemView) {
        super(itemView);
    }

    public static LiveCommerceSuggestionHolder create(ViewGroup parent){
        return new LiveCommerceSuggestionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_commerce_suggestion, parent, false));
    }

    public void bindTo(ChatItemModelClass item, ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener){
        itemView.setOnClickListener(view -> chatGroupClickListener.onLiveCommerceSuggestionItemClicked());
    }
}
