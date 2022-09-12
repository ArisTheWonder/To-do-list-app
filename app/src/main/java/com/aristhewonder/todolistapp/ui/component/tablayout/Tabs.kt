package com.aristhewonder.todolistapp.ui.component.tablayout

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
    tabFooter: TabFooter? = null,
    onTabSelected: (index: Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = "scroll_to_index") {
        pagerState.animateScrollToPage(defaultSelectedItemIndex)
    }
    ScrollableTabRow(
        edgePadding = 8.dp,
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
            TabItem(
                text = item.text,
                icon = item.icon,
                selected = pagerState.currentPage == index
            ) {
                scope.launch {
                    onTabSelected.invoke(index)
                    pagerState.animateScrollToPage(index)
                }
            }
        }
        tabFooter?.let { footer ->
            TabItem(text = footer.text, icon = footer.icon, selected = false) {
                footer.onClick.invoke()
            }
        }
    }
}

@Composable
private fun TabItem(
    text: String?,
    icon: Int?,
    selected: Boolean,
    onClick: () -> Unit
) {
    LeadingIconTab(
        modifier = Modifier.height(56.dp),
        text = {
            text?.let {
                Text(text = it, color = Color.Black)
            }
        },
        icon = {
            icon?.let { icon ->
                Icon(
                    painterResource(id = icon),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        selected = selected,
        onClick = onClick
    )
}