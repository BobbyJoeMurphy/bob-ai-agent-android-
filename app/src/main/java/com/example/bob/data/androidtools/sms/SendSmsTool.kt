package com.example.bob.data.androidtools.sms

import android.telephony.SmsManager
import com.example.bob.core.tools.BobTool
import com.example.bob.core.tools.ToolResult

class SendSmsTool : BobTool {

    override val name: String = "send_sms"

    override val description: String = "Sends an SMS message to a phone number."

    override val requiresConfirmation: Boolean = true

    override val requiredPermissions: List<String> = listOf(
        android.Manifest.permission.SEND_SMS
    )

    override suspend fun execute(
        arguments: Map<String, String>
    ): ToolResult {
        val phoneNumber = arguments["phoneNumber"].orEmpty()
        val message = arguments["message"].orEmpty()

        if (phoneNumber.isBlank()) {
            return ToolResult(
                success = false,
                content = "No phone number provided."
            )
        }

        if (message.isBlank()) {
            return ToolResult(
                success = false,
                content = "No message provided."
            )
        }

        return try {
            SmsManager.getDefault().sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )

            ToolResult(
                success = true,
                content = "Message sent to $phoneNumber: $message"
            )
        } catch (e: Exception) {
            ToolResult(
                success = false,
                content = "Failed to send SMS: ${e.message}"
            )
        }
    }
}