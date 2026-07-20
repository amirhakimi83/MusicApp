package com.example.musicapp.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.core.util.LocaleManager
import com.example.musicapp.domain.model.AppTheme
import com.example.musicapp.domain.model.FontScale
import com.example.musicapp.ui.components.DetailTopBar
import com.example.musicapp.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            DetailTopBar(title = stringResource(R.string.settings_title), onBack = onBack)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            // Language
            SettingsGroup(title = stringResource(R.string.settings_language)) {
                RadioRow(
                    label = stringResource(R.string.settings_language_en),
                    selected = prefs.language == LocaleManager.LANG_ENGLISH,
                    onClick = { viewModel.setLanguage(LocaleManager.LANG_ENGLISH) },
                )
                RadioRow(
                    label = stringResource(R.string.settings_language_fa),
                    selected = prefs.language == LocaleManager.LANG_PERSIAN,
                    onClick = { viewModel.setLanguage(LocaleManager.LANG_PERSIAN) },
                )
            }

            HorizontalDivider()

            // Theme
            SettingsGroup(title = stringResource(R.string.settings_theme)) {
                RadioRow(
                    label = stringResource(R.string.settings_theme_light),
                    selected = prefs.theme == AppTheme.LIGHT,
                    onClick = { viewModel.setTheme(AppTheme.LIGHT) },
                )
                RadioRow(
                    label = stringResource(R.string.settings_theme_dark),
                    selected = prefs.theme == AppTheme.DARK,
                    onClick = { viewModel.setTheme(AppTheme.DARK) },
                )
                RadioRow(
                    label = stringResource(R.string.settings_theme_system),
                    selected = prefs.theme == AppTheme.SYSTEM,
                    onClick = { viewModel.setTheme(AppTheme.SYSTEM) },
                )
            }

            HorizontalDivider()

            // Font size
            SettingsGroup(title = stringResource(R.string.settings_font_size)) {
                RadioRow(
                    label = stringResource(R.string.settings_font_small),
                    selected = prefs.fontScale == FontScale.SMALL,
                    onClick = { viewModel.setFontScale(FontScale.SMALL) },
                )
                RadioRow(
                    label = stringResource(R.string.settings_font_normal),
                    selected = prefs.fontScale == FontScale.NORMAL,
                    onClick = { viewModel.setFontScale(FontScale.NORMAL) },
                )
                RadioRow(
                    label = stringResource(R.string.settings_font_large),
                    selected = prefs.fontScale == FontScale.LARGE,
                    onClick = { viewModel.setFontScale(FontScale.LARGE) },
                )
            }

            HorizontalDivider()

            // Material You (dynamic color)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.screen,
                        vertical = MaterialTheme.spacing.small,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.settings_dynamic_color),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = prefs.dynamicColor,
                    onCheckedChange = { viewModel.setDynamicColor(it) },
                )
            }

            HorizontalDivider()

            // Logout
            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.screen,
                        vertical = MaterialTheme.spacing.small,
                    ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.settings_logout),
                    modifier = Modifier.padding(start = MaterialTheme.spacing.small),
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.settings_logout_title)) },
            text = { Text(stringResource(R.string.settings_logout_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                }) {
                    Text(stringResource(R.string.settings_logout))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            horizontal = MaterialTheme.spacing.screen,
            vertical = MaterialTheme.spacing.small,
        ),
    )
    content()
}

@Composable
private fun RadioRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(
                horizontal = MaterialTheme.spacing.screen,
                vertical = MaterialTheme.spacing.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}
