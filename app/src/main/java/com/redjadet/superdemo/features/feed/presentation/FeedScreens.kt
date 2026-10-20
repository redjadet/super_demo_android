package com.redjadet.superdemo.features.feed.presentation

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.redjadet.superdemo.app.ui.ReviewerContentWidth
import com.redjadet.superdemo.features.feed.domain.FeedDisplayError
import com.redjadet.superdemo.features.feed.domain.FeedPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedRoute(
    pendingPostId: Int?,
    onPendingConsumed: () -> Unit,
    onOpenPost: (Int) -> Unit,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(pendingPostId, state.posts, state.isLoading) {
        val id = pendingPostId ?: return@LaunchedEffect
        // Wait for a terminal Feed load (seed/refresh finished) before resolving.
        if (state.isLoading) return@LaunchedEffect
        // Open detail whether the id exists or not; detail shows "Post not found."
        onOpenPost(id)
        onPendingConsumed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feed") },
                actions = {
                    IconButton(
                        onClick = viewModel::refresh,
                        modifier = Modifier.testTag("feedRefresh"),
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh feed")
                    }
                },
            )
        },
    ) { padding ->
        ReviewerContentWidth(modifier = Modifier.padding(padding)) {
            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    state.isLoading && state.posts.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.testTag("feedLoading"))
                        }
                    }
                    state.error != null && state.posts.isEmpty() -> {
                        FeedError(
                            error = state.error!!,
                            onRetry = viewModel::refresh,
                        )
                    }
                    state.posts.isEmpty() -> {
                        FeedEmpty(onRetry = viewModel::refresh)
                    }
                    else -> {
                        Column(Modifier.fillMaxSize()) {
                            if (state.isStale || state.showingLocalOnly) {
                                val bannerText = if (state.isStale) {
                                    "Couldn’t refresh. Showing saved posts."
                                } else {
                                    "Showing posts saved on this device."
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("feedStaleBanner"),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = bannerText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f),
                                        )
                                        TextButton(
                                            onClick = viewModel::refresh,
                                            modifier = Modifier.testTag("feedStaleRetry"),
                                        ) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.testTag("feedList"),
                            ) {
                                items(state.posts, key = { it.id }) { post ->
                                    FeedRow(
                                        post = post,
                                        onOpen = { onOpenPost(post.id) },
                                        onToggleBookmark = { viewModel.toggleBookmark(post.id) },
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedRow(
    post: FeedPost,
    onOpen: () -> Unit,
    onToggleBookmark: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(vertical = 12.dp)
            .testTag("feedPost_${post.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        IconButton(onClick = onToggleBookmark) {
            Icon(
                imageVector = if (post.bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                contentDescription = if (post.bookmarked) "Remove bookmark: ${post.title}" else "Bookmark: ${post.title}",
            )
        }
    }
}

@Composable
private fun FeedEmpty(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("feedEmpty"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("No saved posts yet.", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Pull to refresh or tap Retry to load posts.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        TextButton(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 12.dp)
                .testTag("feedEmptyRetry"),
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun FeedError(
    error: FeedDisplayError,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("feedError"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = when (error) {
                FeedDisplayError.Network -> "Could not reach the network."
                FeedDisplayError.Empty -> "No posts yet."
                is FeedDisplayError.Unknown -> error.message
            },
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = when (error) {
                FeedDisplayError.Network ->
                    "Check your connection and try again. Posts will be available offline after they are saved."
                else -> "Try again when you are online."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        TextButton(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 12.dp)
                .testTag("feedRetry"),
        ) {
            Text("Retry")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPostDetailRoute(
    postId: Int,
    onBack: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val post = state.posts.firstOrNull { it.id == postId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        ReviewerContentWidth(modifier = Modifier.padding(padding)) {
            if (post == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("feedPostMissing"),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Post not found.")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                        .testTag("feedPostDetail"),
                ) {
                    Text(post.title, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = "User ${post.userId}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    )
                    Text(post.body, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
