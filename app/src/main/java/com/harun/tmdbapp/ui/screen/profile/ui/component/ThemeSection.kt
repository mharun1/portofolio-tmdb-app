package com.harun.tmdbapp.ui.screen.profile.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harun.tmdbapp.R
import com.harun.tmdbapp.model.ThemeMode

@Composable
fun ThemeSection(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = R.string.profile_appearance),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(8.dp))
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.profile_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(12.dp))
            ThemeMode.entries.forEach { mode ->
                val modeLabel = when (mode) {
                    ThemeMode.SYSTEM -> stringResource(id = R.string.profile_theme_system)
                    ThemeMode.LIGHT -> stringResource(id = R.string.profile_theme_light)
                    ThemeMode.DARK -> stringResource(id = R.string.profile_theme_dark)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = currentTheme == mode,
                        onClick = { onThemeSelected(mode) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = modeLabel, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
