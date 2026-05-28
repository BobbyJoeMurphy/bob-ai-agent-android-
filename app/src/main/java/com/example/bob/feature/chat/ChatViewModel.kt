package com.example.bob.feature.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bob.core.agent.AgentManager
import com.example.bob.core.tools.ToolRegistry
import com.example.bob.data.androidtools.remote.openai.OpenAiLlmClient
import com.example.bob.data.androidtools.sms.SearchSmsTool
import com.example.bob.data.androidtools.test.EchoTool
import com.example.bob.domain.model.BobMessage
import com.example.bob.domain.model.ConversationMessage
import com.example.bob.domain.model.MessageSender
import com.example.bob.domain.usecase.SendUserMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.bob.core.agent.AgentResult
import com.example.bob.data.androidtools.contacts.FindContactTool
import com.example.bob.data.androidtools.sms.SendSmsTool

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val conversationHistory = mutableListOf<ConversationMessage>()

    private val sendUserMessageUseCase = SendUserMessageUseCase(
        agentManager = AgentManager(
            llmClient = OpenAiLlmClient(),
            toolRegistry = ToolRegistry(
                tools = listOf(
                    EchoTool(),
                    SearchSmsTool(application.applicationContext),
                    SendSmsTool(),
                    FindContactTool(application.applicationContext)
                )
            )
        )
    )

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputChanged(value: String) {
        _uiState.update {
            it.copy(inputText = value)
        }
    }

    fun sendMessage() {
        val message = _uiState.value.inputText.trim()

        if (message.isBlank()) return

        val userMessage = BobMessage(
            id = UUID.randomUUID().toString(),
            content = message,
            sender = MessageSender.USER
        )

        conversationHistory.add(
            ConversationMessage(
                role = "user",
                content = message
            )
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val result = sendUserMessageUseCase(
                    message = message,
                    history = conversationHistory.toList()
                )

                val response = when (result) {
                    is AgentResult.FinalResponse -> {
                        result.message
                    }

                    is AgentResult.ConfirmationRequired -> {
                        _uiState.update {
                            it.copy(
                                pendingConfirmation = result.toolCall
                            )
                        }

                        result.message
                    }

                    is AgentResult.Error -> {
                        result.message
                    }
                }

                val bobMessage = BobMessage(
                    id = UUID.randomUUID().toString(),
                    content = response,
                    sender = MessageSender.BOB
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + bobMessage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Something went wrong"
                    )
                }
            }
        }
    }
    fun confirmPendingAction() {
        val pendingToolCall = _uiState.value.pendingConfirmation ?: return

        _uiState.update {
            it.copy(
                pendingConfirmation = null,
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                val result = sendUserMessageUseCase.executeTool(pendingToolCall)

                val message = BobMessage(
                    id = UUID.randomUUID().toString(),
                    content = result.content,
                    sender = MessageSender.BOB
                )

                conversationHistory.add(
                    ConversationMessage(
                        role = "assistant",
                        content = result.content
                    )
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + message,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Something went wrong"
                    )
                }
            }
        }
    }

    fun cancelPendingAction() {
        _uiState.update {
            it.copy(
                pendingConfirmation = null,
                isLoading = false,
                messages = it.messages + BobMessage(
                    id = UUID.randomUUID().toString(),
                    content = "Okay, I cancelled that action.",
                    sender = MessageSender.BOB
                )
            )
        }
    }
}