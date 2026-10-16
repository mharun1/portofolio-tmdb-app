package com.harun.tmdbapp.ui.screen.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.ui.component.Banner
import com.harun.tmdbapp.ui.component.CastItem
import com.harun.tmdbapp.ui.component.GenreRow
import com.harun.tmdbapp.ui.component.MovieInfoItem
import com.harun.tmdbapp.ui.utils.toDisplayDate
import com.harun.tmdbapp.ui.utils.toDisplayRuntime
import java.util.Locale

@Composable
fun DetailContent(
    modifier: Modifier = Modifier,
    movie: MovieDetail,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Banner(
            backdropUrl = movie.backdropUrl,
            onBack = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Bookmark,
                            contentDescription = "Bookmark"
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = String.format(Locale.US, "%.1f/10 TMDb", movie.rating),
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.height(12.dp))
                if (movie.genres.isNotEmpty()) {
                    GenreRow(genres = movie.genres)
                }
                Spacer(Modifier.height(12.dp))
                Row {
                    MovieInfoItem(label = "Duration", data = movie.runtimeMinutes.toDisplayRuntime(), modifier = Modifier.weight(1f))
                    MovieInfoItem(label = "Language", data = movie.languages.firstOrNull() ?: "-", modifier = Modifier.weight(1f))
                    MovieInfoItem(label = "Release Date", data = movie.releaseDate.toDisplayDate(), modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = movie.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Cast",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                if (movie.cast.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = movie.cast,
                            key = { actor -> actor.id }
                        ) { actor ->
                            CastItem(
                                pictureUrl = actor.picture,
                                castName = actor.name,
                                characterName = actor.character
                            )
                        }
                    }
                }
            }
        }
    }
}