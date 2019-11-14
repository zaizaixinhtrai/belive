package com.appster.customview.trivia

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.RenderMode
import com.appster.R
import kotlinx.android.synthetic.main.item_trivia_revive.view.*
import kotlin.properties.Delegates


/**
 * Created by thanhbc on 4/17/18.
 */

class TriviaReviveView : FrameLayout {


    var currentRevive: Int by Delegates.observable(0) { _, _, new ->
        tvReviveCount.text = new.toString()
    }
    private val REVIVE_ANIM_FILE: String = "circlesflare.json"

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        context?.let {
            View.inflate(it, R.layout.item_trivia_revive, this)
            lottieReviveAnim.setRenderMode(RenderMode.HARDWARE)
            lottieReviveAnim.enableMergePathsForKitKatAndAbove(true)

        }
    }

    private fun loadJsonString(fileName: String) {
        if (context == null) {
            return
        }
        LottieComposition.Factory.fromAssetFileName(context, fileName) { composition ->
            if (composition != null) {
                setComposition(composition)
            }
        }
    }


    fun increaseReviveWithAnimation(reviveCount: Int) {
        loadJsonString(REVIVE_ANIM_FILE)
        postDelayed({ currentRevive = reviveCount }, 1000)
    }

    private fun setComposition(composition: LottieComposition) {
        if (composition.hasImages() && TextUtils.isEmpty(lottieReviveAnim.imageAssetsFolder)) {
            return
        }

        lottieReviveAnim.setComposition(composition)
        playAnimation()
    }

    fun playAnimation() {
        lottieReviveAnim.visibility = View.VISIBLE
        lottieReviveAnim.progress = 0f
        lottieReviveAnim.playAnimation()
    }

}
