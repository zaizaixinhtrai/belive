package agora.kit;


import com.ksyun.media.streamer.util.gles.GLRender;

import agora.MediaManager;


/**
 * Created by qyvideo on 12/13/16.
 */

public class AgoraRTCClient {

    private MediaManager mMediaManager;
    private AgoraRTCIO mIO;

    public AgoraRTCClient(GLRender glRender, MediaManager mediaManager) {
        mMediaManager = mediaManager;
        mIO = new AgoraRTCIO(glRender, mediaManager);
    }

    public void release() {
        mIO.release();
    }

    public AgoraRTCIO getRTCIO() {
        return mIO;
    }

    public void joinChannel(String channel) {
        enableObserver(true);
        mMediaManager.joinChannel(channel);
    }

    public void leaveChannel() {
        enableObserver(false);
        mMediaManager.leaveChannel();
    }

    public void enableObserver(boolean enable) {
        mIO.enableObserver(enable);
    }
}
