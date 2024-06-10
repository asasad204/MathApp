package com.example.projekat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.projekat.ui.theme.ProjekatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjekatTheme {
                // A surface container using the 'background' color from the theme
                    MainScreen()

            }
        }
    }
}

