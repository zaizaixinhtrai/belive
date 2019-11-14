package com.apster.common.ksy_utils;

import com.ksyun.media.player.KSYTextureView;

import static com.ksyun.media.player.KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO;

/**
 * Created by Ngoc on 4/26/2017.
 */

public class KSYConfig {
    public static void initConfig(KSYTextureView videoView){
        videoView.setKeepScreenOn(true);
        videoView.setScreenOnWhilePlaying(true);
        videoView.setTimeout(15, 30);
        videoView.setBufferTimeMax(2);
        videoView.setBufferSize(15);
        videoView.setDecodeMode(KSY_DECODE_MODE_AUTO);
    }
}
