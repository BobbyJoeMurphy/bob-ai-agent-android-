package com.example.bob.data.androidtools.remote.openai

import com.example.bob.core.agent.AgentDecision
import com.example.bob.core.agent.AgentDecisionParser
import com.example.bob.core.llm.LlmClient
import com.example.bob.core.tools.ToolCall
import com.example.bob.core.tools.ToolResult
import com.example.bob.data.androidtools.remote.openai.dto.OpenAiRequest
import com.example.bob.domain.model.ConversationMessage

class OpenAiLlmClient(
    private val api: OpenAiApi = OpenAiService.api
) : LlmClient {

    private val parser = AgentDecisionParser()

    override suspend fun getDecision(
        message: String,
        history: List<ConversationMessage>
    ): AgentDecision {

        val conversationHistory = history.joinToString("\n") {
            "${it.role}: ${it.content}"
        }

        val prompt = """
            You are Bob, an Android AI assistant.

            Conversation history:
            $conversationHistory

            You must ONLY respond with valid JSON.

            Available response types:

            1. Normal reply:
            {
              "type": "final_response",
              "message": "your message here"
            }

         2. Tool call:
        {
          "type": "tool_call",
          "tool": "tool_name",
         "arguments": {
           "key": "value"
         }
         }
        3. Confirmation required:
        {
          "type": "requires_confirmation",
         "message": "Send this message to 07300872109?",
         "tool": "send_sms",
         "arguments": {
           "phoneNumber": "07300872109",
          "message": "I'm on my way"
         }
            }
            Available tools:
            
            find_contact:
            - Use when the user gives a contact name and you need their phone number.
            - Arguments:
             {
             "name": "contact name"
              }
            send_sms:
            - Use when the user asks you to send a text/SMS/message to a phone number.
            - This tool requires confirmation before execution.
            - Arguments:
            {
              "phoneNumber": "recipient phone number",
              "message": "message to send"
             }
             
             
            echo_tool:
            - Use when the user asks you to echo/repeat something.
            - Arguments:
              {
                "text": "text to echo"
              }

           search_sms:
- Use when the user asks you to search, find, check, or look through their text messages/SMS.
- ALSO use it when the answer may exist in previous SMS content.
- If the user asks about:
  - dinner
  - a postcode
  - a person texting them
  - information "in the text"
  - messages from someone
  then prefer search_sms.
    - Arguments:
              {
                "query": "keyword or phrase to search for"
              }

            Rules:
            - Use search_sms for requests like:
              "Find messages about dinner"
              "Search my texts for postcode"
              "What did Mum say about tomorrow?"
            - Do not use search_sms for general questions.
            - NEVER put the tool name inside "type".
            - For tools, "type" must always be "tool_call".
            - Do not include markdown.
            - Do not include explanations outside the JSON.
            - ALWAYS prefer search_sms if the answer could exist inside SMS messages.
            - If uncertain whether the answer is from SMS history, use search_sms.
            - Do not hallucinate SMS contents from general knowledge.
            - Use search_sms before answering questions about texts/messages.
            - Use send_sms only when the user clearly provides both the recipient phone number and the message.
            - If the user gives a contact name but no phone number, ask for the phone number for now.
            - Never execute send_sms without confirmation.
            - NEVER ask for confirmation conversationally.
            - If a tool requires confirmation, ALWAYS return:
              {
                "type": "requires_confirmation"
              }
            - The app UI handles confirmation.
            - Do not ask the user to reply yes/no in chat.
            - If the user asks to send a message to a contact name, first use find_contact.
            - Do not ask the user for the phone number until find_contact has been tried.
            - If find_contact returns one clear result, use that phone number for send_sms.
            User message: $message
        """.trimIndent()

        val response = api.createResponse(
            OpenAiRequest(
                model = "gpt-4.1-mini",
                input = prompt
            )
        )

        val rawText = response.output
            .flatMap { it.content }
            .firstOrNull { it.text != null }
            ?.text
            ?: """{"type":"final_response","message":"I couldn't generate a response."}"""

        return parser.parse(rawText)
    }

    override suspend fun getDecisionAfterTool(
        originalMessage: String,
        toolCall: ToolCall,
        toolResult: ToolResult,
        history: List<ConversationMessage>
    ): AgentDecision {

        val conversationHistory = history.joinToString("\n") {
            "${it.role}: ${it.content}"
        }

        val prompt = """
        You are Bob, an Android AI assistant.

        Conversation history:
        $conversationHistory

        The user originally said:
        "$originalMessage"

        You used this tool:
        ${toolCall.name}

        Tool result:
        ${toolResult.content}

        You must ONLY respond with valid JSON.

        If the tool result gives enough information to continue with another tool, return the next tool action.

        Example:
        User asked: "Send Bobby hello"
        Tool used: find_contact
        Tool result: "Bobby: 07300111222"

        You should return:
        {
          "type": "requires_confirmation",
          "message": "Send this message to Bobby at 07300111222?",
          "tool": "send_sms",
          "arguments": {
            "phoneNumber": "07300111222",
            "message": "hello"
          }
        }

        Otherwise return:
        {
          "type": "final_response",
          "message": "your response here"
        }

        Rules:
        - NEVER output anything outside JSON.
        - If sending an SMS, always use "requires_confirmation".
        - Never ask for yes/no in chat.
        - The app UI handles confirmation.
    """.trimIndent()

        val response = api.createResponse(
            OpenAiRequest(
                model = "gpt-4.1-mini",
                input = prompt
            )
        )

        val rawText = response.output
            .flatMap { it.content }
            .firstOrNull { it.text != null }
            ?.text
            ?: """{"type":"final_response","message":"I couldn't continue after using the tool."}"""

        return parser.parse(rawText)
    }

    override suspend fun generateToolResponse(
        originalMessage: String,
        toolCall: ToolCall,
        toolResult: ToolResult,
        history: List<ConversationMessage>
    ): String {

        val conversationHistory = history.joinToString("\n") {
            "${it.role}: ${it.content}"
        }

        val prompt = """
            You are Bob, an Android AI assistant.

            Conversation history:
            $conversationHistory

            The user originally said:
            "$originalMessage"

            You used the tool:
            "${toolCall.name}"

            Tool result:
            "${toolResult.content}"

            Respond naturally to the user.
        """.trimIndent()

        val response = api.createResponse(
            OpenAiRequest(
                model = "gpt-4.1-mini",
                input = prompt
            )
        )

        return response.output
            .flatMap { it.content }
            .firstOrNull { it.text != null }
            ?.text
            ?: toolResult.content
    }
}