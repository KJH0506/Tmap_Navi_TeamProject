package com.hansung.sherpa.ui.preference.caregiver

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansung.sherpa.R
import com.hansung.sherpa.ui.preference.caregiver.carousel.MultiBrowseCarousel
import com.hansung.sherpa.ui.preference.caregiver.carousel.rememberCarouselState

data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val contentDescriptionResId: Int,
    val name: String = "",
    val email: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverSyncScreen() {
    var searchQuery by remember { mutableStateOf("") }

    // sample data 값
    val items =
        listOf(
            CarouselItem(0, R.drawable._1, R.string.float_icon_1, "홍길동", "abcd1234@gmail.com"),
            CarouselItem(1, R.drawable._2, R.string.float_icon_2, "홍김동", "abcd12345@gmail.com"),
            CarouselItem(2, R.drawable._3, R.string.float_icon_3, "홍길돌", "abcd1233@gmail.com"),
            CarouselItem(3, R.drawable._4, R.string.float_icon_4, "홀길돌", "abcd1222@gmail.com"),
            CarouselItem(4, R.drawable._5, R.string.float_icon_5, "홈길동", "aaad1234@gmail.com"),
            CarouselItem(5, R.drawable._6, R.string.float_icon_5, "홍길동동", "accbd1234@gmail.com"),
            CarouselItem(6, R.drawable._7, R.string.float_icon_5, "홍동길", "abcd14@gmail.com"),
            CarouselItem(7, R.drawable._8, R.string.float_icon_5, "길동홍", "abdd1212@gmail.com"),
        )

    // 클릭한 항목의 상태를 저장하기 위한 상태 변수
    var selectedItem by remember { mutableStateOf<CarouselItem?>(null) }

    // 클릭한 항목이 있을 때 모달을 표시
    if (selectedItem != null) {
        ItemDetailDialog(item = selectedItem!!, onDismiss = { selectedItem = null })
    }

    // 검색 필터
    val filteredItems = items.filter { item ->
        item.name.contains(searchQuery, ignoreCase = true) ||
                item.email.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = Color(0xFF6200EE),
                onBackground = Color.Black,
                onSurfaceVariant = Color.Gray
            )
        ) {
            Surface(shape = RoundedCornerShape(20.dp)) {
                SearchBar(
                    onSearch = {
                        query -> println("Search query: $query")
                        searchQuery = query
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MultiBrowseCarousel(
            state = rememberCarouselState { items.count() },
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight(),
            itemSpacing = 8.dp,
            preferredItemWidth = 250.dp,
            orientation = Orientation.Vertical,
            contentPadding = PaddingValues(vertical = 6.dp),
        ) { i ->
            if (i < filteredItems.size) {
                val item = filteredItems[i]
                    Box(
                        modifier = Modifier
                            .width(250.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable{ selectedItem = item }
                    ) {
                        Image(
                            painter = painterResource(id = item.imageResId),
                            contentDescription = stringResource(item.contentDescriptionResId),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = item.name,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = item.email,
                                style = TextStyle(
                                    color = Color.White
                                ),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                }
            }
        }
    }
}

@Composable
fun ItemDetailDialog(item: CarouselItem, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("보호자 연동", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        text = {
            Box(
                modifier = Modifier
                    .width(350.dp).height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = stringResource(item.contentDescriptionResId),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter) // Align content at the bottom center of the image
                        .padding(16.dp)
                ) {
                    Text(
                        text = item.name,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = item.email,
                        style = TextStyle(
                            color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("요청하기")
            }
        }
    )
}

@SuppressLint("RememberReturnType")
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    placeholderText: String = "이름이나 이메일을 입력하세요.",
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) Color(0xFFEFEFEF) else MaterialTheme.colorScheme.background,
        label = ""
    )

    TextField(
        value = searchQuery,
        onValueChange = { newValue ->
            searchQuery = newValue
            onSearch(newValue.text)
        },
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp),
        interactionSource = interactionSource,
        placeholder = {
            Text(text = placeholderText)
        },
        leadingIcon = {
            AnimatedVisibility(visible = searchQuery.text.isEmpty(),
                enter = slideInHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth ->
                    -fullWidth / 3
                } + fadeIn(
                    animationSpec = tween(durationMillis = 200)
                ),
                exit = slideOutHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth ->
                    -fullWidth / 3
                } + fadeOut(
                    animationSpec = tween(durationMillis = 200)
                )) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        },
        trailingIcon = {
            AnimatedVisibility(visible = searchQuery.text.isNotEmpty()) {
                IconButton(onClick = { searchQuery = TextFieldValue("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Icon"
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        ),
        singleLine = true
    )
}

@Preview
@Composable
fun PreviewAccountSyncScreen() {
    CaregiverSyncScreen()
}

@Preview
@Composable
fun SearchBarPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE),
            onBackground = Color.Black,
            onSurfaceVariant = Color.Gray
        )
    ) {
        Surface {
            SearchBar(
                onSearch = { query -> println("Search query: $query") }
            )
        }
    }
}