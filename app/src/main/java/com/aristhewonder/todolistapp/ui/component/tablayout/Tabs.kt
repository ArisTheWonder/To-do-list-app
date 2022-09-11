package com.aristhewonder.todolistapp.ui.component.tablayout

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun Tabs(
    pagerState: PagerState,
    items: List<TabItemModel>,
    defaultSelectedItemIndex: Int,
    onTabSelected: (index: Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = "scroll_to_index") {
        pagerState.animateScrollToPage(defaultSelectedItemIndex)
    }
    ScrollableTabRow(
        edgePadding = 0.dp,
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = MaterialTheme.colors.primary
            )
        }
    ) {
        items.forEachIndexed { index, item ->
            Tab(
                text = {
                    item.text?.let { text ->
                        Text(text, color = Color.Black)
                    }
                },
                icon = {
                    item.icon?.let { icon ->
                        Icon(painterResource(id = icon), contentDescription = "")
                    }
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                        onTabSelected.invoke(index)
                    }
                }
            )
        }
    }
}