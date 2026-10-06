package com.harun.tmdbapp.ui.component

import android.R.attr.contentDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.harun.tmdbapp.R
import com.harun.tmdbapp.ui.theme.PortofolioTMDBAppTheme

private val genres = listOf("Horror", "Mystery", "Thriller")

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    poster: String?,
    title: String,
    rating: Double,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        SubcomposeAsyncImage(
            model = poster ?: Icons.Default.BrokenImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = { LoadingScreen() },
            modifier = Modifier
                .width(80.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Star icon",
                    tint = Color(0xFFFFC107)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = "$rating/10 IMDb",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            /*Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Spacer(Modifier.width(4.dp))
                }
            }*/
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    PortofolioTMDBAppTheme {
        Row(modifier = Modifier.fillMaxWidth()) {
            SubcomposeAsyncImage(
                model = painterResource(R.drawable.img),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                loading = { LoadingScreen() },
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Spiderman: Brand New Day",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Star icon",
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "6.4/10 IMDb",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}