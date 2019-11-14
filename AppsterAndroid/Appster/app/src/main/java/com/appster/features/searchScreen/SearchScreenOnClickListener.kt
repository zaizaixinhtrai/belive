package com.appster.features.searchScreen

import com.domain.models.ExploreStreamModel

/**
 * Created by Ngoc on 5/23/2018.
 */
interface SearchScreenOnClickListener{
     fun onItemUserImageClicked(model: ExploreStreamModel, position: Int)

     fun onItemUserNameClicked(model: ExploreStreamModel, position: Int)
}