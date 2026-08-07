package rpt.tool.marimocare.utils.data.apimodels

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    val id: String,
    @SerializedName("object")
    val objectName: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: Any?,
    @SerializedName("finish_reason")
    val finishReason: String
)

data class Message(
    val role: String,
    val content: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)