package com.appster.manager;

/**
 * Created by sonnguyen on 12/6/16.
 */

import com.appster.interfaces.IReleaseMediaPlayerCallback;
import com.ksyun.media.player.KSYMediaPlayer;
import com.pack.utility.StringUtil;

import java.io.IOException;

import timber.log.Timber;


public class VideosManager {
    public static final String TAG = "VideosManager";
    KSYMediaPlayer currentMediaPlayer;
    String url;
    IReleaseMediaPlayerCallback mIReleaseMediaPlayerCallback;
    private static VideosManager videosManager;

    private VideosManager() {

    }

    public static VideosManager getInstance() {
        if (videosManager == null) {
            videosManager = new VideosManager();
        }
        return videosManager;
    }

    public void playVideos(KSYMediaPlayer mediaPlayer, String url, IReleaseMediaPlayerCallback releaseMediaPlayerCallback) {
        if (StringUtil.isNullOrEmptyString(url)) return;
        //String connectUrl = url.replace(":1935", "");
        String connectUrl = url;

//        if(currentMediaPlayer!=null ){
//            LogUtils.logV(TAG, "Current URL player" +currentMediaPlayer.getConfig().getConnectionUrl()+" getState() : "+ currentMediaPlayer.getState().toString());
//        }
//        if(currentMediaPlayer!=null &&  currentMediaPlayer.getConfig().getConnectionUrl().equals(connectUrl)&&
//                (currentMediaPlayer.getState() == MediaPlayer.PlayerState.Started || currentMediaPlayer.getState() == MediaPlayer.PlayerState.Opening)){
//            return;
//        }

        if (currentMediaPlayer != null && connectUrl.equals(getUrl()) && currentMediaPlayer.isPlaying()) {
            return;
        }

        stopAnyPlayback();

        // if it calls UpdateView(true), aspect of some videos ( rotate = 90 case ) will be wrong.
        // so comment outed this method.
//        mediaPlayer.UpdateView(true);
//        mediaPlayer.toggleMute(true);

        try {
            mediaPlayer.setDataSource(connectUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaPlayer != null) {
            mediaPlayer.prepareAsync();
            mediaPlayer.start();
        }

        currentMediaPlayer = mediaPlayer;
        this.url = connectUrl;
        this.mIReleaseMediaPlayerCallback = releaseMediaPlayerCallback;
        Timber.e("debug playVideos - " + this.url);
    }

    public void stopAnyPlayback() {
//        Log.d(TAG, "debug stopAnyPlayback - " + url);

        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
            currentMediaPlayer.release();
            currentMediaPlayer = null;
            url = null;

            if (mIReleaseMediaPlayerCallback != null) {
                mIReleaseMediaPlayerCallback.onReleaseComplete();
            }
            mIReleaseMediaPlayerCallback = null;
        }
    }

    public void resetMediaPlayer() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
            currentMediaPlayer.release();
            currentMediaPlayer = null;
            url = null;
            if (mIReleaseMediaPlayerCallback != null) {
                mIReleaseMediaPlayerCallback.onReleaseComplete();
            }
            mIReleaseMediaPlayerCallback = null;
        }

    }

    public String getUrl() {
        return url;
    }
}
