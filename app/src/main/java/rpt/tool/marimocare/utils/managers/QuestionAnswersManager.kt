package rpt.tool.marimocare.utils.managers

import android.content.Context
import rpt.com.base.log.e
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.data.api.OpenAiApi
import rpt.tool.marimocare.utils.data.apimodels.ChatRequest
import rpt.tool.marimocare.utils.data.apimodels.Message

class QuestionAnswersManager(context: Context) {

    private val _context = context

    private val SYSTEM_INSTRUCTION = _context.getString(R.string.instruction).trimIndent()
    private val elencoMessaggi = mutableListOf<Message>()

    init {
        elencoMessaggi.add(Message(role = "system", content = SYSTEM_INSTRUCTION))
    }

    suspend fun putQuestion(question: String): String {

        val userMessage = Message(role = "user", content = question)
        elencoMessaggi.add(userMessage)

        val richiesta = ChatRequest(
            model = "openrouter/free",
            messages = elencoMessaggi
        )

        val rispostaApi = try {
            OpenAiApi.getChatResponse(richiesta)
        } catch (e: Exception) {
            e(Throwable(e),
                "Errore chiamata API: ${e.localizedMessage}")
            null
        }

        val testoRisposta = rispostaApi?.choices?.firstOrNull()?.message?.content

        return if (testoRisposta != null) {
            val assistantMessage = Message(role = "assistant", content = testoRisposta)
            elencoMessaggi.add(assistantMessage)
            testoRisposta
        } else {
            elencoMessaggi.removeLastOrNull()
            _context.getString(R.string.no_answer)
        }
    }

    fun clearConversation() {
        elencoMessaggi.clear()
        elencoMessaggi.add(Message(role = "system", content = SYSTEM_INSTRUCTION))
    }
}