package rpt.tool.marimocare.utils.balloon

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonHighlightAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect
import rpt.tool.marimocare.R

abstract class BaseBalloonFactory : Balloon.Factory() {

    @get:StringRes
    abstract val textResource: Int

    @get:ColorRes
    open val backgroundColorResource: Int = R.color.marimo_accent

    open val dismissWhenClicked: Boolean = false

    override fun create(context: Context, lifecycle: LifecycleOwner?): Balloon {
        return Balloon.Builder(context)
            .setText(context.getText(textResource))
            .setArrowSize(10)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setPadding(12)
            .setMarginRight(12)
            .setMarginLeft(12)
            .setTextSize(15f)
            .setCornerRadius(8f)
            .setTextColorResource(R.color.marimo_white)
            .setBackgroundColorResource(backgroundColorResource)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setIsVisibleOverlay(true)
            .setOverlayColorResource(R.color.overlay)
            .setOverlayPaddingResource(R.dimen.editBalloonOverlayPadding)
            .setBalloonHighlightAnimation(BalloonHighlightAnimation.SHAKE)
            .setOverlayShape(
                BalloonOverlayRoundRect(
                    R.dimen.editBalloonOverlayRadius,
                    R.dimen.editBalloonOverlayRadius,
                ),
            )
            .setLifecycleOwner(lifecycle)
            .setDismissWhenClicked(dismissWhenClicked)
            .build()
    }
}
