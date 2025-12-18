package com.example.hmong_translate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hmong_translate.ui.TranslateScreen
import com.example.hmong_translate.ui.theme.Hmong_TranslateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hmong_TranslateTheme {
                TranslateScreen()
            }
        }
    }
}
