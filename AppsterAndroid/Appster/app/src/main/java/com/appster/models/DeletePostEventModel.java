package com.appster.models;

/**
 * Created by User on 4/20/2016.
 */
public class DeletePostEventModel {
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
    private String postId;
    private String slug;
    private boolean isStream;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isStream() {
        return isStream;
    }

    public void setStream(boolean stream) {
        isStream = stream;
    }
}
