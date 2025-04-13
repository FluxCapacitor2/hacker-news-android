package dev.bswanson.hackernews.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bswanson.hackernews.HNViewModel
import dev.bswanson.hackernews.model.ID

@Composable
fun StoryList(modifier: Modifier = Modifier) {
    val viewModel: HNViewModel = viewModel()
    val stories = viewModel.topStories.observeAsState()

    LazyColumn(modifier = modifier) {
        item {
            Text("Hacker News", style = MaterialTheme.typography.headlineLarge)
        }
        for ((i, storyId) in (stories.value?.withIndex() ?: listOf<ID>().withIndex())) {
            item(key = storyId) {
                StoryListItem(storyId)
                if (i != stories.value?.size) {
                    HorizontalDivider()
                }
            }
        }
    }
}