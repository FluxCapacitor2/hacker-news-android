package dev.bswanson.hackernews.composable

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.bswanson.hackernews.model.ID
import kotlinx.serialization.Serializable

@Serializable
object TopStories

@Serializable
data class Story(val id: ID)

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = TopStories) {
        composable<TopStories> {
            StoryList(navController)
        }
        composable<Story> { args ->
            val storyId = args.toRoute<Story>().id
            CommentsList(navController, storyId)
        }
    }

}
