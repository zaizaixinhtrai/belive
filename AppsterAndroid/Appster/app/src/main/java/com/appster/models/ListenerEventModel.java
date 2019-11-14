package com.appster.models;

import java.util.ArrayList;

/**
 * Created by User on 4/20/2016.
 */
public class ListenerEventModel {

    public enum TypeEvent {
        FOLLOW_USER,
        DELETE_POST,
        EDIT_PROFILE,
        NEW_POST,
        NEW_COMMENT,
        NEW_LIKE,
        NEW_EVENT_FROM_USER_POST_DETAIL,
        EVENT_VIEW_VIDEOS,
        NEW_EVENT_FROM_LIVE_STREAM,
        EVENT_REPORT,
    }

    public enum TypeFragment {
        TRENDING_LIVE,
        TRENDING_POST,
        PROFILE_ME
    }

    private TypeEvent typeEvent;
    private TypeFragment typeFragment;
    private FollowStatusChangedEvent followStatusChangedEvent;
    private DeletePostEventModel deletePostEventModel;
    private EditProfileEventModel editProfileEventModel;
    private NewPostEventModel newPostEventModel;
    private NewCommentEventModel newCommentEventModel;
    private NewLikeEventModel newLikeEventModel;
    private ArrayList<NewCommentEventModel> arrNewCommentEvnt;
    private ArrayList<NewLikeEventModel> arrNewLikeEvent;
    private ArrayList<ReportEvent> arrReportEvent;

    private ViewVideosEvent viewVideosEvent;

    private ReportEvent reportEvent;

    public ArrayList<NewLikeEventModel> getArrNewLikeEvnt() {
        return arrNewLikeEvent;
    }

    public void setArrNewLikeEvnt(ArrayList<NewLikeEventModel> arrNewLikeEvnt) {
        this.arrNewLikeEvent = arrNewLikeEvnt;
    }

    public ArrayList<NewCommentEventModel> getArrNewCommentEvnt() {
        return arrNewCommentEvnt;
    }

    public void setArrNewCommentEvnt(ArrayList<NewCommentEventModel> arrNewCommentEvnt) {
        this.arrNewCommentEvnt = arrNewCommentEvnt;
    }

    public NewLikeEventModel getNewLikeEventModel() {
        return newLikeEventModel;
    }

    public void setNewLikeEventModel(NewLikeEventModel newLikeEventModel) {
        this.newLikeEventModel = newLikeEventModel;
    }

    public NewCommentEventModel getNewCommentEventModel() {
        return newCommentEventModel;
    }

    public void setNewCommentEventModel(NewCommentEventModel newCommentEventModel) {
        this.newCommentEventModel = newCommentEventModel;
    }

    public ArrayList<ReportEvent> getArrReportEvent() {
        return arrReportEvent;
    }

    public void setArrReportEvent(ArrayList<ReportEvent> arrReportEvent) {
        this.arrReportEvent = arrReportEvent;
    }

    public NewPostEventModel getNewPostEventModel() {
        return newPostEventModel;
    }

    public void setNewPostEventModel(NewPostEventModel newPostEventModel) {
        this.newPostEventModel = newPostEventModel;
    }

    public DeletePostEventModel getDeletePostEventModel() {
        return deletePostEventModel;
    }

    public void setDeletePostEventModel(DeletePostEventModel deletePostEventModel) {
        this.deletePostEventModel = deletePostEventModel;
    }

    public EditProfileEventModel getEditProfileEventModel() {
        return editProfileEventModel;
    }

    public void setEditProfileEventModel(EditProfileEventModel editProfileEventModel) {
        this.editProfileEventModel = editProfileEventModel;
    }

    public NewPostEventModel getNewPostEverntModel() {
        return newPostEventModel;
    }

    public void setNewPostEverntModel(NewPostEventModel newPostEverntModel) {
        this.newPostEventModel = newPostEverntModel;
    }

    public FollowStatusChangedEvent getFollowStatusChangedEvent() {
        return followStatusChangedEvent;
    }

    public void setFollowStatusChangedEvent(FollowStatusChangedEvent followStatusChangedEvent) {
        this.followStatusChangedEvent = followStatusChangedEvent;
    }

    public ViewVideosEvent getViewVideosEvent() {
        return viewVideosEvent;
    }

    public void setViewVideosEvent(ViewVideosEvent viewVideosEvent) {
        this.viewVideosEvent = viewVideosEvent;
    }

    public ReportEvent getReportEvent() {
        return reportEvent;
    }

    public void setReportEvent(ReportEvent reportEvent) {
        this.reportEvent = reportEvent;
    }

    public TypeFragment getTypeFragment() {
        return typeFragment;
    }

    public void setTypeFragment(TypeFragment typeFragment) {
        this.typeFragment = typeFragment;
    }

    public TypeEvent getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(TypeEvent typeEvent) {
        this.typeEvent = typeEvent;
    }
}
