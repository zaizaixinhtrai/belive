package com.appster.features.messages.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import com.appster.R
import com.appster.activity.BaseActivity
import com.appster.utility.ConstantBundleKey
import com.appster.utility.ImageLoaderUtil
import com.apster.common.DownloadVideos
import com.apster.common.FileUtility
import com.apster.common.LogUtils
import com.pack.utility.StringUtil
import kotlinx.android.synthetic.main.activity_view_media.*

/**
 *  Created by DatTN on 10/12/2018
 */
class MediaViewActivity : BaseActivity() {

    private var mType: Int = 0
    private var mImageUrl: String? = null
    private var mVideoUrl: String? = null
    private var mMediaController: MediaController? = null

    //region-------activity life cycle-------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_media)

        // 1. get passed intent
        val extras = intent.extras
        if (extras != null) {
            mType = extras.getInt(ConstantBundleKey.BUNDLE_TYPE_KEY)
            mImageUrl = extras.getString(ConstantBundleKey.BUNDLE_CHAT_URL_IMAGE)
            mVideoUrl = extras.getString(ConstantBundleKey.BUNDLE_CHAT_URL_VIDEO)
        }
        if (!StringUtil.isNullOrEmptyString(mVideoUrl)) {
            ImageLoaderUtil.displayMediaImage(this, mImageUrl, iv_media)
        }
        iv_play.setOnClickListener {
            if (StringUtil.isNullOrEmptyString(mVideoUrl)) {
                Toast.makeText(applicationContext, getString(R.string.can_not_play_video), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mMediaController == null) {
                iv_play.visibility = View.GONE
                iv_media.visibility = View.GONE
                pb_video.visibility = View.VISIBLE
                tt_video_view.visibility = View.VISIBLE
                handleVideoLoading(mVideoUrl!!)
            } else {
                tt_video_view.visibility = View.VISIBLE
                iv_media.visibility = View.GONE
                iv_play.visibility = View.GONE
                mMediaController!!.show()
                startVideoPlayback()
                startVideoAnimation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tt_video_view != null) {
            tt_video_view.stopPlayback()
        }
    }
    //endregion-------activity life cycle-------

    //region -------inner methods-------
    private fun handleVideoLoading(videosServerURL: String) {
        FileUtility.deleteVideoCacheFile()
        DownloadVideos.getInstance().isVideoAlreadyDownloaded(videosServerURL) { isNeedToDownload, fileName ->
            runOnUiThread {
                if (!isNeedToDownload) {
                    initVideoView(fileName)
                    LogUtils.logD("NCS", "Play video on local file: $fileName")
                } else {
                    DownloadVideos.getInstance().downloadVideoFile(videosServerURL,  object : DownloadVideos.IDownloadListener {
                        override fun successful(filePath: String) {
                            runOnUiThread { initVideoView(filePath) }
                        }

                        override fun fail() {}
                    })
                }
            }
        }
    }

    private fun initVideoView(urlVideo: String) {
        tt_video_view.setVideoPath(urlVideo)

        mMediaController = MediaController(this@MediaViewActivity)
        tt_video_view.setMediaController(mMediaController)

        tt_video_view.setOnPreparedListener {
            pb_video.visibility = View.GONE
            startVideoPlayback()
            startVideoAnimation()
        }
        tt_video_view.setOnCompletionListener {
            tt_video_view.visibility = View.GONE
            iv_play.visibility = View.VISIBLE
            iv_media.visibility = View.VISIBLE
            pb_video.visibility = View.GONE
        }
    }

    private fun startVideoAnimation() {
        if (tt_video_view.duration > 0) {
            tt_video_view.animate().setDuration(tt_video_view.duration.toLong()).start()
        }
    }

    private fun startVideoPlayback() {
        tt_video_view.start()
    }
    //endregion -------inner methods-------

    //region -------inner class-------
    companion object {
        fun createIntent(context: Context, type: Int, urlImage: String, urlVideo: String): Intent {
            val intent = Intent(context, MediaViewActivity::class.java)
            intent.putExtra(ConstantBundleKey.BUNDLE_TYPE_KEY, type)
            intent.putExtra(ConstantBundleKey.BUNDLE_CHAT_URL_VIDEO, urlVideo)
            intent.putExtra(ConstantBundleKey.BUNDLE_CHAT_URL_IMAGE, urlImage)
            return intent
        }
    }
    //endregion -------inner class-------
}