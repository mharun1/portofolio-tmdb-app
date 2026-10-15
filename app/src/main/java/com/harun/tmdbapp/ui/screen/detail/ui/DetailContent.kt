package com.harun.tmdbapp.ui.screen.detail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.harun.tmdbapp.ui.component.Banner
import com.harun.tmdbapp.ui.component.MovieInfoItem
import com.harun.tmdbapp.ui.utils.toDisplayDate
import com.harun.tmdbapp.ui.utils.toDisplayRuntime

private val genres = listOf(
    "Action", "Adventure", "Fantasy", "Action", "Adventure", "Fantasy"
)

@Composable
fun DetailContent(
    modifier: Modifier = Modifier,
    backdropUrl: String?,
    title: String,
    rating: Double,
    runtime: Int?,
    language: List<String>,
    releaseDate: String,
    summary: String,
    onBack: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Banner(
            backdropUrl = backdropUrl,
            onBack = onBack,
            modifier = Modifier.fillMaxWidth()
        )
        Surface(
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
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
                        text = "$rating/10 TMDb",
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.height(12.dp))
                if (genres.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        genres.forEach { genre ->
                            Surface(
                                shape = RoundedCornerShape(100),
                                color = Color(0xffdce3ff),
                                contentColor = Color(0xff87a4e8),
                            ) {
                                Text(
                                    text = genre,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row {
                    MovieInfoItem(label = "Duration", data = runtime.toDisplayRuntime(), modifier = Modifier.weight(1f))
                    MovieInfoItem(label = "Language", data = language.firstOrNull() ?: "-", modifier = Modifier.weight(1f))
                    MovieInfoItem(label = "Release Date", data = releaseDate.toDisplayDate(), modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = summary,
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
            }
        }
    }
}