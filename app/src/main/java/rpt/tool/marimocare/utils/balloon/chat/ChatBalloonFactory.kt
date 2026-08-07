package rpt.tool.marimocare.utils.balloon.chat

import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.balloon.BaseBalloonFactory

class ChatBalloonFactory: BaseBalloonFactory() {
    override val textResource: Int = R.string.chat_balloon_text
    override val backgroundColorResource: Int = R.color.marimo_item_green
    override val dismissWhenClicked: Boolean = true
}
