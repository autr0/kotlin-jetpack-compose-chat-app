package com.devautro.firebasechatapp.users.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.core.presentation.AutoResizedText
import com.devautro.firebasechatapp.users.presentation.UsersScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UsersScreen(
    vm: UsersScreenViewModel = hiltViewModel(),
    bottomNavPadding: PaddingValues
) {
    val users by vm.usersList.collectAsStateWithLifecycle()

    val outgoingRequests by vm.outgoingRequestsList.collectAsStateWithLifecycle()

    val incomingRequests by vm.incomingRequestsList.collectAsStateWithLifecycle()

    val tabItems = listOf(
        stringResource(id = R.string.Users),
        stringResource(id = R.string.incoming),
        stringResource(id = R.string.outgoing)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.Users),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(4.dp),
                    )

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
        ) {
            var selectedTabIndex by remember {
                mutableIntStateOf(0)
            }
            val pagerState = rememberPagerState {
                tabItems.size
            }
            LaunchedEffect(selectedTabIndex) {
                pagerState.animateScrollToPage(selectedTabIndex)
            }
            LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                if (!pagerState.isScrollInProgress) {
                    selectedTabIndex = pagerState.currentPage
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = Color.White
                            )
                        }
                    }
                ) {
                    tabItems.forEachIndexed { index, text ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = {
                                selectedTabIndex = index
                            },
                            selectedContentColor = Color.White,
                            unselectedContentColor = Color.LightGray
                        ) {
                            Box(
                                modifier = Modifier.padding(
                                    top = 10.dp, bottom = 10.dp
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                AutoResizedText(
                                    text = text
                                )
                            }
                        }
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { index ->
                when (index) {
                    0 -> {
                        UsersMainScreen(
                            allUsersList = users,
                            vm = vm,
                            bottomNavPadding = bottomNavPadding
                        )
                    }

                    1 -> {
                        IncomingRequestsScreen(
                            incomingRequestsList = incomingRequests,
                            openUsers = { selectedTabIndex = 0 },
                            bottomNavPadding = bottomNavPadding
                        )
                    }

                    2 -> {
                        OutgoingRequestsScreen(
                            outgoingRequestScreen = outgoingRequests,
                            openUsers = { selectedTabIndex = 0 },
                            bottomNavPadding = bottomNavPadding
                        )
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = tabItems[index])
                        }
                    }
                }
            }
        }
    }
}