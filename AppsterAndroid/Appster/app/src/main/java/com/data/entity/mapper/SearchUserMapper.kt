package com.data.entity.mapper

import com.appster.webservice.response.BaseDataPagingResponseModel
import com.data.entity.SearchUserEntity
import com.domain.models.SearchUserModel

/**
 * Created by Ngoc on 5/28/2018.
 */
class SearchUserMapper {
    fun transform(searchUserRequestEntity: BaseDataPagingResponseModel<SearchUserEntity>?): BaseDataPagingResponseModel<SearchUserModel>? {
        return searchUserRequestEntity?.let {
            return BaseDataPagingResponseModel<SearchUserModel>().apply {
                isEnd = it.isEnd
                nextId = it.nextId
                setResult(it.result?.map {
                    SearchUserModel().apply {
                        userId = it.userId
                        username = it.username
                        displayName = it.displayName
                        userProfilePic = it.userProfilePic
                        gender = it.gender
                        isFollow = it.isFollow
                        typeModel = it.typeModel
                    }
                })
            }
        }
    }
}