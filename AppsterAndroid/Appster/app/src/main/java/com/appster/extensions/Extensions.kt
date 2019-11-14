package com.appster.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.appster.R
import com.appster.features.mvpbase.BaseContract
import com.appster.utility.ImageLoaderUtil
import com.appster.utility.ImageLoaderUtil.ScaleType.NONE
import com.appster.utility.glide.GlideApp
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.pack.utility.StringUtil

/**
 * Created by thanhbc on 3/29/18.
 */
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ImageView.loadImg(imageUrl: String?) {
    if (imageUrl != null) {
        displayImage(context.applicationContext, imageUrl.trim(), this)
    }
}

fun ImageView.loadImg(@DrawableRes drawableId: Int?) {
    displayImage(context.applicationContext, drawableId, this)
}

fun ImageView.loadImg(@DrawableRes drawableId: Int?, @DrawableRes holder: Int) {
    displayImage(context.applicationContext, drawableId, imageHolder = holder, imageView = this)
}

fun ImageView.loadImg(imageUrl: String?, @DrawableRes holder: Int) {
    if (imageUrl != null) {
        displayImage(context.applicationContext, imageUrl.trim(), imageHolder = holder, imageView = this)
    }
}

inline fun <T> supportAndroid(androidVer: Int, code: () -> T, nonSupportCode: () -> T) =
        (Build.VERSION.SDK_INT >= androidVer) then code() ?: nonSupportCode()

@SuppressLint("NewApi")
fun String.fromHtml(): Spanned {
    return supportAndroid(Build.VERSION_CODES.N, { Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT) }, { Html.fromHtml(this) })
}

fun String.decodeEmoji(): String = StringUtil.decodeString(this)

fun String.toUserName(): String = String.format("@%s", this)

@SuppressLint("CheckResult")
private fun displayImage(context: Context?, url: Any?, imageView: ImageView, @ImageLoaderUtil.ScaleType scaleType: Int = ImageLoaderUtil.ScaleType.NONE, @DrawableRes imageHolder: Int = R.drawable.user_image_default, width: Int = 0, height: Int = 0, transformation: Transformation<Bitmap>? = null, callback: ImageLoaderUtil.ImageLoaderCallback? = null) {
    if (context == null) return
    var requestOptions = RequestOptions()
    if (imageHolder != 0) {
        requestOptions.placeholder(imageHolder).error(imageHolder).fallback(imageHolder)
    }

    if (width != 0 && height != 0) {
        requestOptions.override(width, height)
    }

    if (transformation != null) {
        requestOptions.transform(transformation)
    }

    //none is default scale type to respect image size
    if (scaleType != NONE) {
        requestOptions = applyScaleType(requestOptions, scaleType)
    }

    GlideApp.with(context)
            .load(url)
            .apply(requestOptions)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    callback?.onFailed(e)
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    callback?.onSuccess((resource as BitmapDrawable).bitmap)
                    return false
                }
            })
            .into(imageView)
}

private fun applyScaleType(oldRequest: RequestOptions, @ImageLoaderUtil.ScaleType scaleType: Int): RequestOptions {
    val requestOptions = oldRequest.clone()
//    if (scaleType == NONE) return requestOptions
    return when (scaleType) {
        ImageLoaderUtil.ScaleType.CENTER_CROP -> requestOptions.centerCrop()
        ImageLoaderUtil.ScaleType.CENTER_INSIDE -> requestOptions.centerInside()
        ImageLoaderUtil.ScaleType.FIT_CENTER -> requestOptions.fitCenter()
        ImageLoaderUtil.ScaleType.NONE -> requestOptions //will never happen
        else -> requestOptions
    }
}

infix fun <T> Boolean.then(param: T): T? = if (this) param else null

infix fun <T> Boolean.then(param: () -> T): T? = if (this) param() else null

fun Boolean.toInt() = if (this) 1 else 0

fun View.cleanCurrencyValue(value: Double): String {
    return convertCurrency(value, context)
}

fun BaseContract.View.cleanCurrencyValue(value: Double): String {
    return convertCurrency(value, viewContext)
}

private fun convertCurrency(value: Double, context: Context): String {
    return context.getString(((value % 1 == 0.0) then R.string.dollar_amount_non_floating_point
            ?: R.string.dollar_amount), value)
}