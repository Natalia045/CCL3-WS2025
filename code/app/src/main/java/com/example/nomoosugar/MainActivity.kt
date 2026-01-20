package com.example.nomoosugar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nomoosugar.navigation.NoMooSugarNavigation
import com.example.nomoosugar.notifications.NotificationHelper
import com.example.nomoosugar.ui.theme.NoMooSugarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        setContent {
            NoMooSugarTheme {
                NoMooSugarNavigation()
            }
        }
    }
}