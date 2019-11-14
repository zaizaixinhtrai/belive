package com.appster.features.messages.chat.di

import android.content.Context
import com.appster.features.messages.MessageListContract
import com.appster.features.messages.MessageListPresenter
import com.appster.features.messages.chat.ChatContract
import com.appster.features.messages.chat.ChatPresenter
import com.appster.manager.AppsterChatManger
import dagger.Module
import dagger.Provides

/**
 *  Created by DatTN on 10/9/2018
 */
@Module
class ChatModule {

    @Provides
    fun provideAppChatManager(context: Context): AppsterChatManger = AppsterChatManger.getInstance(context)

    @Provides
    fun providePresenter(presenter: ChatPresenter): ChatContract.UserActions = presenter

}