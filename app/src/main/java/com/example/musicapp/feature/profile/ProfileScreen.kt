package com.example.musicapp.feature.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.ui.components.UserAvatar
import com.example.musicapp.ui.theme.spacing

@Composable
fun ProfileScreen(
    onOpenSettings: () -> Unit,
    onOpenLiked: () -> Unit,
    onOpenRecent: () -> Unit,
    onOpenArtists: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenPeople: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val premiumMessage = stringResource(R.string.premium_success)
    var showUpgradeDialog by remember { mutableStateOf(false) }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.changeAvatar(it.toString()) }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.premiumActivated.collect {
            snackbarHostState.showSnackbar(premiumMessage)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            // Avatar with change button overlay
            Box {
                UserAvatar(
                    url = user?.avatarUrl,
                    contentDescription = stringResource(R.string.cd_profile_picture),
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            pickImage.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = stringResource(R.string.profile_change_avatar),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Name + username
            Text(
                text = user?.name.orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = user?.username?.let { "@$it" }.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Premium status chip
            PremiumChip(isPremium = user?.isPremium == true)

            // Follower / following stats
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraLarge),
            ) {
                StatColumn(
                    count = user?.followerCount ?: 0,
                    label = stringResource(R.string.profile_followers),
                )
                StatColumn(
                    count = user?.followingCount ?: 0,
                    label = stringResource(R.string.profile_following),
                )
            }

            // Upgrade / renew
            if (user?.isPremium == true) {
                OutlinedButton(
                    onClick = { showUpgradeDialog = true },
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.screen),
                ) {
                    Text(stringResource(R.string.profile_renew))
                }
            } else {
                Button(
                    onClick = { showUpgradeDialog = true },
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.screen),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.spacing.iconSmall),
                    )
                    Text(
                        text = stringResource(R.string.profile_upgrade),
                        modifier = Modifier.padding(start = MaterialTheme.spacing.small),
                    )
                }
            }

            // Navigation rows
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileRow(
                    icon = Icons.Outlined.FavoriteBorder,
                    label = stringResource(R.string.profile_liked_songs),
                    onClick = onOpenLiked,
                )
                ProfileRow(
                    icon = Icons.Outlined.History,
                    label = stringResource(R.string.profile_recently_played),
                    onClick = onOpenRecent,
                )
                ProfileRow(
                    icon = Icons.Outlined.Person,
                    label = stringResource(R.string.profile_followed_artists),
                    onClick = onOpenArtists,
                )
                ProfileRow(
                    icon = Icons.Outlined.Person,
                    label = stringResource(R.string.profile_people),
                    onClick = onOpenPeople,
                )
                ProfileRow(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    label = stringResource(R.string.chat_title),
                    onClick = onOpenMessages,
                )
                ProfileRow(
                    icon = Icons.Filled.Settings,
                    label = stringResource(R.string.profile_settings),
                    onClick = onOpenSettings,
                )
            }
        }
    }

    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            icon = { Icon(Icons.Filled.Star, contentDescription = null) },
            title = { Text(stringResource(R.string.premium_upgrade_title)) },
            text = { Text(stringResource(R.string.premium_upgrade_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showUpgradeDialog = false
                    viewModel.upgradeToPremium()
                }) {
                    Text(stringResource(R.string.profile_upgrade))
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpgradeDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun PremiumChip(isPremium: Boolean) {
    val (containerColor, contentColor) = if (isPremium) {
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(containerColor)
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        if (isPremium) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(MaterialTheme.spacing.iconSmall),
            )
        }
        Text(
            text = stringResource(
                if (isPremium) R.string.profile_premium_active else R.string.profile_free_account
            ),
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
        )
    }
}

@Composable
private fun StatColumn(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProfileRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.spacing.screen,
                vertical = MaterialTheme.spacing.medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
