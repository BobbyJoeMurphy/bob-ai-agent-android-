package com.example.bob.feature.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*

@Composable
fun rememberSmsPermissionState(): SmsPermissionState {

    var hasPermission by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    return SmsPermissionState(
        hasPermission = hasPermission,
        requestPermission = {
            launcher.launch(Manifest.permission.READ_SMS)
        }
    )
}

data class SmsPermissionState(
    val hasPermission: Boolean,
    val requestPermission: () -> Unit
)