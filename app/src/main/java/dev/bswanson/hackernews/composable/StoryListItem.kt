package dev.bswanson.hackernews.composable

import android.text.format.DateUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.bswanson.hackernews.HNViewModel
import dev.bswanson.hackernews.model.ID
import dev.bswanson.hackernews.model.Submission

@Composable
fun StoryListItem(navController: NavController, id: ID) {
    val viewModel: HNViewModel = viewModel()
    var loaded by remember { mutableStateOf(false) }
    var submission by remember { mutableStateOf<Submission?>(null) }
    val ctx = LocalContext.current

    val onClick: () -> Unit = {
        if (submission != null) {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(ctx, submission!!.url!!.toUri())
        }
    }

    LaunchedEffect(id) {
        try {
            submission = viewModel.getSubmission(id)
        } catch (exception: Exception) {
            // TODO error
        }
        loaded = true
    }

    if (submission == null) {
        if (!loaded) {
            Box(modifier = Modifier.height(48.dp))
        }
        return
    }

    if (submission!!.deleted == true || submission!!.dead == true) {
        return
    }

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(submission!!.title, style = MaterialTheme.typography.titleLarge)
        if (submission?.url != null) {
            val uri = submission!!.url!!.toUri()
            if (uri.host != null) {
                Text(uri.host ?: "", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left side
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.ThumbUp, "Upvotes", modifier = Modifier.size(16.dp))
                    Text(
                        (submission?.score ?: 0).toString(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val timeText = DateUtils.getRelativeTimeSpanString(
                        submission!!.time * 1000L,
                        System.currentTimeMillis(),
                        0L
                    )

                    Icon(Icons.Filled.DateRange, "Published", modifier = Modifier.size(16.dp))
                    Text(timeText.toString(), style = MaterialTheme.typography.labelMedium)
                }
            }

            // Right side
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        Color.LightGray.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        navController.navigate(Story(submission!!.id))
                    }
                    .padding(6.dp, 4.dp)

            ) {
                Icon(Icons.Filled.Email, "Comments", modifier = Modifier.size(16.dp))
                Text(
                    (submission?.descendants ?: 0).toString(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }

    HorizontalDivider()
}