package com.aksar.dungru.utils.extensions

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import com.aksar.dungru.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

fun slideInFromRightAnimationShow(context: Context, view: View?) {
    view?.visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
    view?.startAnimation(animation)
}

fun slideUpAnimationShow(context: Context, view: View?) {
    view?.visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
    view?.startAnimation(animation)
}
fun slideDownAnimationShow(context: Context, view: View?) {
    val animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
    view?.startAnimation(animation)
}

/**Simmer Animations*/
//Shimmer Drawable
fun getShimmerDrawableForGlide():ShimmerDrawable{
    val shimmer = Shimmer.AlphaHighlightBuilder()
        .setDuration(1800)
        .setBaseAlpha(0.7f)
        .setHighlightAlpha(0.6f)
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    return ShimmerDrawable().apply { setShimmer(shimmer) }
}

