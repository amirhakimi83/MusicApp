package com.example.musicapp.feature.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.components.NetworkImage
import com.example.musicapp.ui.theme.AppGradients
import com.example.musicapp.ui.theme.spacing
import kotlinx.coroutines.delay

/**
 * Auto-advancing hero carousel at the top of Home (daily picks). Big cover art
 * with a gradient scrim and a page indicator.
 */
@Composable
fun HomeCarousel(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (songs.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { songs.size })

    // Auto-advance every 3.5s.
    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(3500)
            val next = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(next)
        }
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.screen),
            pageSpacing = MaterialTheme.spacing.small,
        ) { page ->
            val song = songs[page]
            val scale by animateFloatAsState(
                targetValue = if (page == pagerState.currentPage) 1f else 0.92f,
                label = "carouselScale",
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(vertical = MaterialTheme.spacing.small)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onSongClick(song) },
            ) {
                NetworkImage(
                    url = song.coverImageUrl,
                    contentDescription = song.title,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppGradients.coverScrim(Color.Black)),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = song.artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.small),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(pagerState.pageCount) { index ->
                val selected = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (selected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant,
                        ),
                )
            }
        }
    }
}
