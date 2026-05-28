package com.example.bob.data.androidtools.sms

import android.content.Context
import android.provider.Telephony
import com.example.bob.core.tools.BobTool
import com.example.bob.core.tools.ToolResult

class SearchSmsTool(
    private val context: Context,
) : BobTool {

    override val name: String = "search_sms"

    override val description: String = "Searches the user's SMS messages."
    override val requiresConfirmation: Boolean = false

    override val requiredPermissions: List<String> = listOf(
        android.Manifest.permission.READ_SMS
    )
    override suspend fun execute(arguments: Map<String, String>): ToolResult {
        val query = arguments["query"].orEmpty()

        if (query.isBlank()) {
            return ToolResult(
                success = false,
                content = "No search query provided."
            )
        }

        val results = mutableListOf<String>()

        val projection = arrayOf(
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
        )

        context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            projection,
            "${Telephony.Sms.BODY} LIKE ?",
            arrayOf("%$query%"),
            "${Telephony.Sms.DATE} DESC"
        )?.use { cursor ->

            val addressIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val bodyIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)

            while (cursor.moveToNext() && results.size < 5) {
                val address = cursor.getString(addressIndex)
                val body = cursor.getString(bodyIndex)

                results.add("From $address: $body")
            }
        }

        return ToolResult(
            success = true,
            content = results.joinToString("\n").ifBlank {
                "No matching SMS messages found."
            }
        )
    }
}