package com.tecsup.lab10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tecsup.lab10.ui.theme.Lab10Theme
import com.tecsup.lab10.view.SeriesApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab10Theme {
                SeriesApp()
            }
        }
    }
}