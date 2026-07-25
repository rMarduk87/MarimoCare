package rpt.tool.marimocare.utils.data.apimodels

data class ChatRequest(
    val model: String = "openrouter/free",
    val messages: List<Message>
)