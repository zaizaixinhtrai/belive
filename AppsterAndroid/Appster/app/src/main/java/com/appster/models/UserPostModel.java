package com.appster.models;

import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.apster.common.Constants;

/**
 * Created by User on 9/20/2016.
 */
public class UserPostModel {

    private int Type;
    private StreamModel Stream;
    private ItemModelClassNewsFeed Post;

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public StreamModel getStream() {
        return Stream;
    }

    public void setStream(StreamModel stream) {
        Stream = stream;
    }

    public ItemModelClassNewsFeed getPost() {
        return Post;
    }

    public void setPost(ItemModelClassNewsFeed post) {
        Post = post;
    }

    public boolean isStreamItem(){
        return getType()== Constants.LIST_USER_POST_LIVE_STREAM;
    }
}
