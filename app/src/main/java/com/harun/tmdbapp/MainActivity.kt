package com.harun.tmdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.harun.tmdbapp.navigation.Navigation
import com.harun.tmdbapp.ui.theme.PortofolioTMDBAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PortofolioTMDBAppTheme {
                Navigation()
            }
        }
    }
}