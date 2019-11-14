package com.appster.manager;

import com.appster.AppsterApplication;
import com.appster.models.DeletePostEventModel;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.ListenerEventModel;
import com.appster.models.NewLikeEventModel;
import com.appster.models.ReportEvent;
import com.appster.models.UserPostModel;
import com.appster.models.ViewVideosEvent;
import com.apster.common.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * this class will hold the list wall feed
 * Created by sonnguyen on 11/29/16.
 */

public class WallFeedManager {
    private static WallFeedManager instance;
    List<UserPostModel> arrayWallFeed;
    Timer timerTask;
    public static WallFeedManager getInstance() {
        if(instance==null)
        {
            instance= new WallFeedManager();
        }
        return instance;
    }

    public List<UserPostModel> getArrayWallFeed() {
        if(arrayWallFeed==null){
            arrayWallFeed= new ArrayList<>();
        }
        return arrayWallFeed;
    }

    public void setArrayWallFeed(List<UserPostModel> arrayWallFeed) {
        this.arrayWallFeed = arrayWallFeed;
    }

    public void addMoreArrayWallFeed(ArrayList<UserPostModel> arrayWallFeed) {
        this.arrayWallFeed.addAll(arrayWallFeed);
    }

    public void clear(){
        if (arrayWallFeed != null){
            arrayWallFeed.clear();
        }
        arrayWallFeed = null;
    }

    public void updateFollowStatus(FollowStatusChangedEvent event) {
        if (event == null || arrayWallFeed==null || arrayWallFeed.size()==0) return;
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if(postModel.isStreamItem()) {
                    if (arrayWallFeed.get(i).getStream().getPublisher().getUserId().equals(event.getUserId())) {
                        arrayWallFeed.get(i).getStream().getPublisher().setIsFollow(event.getFollowType());
                    }
            } else if(postModel.getPost()!=null){
                    if (arrayWallFeed.get(i).getPost().getUserId().equals(event.getUserId())) {
                        arrayWallFeed.get(i).getPost().setIsFollow(event.getFollowType());
                    }
                 }


            }

    }
    public void updateLike(NewLikeEventModel likeEventModel) {
        if (likeEventModel == null || arrayWallFeed==null || arrayWallFeed.size()==0) return;
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if(likeEventModel.isStream()){
                if(postModel.isStreamItem()){
                    if(arrayWallFeed.get(i).getStream().getSlug().equalsIgnoreCase(likeEventModel.getSlug())){
                        arrayWallFeed.get(i).getStream().setLike(likeEventModel.getIsLike());
                        arrayWallFeed.get(i).getStream().setLikeCount(likeEventModel.getLikeCount());
                        break;
                    }
                }
            }else {
                if(!postModel.isStreamItem()){
                    if (arrayWallFeed.get(i).getPost().getPostId().equals(likeEventModel.getPostId())) {
                        arrayWallFeed.get(i).getPost().setIsLike(likeEventModel.getIsLike());
                        arrayWallFeed.get(i).getPost().setLikeCount(likeEventModel.getLikeCount());

                        break;
                    }
                }
            }


        }
    }
    public void addCommentToPost(ListenerEventModel listenerEventModel) {
        if (listenerEventModel == null || arrayWallFeed==null || arrayWallFeed.size()==0) return;
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if(listenerEventModel.getNewCommentEventModel().isStream()){
//                if (arrayWallFeed.get(i).getStream().getSlug().equals(listenerEventModel.getNewCommentEventModel().getSlug())) {
//                    arrayWallFeed.get(i).getStream().addAll(listenerEventModel.getNewCommentEventModel().getArrComment());
//                    break;
//                }
            }else {
                if(!postModel.isStreamItem()) {
                    if (arrayWallFeed.get(i).getPost().getPostId().equals(listenerEventModel.getNewCommentEventModel().getPostId())) {
                        arrayWallFeed.get(i).getPost().getCommentList().addAll(listenerEventModel.getNewCommentEventModel().getArrComment());
                        break;
                    }
                }
            }

        }
    }
    public void changeUserProfileImage() {
        if (arrayWallFeed==null || arrayWallFeed.size()==0) return;
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if(postModel.isStreamItem()){
                if(arrayWallFeed.get(i).getStream().getPublisher().getUserId().equalsIgnoreCase(AppsterApplication.mAppPreferences.getUserModel().getUserId())){
                    arrayWallFeed.get(i).getStream().getPublisher().setUserImage(AppsterApplication.mAppPreferences.getUserModel().getUserImage());
                    arrayWallFeed.get(i).getStream().getPublisher().setDisplayName(AppsterApplication.mAppPreferences.getUserModel().getDisplayName());
                }
            }else{
                if (arrayWallFeed.get(i).getPost().getNfs_userid().equals(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {
                    arrayWallFeed.get(i).getPost().setUserImage(AppsterApplication.mAppPreferences.getUserModel().getUserImage());
                    arrayWallFeed.get(i).getPost().setDisplayName(AppsterApplication.mAppPreferences.getUserModel().getDisplayName());
                }
            }

        }
    }
    public void removePost(DeletePostEventModel deletePostEventModel) {
        if (deletePostEventModel == null || arrayWallFeed==null || arrayWallFeed.size()==0) return;
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if(deletePostEventModel.isStream()){
                if(postModel.isStreamItem()){
                    if (arrayWallFeed.get(i).getStream().getSlug().equals(deletePostEventModel.getSlug())) {
                        arrayWallFeed.remove(i);
                        break;
                    }
                }
            }else{
                if(!postModel.isStreamItem()) {
                    if (arrayWallFeed.get(i).getPost().getPostId().equals(deletePostEventModel.getPostId())) {
                        arrayWallFeed.remove(i);
                        break;
                    }
                }
            }

        }

    }

    public void viewVideosCount(ViewVideosEvent viewVideosEvent) {
        if (viewVideosEvent == null || arrayWallFeed==null || arrayWallFeed.size()==0) return;
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if(viewVideosEvent.isStream()) {
                if (postModel.isStreamItem()) {
                    if (arrayWallFeed.get(i).getStream().getSlug().equals(viewVideosEvent.getSlug())) {
                        arrayWallFeed.get(i).getStream().setViewCount(viewVideosEvent.getViewCount());
                        break;
                    }
                }
            }else {
            if(!postModel.isStreamItem()) {
                if (arrayWallFeed.get(i).getPost().getPostId().equals(viewVideosEvent.getPostId())) {
                    arrayWallFeed.get(i).getPost().setViewCount(viewVideosEvent.getViewCount());
                    break;
                }
            }
        }

        }
    }
    public void reportEvent(ReportEvent reportEvent) {
        if (reportEvent == null || arrayWallFeed==null || arrayWallFeed.size()==0) return;
        for (int i = 0; i < arrayWallFeed.size(); i++) {
            UserPostModel postModel = arrayWallFeed.get(i);
            if(reportEvent.isStream()){
                if(postModel.isStreamItem()) {
                    if (arrayWallFeed.get(i).getStream().getSlug().equals(reportEvent.getSlug())) {
                        arrayWallFeed.get(i).getStream().setIsReport(reportEvent.getIsReport());
                        break;
                    }
                }
            }else{
                if(!postModel.isStreamItem()){
                    if (arrayWallFeed.get(i).getPost().getPostId().equals(reportEvent.getPostId())) {
                        arrayWallFeed.get(i).getPost().setIsReport(reportEvent.getIsReport());
                        break;
                    }
                }

            }

        }

    }

    public Timer getTimerTask() {
        if(timerTask==null){
            timerTask = new Timer();
        }
        return timerTask;
    }

    public void setTimerTask(Timer timerTask) {
        this.timerTask = timerTask;
    }
    public void stopTimerTask() {
        LogUtils.logV("NCS", "stopTimerTask");
       if(timerTask!=null){
           LogUtils.logV("NCS", "timerTask!=null");
           timerTask.cancel();
           timerTask.purge();
           timerTask=null;

       }
    }
}
