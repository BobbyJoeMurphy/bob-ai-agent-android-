package com.example.bob.data.androidtools.contacts

import android.content.Context
import android.provider.ContactsContract
import com.example.bob.core.tools.BobTool
import com.example.bob.core.tools.ToolResult

class FindContactTool(
    private val context: Context
) : BobTool {

    override val name: String = "find_contact"

    override val description: String = "Finds a contact phone number by name."

    override val requiresConfirmation: Boolean = false

    override val requiredPermissions: List<String> = listOf(
        android.Manifest.permission.READ_CONTACTS
    )

    override suspend fun execute(
        arguments: Map<String, String>
    ): ToolResult {
        val nameQuery = arguments["name"].orEmpty()

        if (nameQuery.isBlank()) {
            return ToolResult(
                success = false,
                content = "No contact name provided."
            )
        }

        val results = mutableListOf<String>()

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        context.contentResolver.query(
            uri,
            projection,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
            arrayOf("%$nameQuery%"),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )?.use { cursor ->

            val nameIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )

            val numberIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (cursor.moveToNext() && results.size < 5) {
                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)

                results.add("$name: $number")
            }
        }

        return ToolResult(
            success = true,
            content = results.joinToString("\n").ifBlank {
                "No matching contacts found for $nameQuery."
            }
        )
    }
}