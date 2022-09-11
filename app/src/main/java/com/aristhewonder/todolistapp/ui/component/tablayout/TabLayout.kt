package com.aristhewonder.todolistapp.ui.component.tablayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalUnitApi::class)
@ExperimentalPagerApi
@Composable
fun TabLayout(
    items: List<TabItemModel>,
    defaultSelectedItemIndex: Int = 0,
    onTabSelected: (index: Int) -> Unit,
    tabContent: @Composable (pageIndex: Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = items.size)
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Tabs(
            pagerState = pagerState,
            defaultSelectedItemIndex = defaultSelectedItemIndex,
            items = items,
            onTabSelected = onTabSelected
        )
        HorizontalPager(state = pagerState) { page ->
            tabContent.invoke(page)
        }
    }
}