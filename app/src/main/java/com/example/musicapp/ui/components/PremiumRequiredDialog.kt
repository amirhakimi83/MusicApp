package com.example.musicapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.musicapp.R

/** Prompt shown when a free user tries a Premium-only action (offline download). */
@Composable
fun PremiumRequiredDialog(
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.WorkspacePremium, contentDescription = null) },
        title = { Text(stringResource(R.string.premium_required_title)) },
        text = { Text(stringResource(R.string.premium_required_message)) },
        confirmButton = {
            TextButton(onClick = onUpgrade) {
                Text(stringResource(R.string.profile_upgrade))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}
