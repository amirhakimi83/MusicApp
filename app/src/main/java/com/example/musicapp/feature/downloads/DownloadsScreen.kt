package com.example.musicapp.feature.downloads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.musicapp.R
import com.example.musicapp.ui.components.ComingSoonScreen

@Composable
fun DownloadsScreen(modifier: Modifier = Modifier) {
    ComingSoonScreen(titleRes = R.string.tab_downloads, modifier = modifier)
}
