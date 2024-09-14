package com.hansung.sherpa.ui.preference.caregiver

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.ButtonColors
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.hansung.sherpa.ui.common.SherpaDialog
import com.hansung.sherpa.ui.common.SherpaDialogParm
import com.hansung.sherpa.ui.preference.TopAppBarScreen
import com.hansung.sherpa.ui.preference.caregiver.carousel.MultiBrowseCarousel
import com.hansung.sherpa.ui.preference.caregiver.carousel.rememberCarouselState
import com.hansung.sherpa.ui.theme.lightScheme
import com.hansung.sherpa.user.UserManager
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val contentDescriptionResId: Int,
    val name: String = "",
    val email: String = "",
)

class CaregiverSyncActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this);
        setContent {
            MaterialTheme(colorScheme = lightScheme) {
                TopAppBarScreen( title = "보호자 연동",
                    { finish() }, { CaregiverSyncScreen() }
                )
            }
        }
    }
}

private enum class CodeStatus(val code: Int, val message: String) {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    companion object {
        fun findByCode(code: Int): CodeStatus? {
            return entries.find { it.code == code }
        }
    }
}

/**
 * 보호자 연동 요청 화면
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverSyncScreen() {
    var searchQuery by remember { mutableStateOf("") }

    val carouselItems = remember { mutableStateListOf<CarouselItem>() }
    val sampleImageList = listOf<Int>(
        R.drawable._1,
        R.drawable._2,
        R.drawable._3,
        R.drawable._4,
        R.drawable._5,
        R.drawable._6,
        R.drawable._7,
        R.drawable._8,
    )
    LaunchedEffect(Unit) {
        val apiList = withContext(Dispatchers.IO) {
            val items = UserManager().getCaregiverUsersList()
            val code = items.code ?: -1
            if (CodeStatus.findByCode(code) == CodeStatus.OK) {
                return@withContext items.data?.mapIndexed { index, user1 ->
                    CarouselItem(
                        id = user1.userId ?: -1,
                        imageResId = sampleImageList[index % sampleImageList.size],
                        contentDescriptionResId = R.string.app_name,
                        name = user1.name ?: "이름 없음",
                        email = user1.userAccount?.email ?: "이메일 없음"
                    )
                } ?: emptyList()
            } else {
                return@withContext emptyList()
            }
        }
        carouselItems.addAll(apiList)
    }

    // 클릭한 항목의 상태를 저장하기 위한 상태 변수
    var selectedItem by remember { mutableStateOf<CarouselItem?>(null) }

    // 클릭한 항목이 있을 때 모달을 표시
    if (selectedItem != null) {
        ItemDetailDialog(item = selectedItem!!, onDismiss = { selectedItem = null })
    }

    // 검색 필터
    val filteredItems = carouselItems.filter { item ->
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
                primary = MaterialTheme.colorScheme.primary,
                onBackground = Color.Black,
                onSurfaceVariant = Color.Gray
            )
        ) {
            // 검색창
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
            state = rememberCarouselState { carouselItems.count() },
            modifier = Modifier
                .width(350.dp)
                .fillMaxHeight(),
            itemSpacing = 8.dp,
            preferredItemWidth = 240.dp,
            orientation = Orientation.Vertical,
            contentPadding = PaddingValues(vertical = 6.dp),
        ) { i ->
            if (i < filteredItems.size) {
                val item = filteredItems[i]
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedItem = item }
                ) {
                    // 배경 이미지(프로필 사진)
                    Image(
                        painter = painterResource(id = item.imageResId),
                        contentDescription = stringResource(item.contentDescriptionResId),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )

                    // 이름, 이메일
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
    val sherpaDialog = remember { mutableStateOf(SherpaDialogParm())}
    val showDialog = remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("보호자 연동", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        text = {
            Box(
                modifier = Modifier
                    .width(350.dp)
                    .height(200.dp)
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
            Button(onClick = {
                requestCareGiver(item.email, sherpaDialog, showDialog)
                onDismiss()
            }
                , colors = ButtonColors(
                    contentColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.scrim,
                    disabledContentColor = MaterialTheme.colorScheme.scrim,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("요청하기")
            }
        }
    )
    if(showDialog.value) {
        SherpaDialog(
            title = sherpaDialog.value.title,
            message = sherpaDialog.value.message,
            confirmButtonText = sherpaDialog.value.confirmButtonText,
            dismissButtonText = sherpaDialog.value.dismissButtonText,
            onConfirmation = sherpaDialog.value.onConfirmation,
            onDismissRequest = sherpaDialog.value.onDismissRequest
        )
    }
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

fun requestCareGiver(caregiverEmail: String, sherpaDialog: MutableState<SherpaDialogParm>, showDialog: MutableState<Boolean>) {
    // TODO: ※ (2024-08-12) 지금은 테스트용으로 보호자 승인 없이 DB에서 바로 받아옴. 진행
    val caregiverUserResponse = UserManager().linkPermission(caregiverEmail)

    if (caregiverUserResponse.code == 200) {
        sherpaDialog.value.setParm(
            title = "연동 성공",
            message =listOf("보호자 연동이 완료되었습니다."),
            confirmButtonText = "확인",
            onConfirmation = { showDialog.value = false },
            onDismissRequest = { showDialog.value = false }
        )
        showDialog.value = true
    }
    else if(caregiverUserResponse.code == 201){
        sherpaDialog.value.setParm(
            title = "연동 실패",
            message =listOf("일치하는 보호자 아이디가 없습니다."),
            confirmButtonText = "확인",
            onConfirmation = { showDialog.value = false },
            onDismissRequest = { showDialog.value = false }
        )
        showDialog.value = true
    }
    else if(caregiverUserResponse.code == 404) {
        sherpaDialog.value.setParm(
            title = "전송 실패",
            message =listOf("다시 한번 전송해주세요."),
            confirmButtonText = "확인",
            onConfirmation = { showDialog.value = false },
            onDismissRequest = { showDialog.value = false }
        )
        showDialog.value = true
    }
    else if(caregiverUserResponse.code == 421) {
        sherpaDialog.value.setParm(
            title = "연동 실패",
            message =listOf(caregiverUserResponse.message?:"None"),
            confirmButtonText = "확인",
            onConfirmation = { showDialog.value = false },
            onDismissRequest = { showDialog.value = false }
        )
        showDialog.value = true
    }
}