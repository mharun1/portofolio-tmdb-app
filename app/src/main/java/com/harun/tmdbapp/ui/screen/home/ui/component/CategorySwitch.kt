package com.harun.tmdbapp.ui.screen.home.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.ui.theme.PortofolioTMDBAppTheme

@Composable
fun CategorySwitch(
    selectedCategory: MovieCategory,
    onCategorySelected: (MovieCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = MovieCategory.entries
    SingleChoiceSegmentedButtonRow(
        modifier = modifier,
    ) {
        categories.forEachIndexed { index, category ->
            SegmentedButton(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = categories.size),
                label = {
                    Text(
                        text = when (category) {
                            MovieCategory.POPULAR -> "Popular"
                            MovieCategory.TOP_RATED -> "Top Rated"
                        }
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySwitchPreview() {
    PortofolioTMDBAppTheme {
        CategorySwitch(
            selectedCategory = MovieCategory.POPULAR,
            onCategorySelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
