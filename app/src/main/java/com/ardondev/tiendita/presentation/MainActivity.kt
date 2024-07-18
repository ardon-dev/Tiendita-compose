package com.ardondev.tiendita.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ardondev.tiendita.presentation.home.HomeScreen
import com.ardondev.tiendita.presentation.theme.TienditaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TienditaTheme {
                HomeScreen(
                    items = listOf("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
                )
            }
        }

    }

    @Preview
    @Composable
    fun MainPreview() {
        HomeScreen(
            items = listOf("", "", "", "", "", "")
        )
    }

}