package rpt.tool.marimocare.utils.balloon.settings

import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.balloon.BaseBalloonFactory

class SettingsBalloonFactory: BaseBalloonFactory() {
    override val textResource: Int = R.string.new_settings_balloon_text
    override val dismissWhenClicked: Boolean = true
}
