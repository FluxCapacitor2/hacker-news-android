package dev.bswanson.hackernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dev.bswanson.hackernews.composable.Navigation
import dev.bswanson.hackernews.composable.StoryList
import dev.bswanson.hackernews.ui.theme.HackerNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseApp = FirebaseApp.initializeApp(
            this,
            FirebaseOptions.Builder().setApplicationId(packageName).build()
        )

        @Suppress("SENSELESS_COMPARISON") // Firebase is lying - initializeApp can return null if the right credentials aren't provided
        if (firebaseApp == null) {
            error("Failed to initialize Firebase app")
        }

        enableEdgeToEdge()

        setContent {
            HackerNewsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(8.dp)
                    ) {
                        Navigation()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HackerNewsTheme {
        StoryList(rememberNavController())
    }
}