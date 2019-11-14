package com.appster.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import com.appster.extensions.then

/**
 * Created by thanhbc on 3/29/18.
 */
class SwipeUpDownDismissTouchListener(// Fixed properties
        private val mView: View,
        private var mToken: Any?,
        private val mCallbacks: DismissCallbacks) : View.OnTouchListener {
    // Cached ViewConfiguration and system-wide constant values
    private val mSlop: Int
    private val mMinFlingVelocity: Int
    private val mMaxFlingVelocity: Int
    private val mAnimationTime: Long
    private var mViewHeight = 1 // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()
    private var mSwiping: Boolean = false
    private var mSwipingSlop: Int = 0
    private var mVelocityTracker: VelocityTracker? = null
    private var mTranslationY: Float = 0.toFloat()

    private val mWasMoved: Boolean = false

    /**
     * The callback interface used by [SwipeLeftRightDismissTouchListener] to inform its client
     * about a successful dismissal of the view for which it was created.
     */
    interface DismissCallbacks {
        /**
         * Called to determine whether the view can be dismissed.
         */
        fun canDismiss(token: Any?): Boolean

        /**
         * Called when the user has indicated they she would like to dismiss the view.
         *
         * @param view  The originating [View] to be dismissed.
         * @param token The optional token passed to this object's constructor.
         */
        fun onDismiss(view: View, token: Any?)
    }

    init {
        val vc = ViewConfiguration.get(mView.context)
        mSlop = vc.scaledTouchSlop
        mMinFlingVelocity = vc.scaledMinimumFlingVelocity * 16
        mMaxFlingVelocity = vc.scaledMaximumFlingVelocity
        mAnimationTime = mView.context.resources.getInteger(
                android.R.integer.config_shortAnimTime).toLong()
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        // offset because the view is translated during swipe
        motionEvent.offsetLocation(mTranslationY, 0f)

        if (mViewHeight < 2) {
            mViewHeight = mView.height
        }

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // TODO: ensure this is a finger, and set a flag
                mDownX = motionEvent.rawX
                mDownY = motionEvent.rawY
                if (mCallbacks.canDismiss(mToken)) {
                    mVelocityTracker = VelocityTracker.obtain()
                    mVelocityTracker?.addMovement(motionEvent)
                }
            }

            MotionEvent.ACTION_UP -> {
                mVelocityTracker?.let {
                    val deltaY = motionEvent.rawY - mDownY
                    it.addMovement(motionEvent)
                    it.computeCurrentVelocity(1000)
                    val velocityY = it.yVelocity
                    val absVelocityX = Math.abs(it.xVelocity)
                    val absVelocityY = Math.abs(velocityY)
                    var dismiss = false
                    var dismissUp = false
                    if (Math.abs(deltaY) > mViewHeight / 2 && mSwiping) {
                        dismiss = true
                        dismissUp = deltaY > 0
                    } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                            && absVelocityY < absVelocityX
                            && absVelocityY < absVelocityX && mSwiping) {
                        // dismiss only if flinging in the same direction as dragging
                        dismiss = velocityY < 0 == deltaY < 0
                        dismissUp = it.yVelocity > 0
                    }
                    if (dismiss) {
                        // dismiss
//                        (if (dismissRight) mViewHeight else -mViewHeight).toFloat()
                        mView.animate()
                                .translationY((dismissUp then mViewHeight
                                        ?: -mViewHeight).toFloat())
                                .alpha(0f)
                                .setDuration(mAnimationTime)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        performDismiss()
                                    }
                                })
                        return true
                    } else if (mSwiping) {
                        // cancel
                        mView.animate()
                                .translationY(0f)
                                .alpha(1f)
                                .setDuration(mAnimationTime)
                                .setListener(null)
                    }
                    it.recycle()
                    mVelocityTracker = null
                    mTranslationY = 0f
                    mDownX = 0f
                    mDownY = 0f
                    mSwiping = false
                }
            }

            MotionEvent.ACTION_CANCEL -> {

                mVelocityTracker?.let {
                    mView.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setDuration(mAnimationTime)
                            .setListener(null)
                    it.recycle()
                    mVelocityTracker = null
                    mTranslationY = 0f
                    mDownX = 0f
                    mDownY = 0f
                    mSwiping = false
                }
            }

            MotionEvent.ACTION_MOVE -> {
                mVelocityTracker?.let {
                    it.addMovement(motionEvent)
                    val deltaX = motionEvent.rawX - mDownX
                    val deltaY = motionEvent.rawY - mDownY
                    if (Math.abs(deltaY) > mSlop && Math.abs(deltaX) < Math.abs(deltaY) / 2) {
                        mSwiping = true
                        mSwipingSlop = (deltaY > 0) then mSlop ?: -mSlop
                        mView.parent.requestDisallowInterceptTouchEvent(true)

                        // Cancel listview's touch
                        val cancelEvent = MotionEvent.obtain(motionEvent)
                        cancelEvent.action = MotionEvent.ACTION_CANCEL or (motionEvent.actionIndex shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)
                        mView.onTouchEvent(cancelEvent)
                        cancelEvent.recycle()
                    }

                    if (mSwiping) {
                        mTranslationY = deltaY
                        mView.translationY = deltaY - mSwipingSlop
                        // TODO: use an ease-out interpolator or such
                        mView.alpha = Math.max(0f, Math.min(1f,
                                1f - 2f * Math.abs(deltaY) / mViewHeight))
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun performDismiss() {
        // Animate the dismissed view to zero-height and then fire the dismiss callback.
        // This triggers layout on each animation frame; in the future we may want to do something
        // smarter and more performant.

        val lp = mView.layoutParams
        val originalWidth = mView.width

        val animator = ValueAnimator.ofInt(originalWidth, 1).setDuration(mAnimationTime)

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mCallbacks.onDismiss(mView, mToken)
                // Reset view presentation
                mView.alpha = 1f
                mView.translationY = 0f
                lp.width = originalWidth
                mView.layoutParams = lp
            }
        })

        animator.addUpdateListener { valueAnimator ->
            lp.width = valueAnimator.animatedValue as Int
            mView.layoutParams = lp
        }

        animator.start()
    }
}