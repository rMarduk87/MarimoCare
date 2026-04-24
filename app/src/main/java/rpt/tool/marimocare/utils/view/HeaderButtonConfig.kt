package rpt.tool.marimocare.utils.view

import android.content.Context
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

data class HeaderButtonConfig(
    val button: Button,
    val iconRes: Int,
    val colorRes: Int,
    val backgroundRes: Int,
    val enabled: Boolean = true,
    val onClick: (() -> Unit)? = null,
    val isTablet: Boolean = false,
    val text: String? = null
)

object HeaderHelper {

    fun setupHeaderButtons(context: Context, configs: List<HeaderButtonConfig>) {
        configs.forEach { config ->
            config.button.apply {
                if (config.isTablet) {
                    text = config.text
                    setTextColor(ContextCompat.getColor(context, config.colorRes))
                    background = ContextCompat.getDrawable(context, config.backgroundRes)
                    setCompoundDrawablesWithIntrinsicBounds(config.iconRes, 0, 0, 0)
                    val drawable = compoundDrawables[0]
                    if (drawable != null) {
                        DrawableCompat.setTint(
                            drawable,
                            ContextCompat.getColor(context, config.colorRes)
                        )
                    }
                } else {
                    text = ""
                    background = null
                    setCompoundDrawablesWithIntrinsicBounds(0, config.iconRes, 0, 0)
                    val drawable = compoundDrawables[1]
                    if (drawable != null) {
                        DrawableCompat.setTint(
                            drawable,
                            ContextCompat.getColor(context, config.colorRes)
                        )
                    }
                }
                isEnabled = config.enabled
                config.onClick?.let { setOnClickListener { it() } }
            }
        }
    }
}