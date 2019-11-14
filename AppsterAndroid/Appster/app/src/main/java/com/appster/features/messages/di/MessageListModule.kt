package com.appster.features.messages.di

import android.content.Context
import com.appster.features.messages.MessageListContract
import com.appster.features.messages.MessageListPresenter
import com.appster.manager.AppsterChatManger
import dagger.Module
import dagger.Provides

/**
 *  Created by DatTN on 10/5/2018<br>
 */
@Module
class MessageListModule {

    @Provides
    fun provideAppChatManager(context: Context): AppsterChatManger = AppsterChatManger.getInstance(context)

    @Provides
    fun providePresenter(presenter: MessageListPresenter): MessageListContract.UserActions = presenter

}