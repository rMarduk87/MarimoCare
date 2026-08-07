package rpt.tool.marimocare.utils.balloon.achievement

import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.balloon.BaseBalloonFactory

class AchievementUnlockedBalloonFactory: BaseBalloonFactory() {
    override val textResource: Int = R.string.new_achievements_unlocked_balloon
    override val dismissWhenClicked: Boolean = true
}
