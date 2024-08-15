package com.devautro.firebasechatapp.chatScreen.data.model

import androidx.compose.foundation.lazy.LazyListState

data class ScrollContext(
    val isTop: Boolean,
    val isBottom: Boolean
)

val LazyListState.isLastItemVisible: Boolean
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

val LazyListState.isFirstItemVisible: Boolean
    get() = firstVisibleItemIndex == 0