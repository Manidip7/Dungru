package com.aksar.dungru.utils

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.roundToInt

class HorizontalMarginItemDecoration(context: Context, horizontalMarginInDp: Int) :
RecyclerView.ItemDecoration() {
    private val horizontalMarginInPx: Int = dpToPx(context,horizontalMarginInDp)
private val horizontalMarginInPx1: Int = dpToPx(context,10)

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }

}

fun ViewPager2.setPreviewBothSide( nextItemVisibleSize: Int, currentItemHorizontalMargin: Int) {
    this.offscreenPageLimit = 1
    val nextItemVisiblePx = dpToPx(context,nextItemVisibleSize)
    val currentItemHorizontalMarginPx = dpToPx(context,currentItemHorizontalMargin )
    val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
    val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
        page.translationX = -pageTranslationX * position
    }
    this.setPageTransformer(pageTransformer)

    val itemDecoration = HorizontalMarginItemDecoration(
        context,
        currentItemHorizontalMargin
    )
    this.addItemDecoration(itemDecoration)
}

fun ViewPager2.setPreviewBothSideWithScale( nextItemVisibleSize: Int, currentItemHorizontalMargin: Int) {
    this.offscreenPageLimit = 1
    val nextItemVisiblePx = dpToPx(context,nextItemVisibleSize)
    val currentItemHorizontalMarginPx = dpToPx(context,currentItemHorizontalMargin )
    val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
    val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
        page.translationX = -pageTranslationX * position
        page.scaleY = 1 - (0.1f * kotlin.math.abs(position))
    }
    this.setPageTransformer(pageTransformer)

    val itemDecoration = HorizontalMarginItemDecoration(
        context,
        currentItemHorizontalMargin
    )
    this.addItemDecoration(itemDecoration)
}

private fun dpToPx(context : Context, dp: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}
