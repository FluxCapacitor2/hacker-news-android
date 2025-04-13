package dev.bswanson.hackernews

import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dev.bswanson.hackernews.model.ID
import dev.bswanson.hackernews.model.Submission
import dev.bswanson.hackernews.ui.theme.HackerNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val firebaseApp = FirebaseApp.initializeApp(
            this,
            FirebaseOptions.Builder().setApplicationId(packageName).build()
        )
        @Suppress("SENSELESS_COMPARISON") // Firebase is lying - initializeApp can return null if the right credentials aren't provided
        if (firebaseApp == null) {
            error("Failed to initialize Firebase app")
        }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            HackerNewsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StoryList(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun StoryList(modifier: Modifier = Modifier) {

    val viewModel: HNViewModel = viewModel()
    val stories = viewModel.topStories.observeAsState()

    LazyColumn(modifier = modifier) {
        item {
            Text("Hacker News", style = MaterialTheme.typography.titleLarge)
        }
        for (story in (stories.value ?: listOf())) {
            item(key = story) {
                StoryListItem(story)
            }
        }
    }
}

@Composable
fun StoryListItem(id: ID) {
    val viewModel: HNViewModel = viewModel()
    var submission by remember { mutableStateOf<Submission?>(null) }

    val onClick: () -> Unit = {} // TODO

    LaunchedEffect(id) {
        submission = viewModel.getStory(id)
    }

    if (submission == null) {
        Box(modifier = Modifier.height(24.dp))
    } else {
        Column(modifier = Modifier.padding(vertical = 8.dp).clickable(onClick = onClick)) {
            Text(submission!!.title, style = MaterialTheme.typography.titleMedium)
            val timeText = DateUtils.getRelativeTimeSpanString(
                submission!!.time * 1000L,
                System.currentTimeMillis(),
                0L
            )
            val subtext =
                "${submission?.score ?: 0} points by ${submission?.by ?: "(unknown)"} $timeText ${submission?.descendants ?: 0} comments"
            Text(subtext, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HackerNewsTheme {
        StoryList()
    }
}