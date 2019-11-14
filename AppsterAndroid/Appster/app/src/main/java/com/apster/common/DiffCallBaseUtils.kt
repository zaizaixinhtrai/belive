package com.apster.common

import com.appster.core.adapter.UpdateableItem

class DiffCallBaseUtils : BaseDiffCallback<UpdateableItem>() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition].isSameItem(mNewList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition].isSameContent(mNewList[newItemPosition])
    }
}