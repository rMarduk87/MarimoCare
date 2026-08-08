package rpt.tool.marimocare.utils.balloon.migration

import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.balloon.BaseBalloonFactory

class MigrationBalloonFactory: BaseBalloonFactory() {
    override val textResource: Int = R.string.migration_balloon_text
    override val dismissWhenClicked: Boolean = true
}
