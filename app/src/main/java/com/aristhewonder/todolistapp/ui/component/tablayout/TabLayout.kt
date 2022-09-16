package com.aristhewonder.todolistapp.ui.component.tablayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

@ExperimentalPagerApi
@Composable
fun TabLayout(
    items: List<TabItemModel>,
    pagerState: PagerState,
    onTabSelected: (index: Int) -> Unit,
    tabFooter: TabFooter? = null,
    tabContent: @Composable (pageIndex: Int) -> Unit
) {
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Tabs(
            items = items,
            onTabSelected = onTabSelected,
            tabFooter = tabFooter,
            pagerState = pagerState
        )
        HorizontalPager(state = pagerState, dragEnabled = false) { page ->
            Box(modifier = Modifier.fillMaxSize()) {
                tabContent.invoke(page)
            }
        }
    }
}