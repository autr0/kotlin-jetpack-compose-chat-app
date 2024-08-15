package com.devautro.firebasechatapp.chatScreen.presentation.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.devautro.firebasechatapp.chatScreen.data.model.ScrollContext
import com.devautro.firebasechatapp.chatScreen.data.model.isFirstItemVisible
import com.devautro.firebasechatapp.chatScreen.data.model.isLastItemVisible

@Composable
fun rememberScrollContext(listState: LazyListState): ScrollContext {
    val scrollContext by remember {
        derivedStateOf {
            ScrollContext(
                isTop = listState.isFirstItemVisible,
                isBottom = listState.isLastItemVisible
            )
        }
    }
    return scrollContext
}