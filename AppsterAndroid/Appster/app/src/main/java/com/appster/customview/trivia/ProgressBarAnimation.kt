package com.appster.customview.trivia

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar

/**
 * Created by thanhbc on 2/22/18.
 */
class ProgressBarAnimation(val progressBar: ProgressBar, runTimeDuration: Long = 500) : Animation() {
    var to = 0
    var from = 0
    private var stepDuration = 0L

    constructor(progressBar: ProgressBar) : this(progressBar, 1000)

    init {
        stepDuration = runTimeDuration / progressBar.max
    }

    fun setProgress(progress: Int) {
        var pc = progress
        if (pc < 0) pc = 0
        if (pc > progressBar.max) pc = progressBar.max
        from = 0
        to = pc
        duration = Math.abs((to - from) * stepDuration)
        progressBar.postDelayed({ progressBar.startAnimation(this) },200)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val value = from + (to - from) * interpolatedTime
        progressBar.progress = value.toInt()
    }
}