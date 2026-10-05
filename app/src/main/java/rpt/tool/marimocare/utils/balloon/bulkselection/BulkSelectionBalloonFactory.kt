package rpt.tool.marimocare.utils.balloon.bulkselection

import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.balloon.BaseBalloonFactory

class BulkSelectionBalloonFactory : BaseBalloonFactory() {
    override val textResource: Int = R.string.bulk_selection_balloon_text
    override val dismissWhenClicked: Boolean = true
}
