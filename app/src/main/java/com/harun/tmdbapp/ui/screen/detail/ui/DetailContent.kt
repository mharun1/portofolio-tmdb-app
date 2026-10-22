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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harun.tmdbapp.model.CastMember
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.ui.component.Banner
import com.harun.tmdbapp.ui.component.CastItem
import com.harun.tmdbapp.ui.component.GenreRow
import com.harun.tmdbapp.ui.component.MovieInfoItem
import com.harun.tmdbapp.ui.theme.PortofolioTMDBAppTheme
import com.harun.tmdbapp.ui.theme.StarYellow
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
            .verticalScroll(rememberScrollState())
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
                    .padding(top = 24.dp, bottom = 48.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = StarYellow
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = String.format(Locale.US, "%.1f/10 TMDb", movie.rating),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = movie.summary.ifBlank { "No summary available." },
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (movie.cast.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Cast",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = movie.cast,
                            key = { actor -> actor.creditId }
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

@Preview(showBackground = true)
@Composable
private fun DetailContentFullPreview() {
    PortofolioTMDBAppTheme {
        DetailContent(
            movie = MovieDetail(
                id = 1,
                backdropUrl = "https://image.tmdb.org/t/p/w780/path.jpg",
                title = "Spider-Man: No Way Home",
                summary = "With Spider-Man's identity now revealed, Peter asks Doctor Strange for help. When a spell goes wrong, dangerous foes from other worlds start to appear, forcing Peter to discover what it truly means to be Spider-Man.",
                genres = listOf("Action", "Adventure", "Fantasy"),
                runtimeMinutes = 148,
                rating = 9.1,
                releaseDate = "2021-12-15",
                languages = listOf("English"),
                cast = listOf(
                    CastMember(id = 1, name = "Tom Holland", character = "Peter Parker / Spider-Man", picture = null, creditId = "129823589q2"),
                    CastMember(id = 2, name = "Zendaya", character = "MJ", picture = null, creditId = "0159q305903"),
                    CastMember(id = 3, name = "Benedict Cumberbatch", character = "Doctor Strange", picture = null, creditId = "0234960234tr"),
                    CastMember(id = 4, name = "Jacob Batalon", character = "Ned Leeds", picture = null, creditId = "9304623948d"),
                )
            ),
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailContentMissingDataPreview() {
    PortofolioTMDBAppTheme {
        DetailContent(
            movie = MovieDetail(
                id = 2,
                backdropUrl = null,
                title = "The Incredibly Long Movie Title That Spans Across Multiple Lines to Test Text Wrapping and Ensure That Long Titles Do Not Clip or Overflow Horizontally",
                summary = "This is an extremely long movie overview designed to verify that the vertical scrolling and text wrapping handle extensive text without issues. ".repeat(6),
                genres = emptyList(),
                runtimeMinutes = null,
                rating = 0.0,
                releaseDate = "",
                languages = emptyList(),
                cast = emptyList()
            ),
            onBack = {}
        )
    }
}
