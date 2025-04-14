package dev.bswanson.hackernews.composable

import android.text.format.DateUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.bswanson.hackernews.HNViewModel
import dev.bswanson.hackernews.model.ID
import dev.bswanson.hackernews.model.Submission

@Composable
fun CommentsList(navController: NavController, storyId: ID) {

    val viewModel: HNViewModel = viewModel()

    var loaded by remember { mutableStateOf(false) }
    var submission by remember { mutableStateOf<Submission?>(null) }

    LaunchedEffect(storyId) {
        try {
            submission = viewModel.getSubmission(storyId)
        } catch (exception: Exception) {
            // TODO error state
        }
        loaded = true
    }

    if (!loaded) {
        CircularProgressIndicator()
        return
    }

    LazyColumn {
        item {
            Text(submission!!.text)
        }
        for (commentId in submission?.kids ?: listOf()) {
            item {
                Comment(commentId)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Comment(commentId: ID) {
    val viewModel: HNViewModel = viewModel()
    val ctx = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var collapsed by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }
    var submission by remember { mutableStateOf<Submission?>(null) }

    LaunchedEffect(commentId) {
        try {
            submission = viewModel.getSubmission(commentId)
        } catch (exception: Exception) {
            // TODO error state
        }
        loaded = true
    }

    if (!loaded) {
        CircularProgressIndicator()
        return
    }

    if (submission!!.deleted == true) {
        return
    }

    Column(
        modifier = Modifier

            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onLongClickLabel = "Collapse comment thread",
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        collapsed = !collapsed
                    },
                    onClick = {
                        if (collapsed) collapsed = false
                    },
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, "Author")
                Text(submission?.by ?: "")

                val timeText = DateUtils.getRelativeTimeSpanString(
                    submission!!.time * 1000L,
                    System.currentTimeMillis(),
                    0L
                )

                Text(timeText.toString())
            }
            if (!collapsed) {
                Text(AnnotatedString.fromHtml(submission!!.text, linkInteractionListener = { link ->
                    when (link) {
                        is LinkAnnotation.Clickable -> {
                            // TODO ?
                        }

                        is LinkAnnotation.Url -> {
                            CustomTabsIntent.Builder().build().launchUrl(ctx, link.url.toUri())
                        }
                    }
                }))
            } else {
                Text("(expand)", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (!collapsed) {
            Column(modifier = Modifier.drawBehind {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = 0f, y = size.height),
                    strokeWidth = 1f
                )
            }.padding(start = 16.dp)) {
                for (commentId in submission?.kids ?: listOf()) {
                    Comment(commentId)
                }
            }
        }
    }
}