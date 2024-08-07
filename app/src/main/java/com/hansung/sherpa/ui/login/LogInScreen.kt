package com.hansung.sherpa.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hansung.sherpa.R

@Composable
fun LoginScreen(navController: NavController = rememberNavController(), modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val applicationValueImage = painterResource(R.drawable.application_value)
            Image( applicationValueImage, "쉽고, 빠르게 찾아보세요 보다 안전하게 안내해드립니다")
            val splashScreenImage = painterResource(R.drawable.splash_screen)
            Image( splashScreenImage, "대표 이미지")

            Column {
                TextButton(
                    onClick = {},
                    colors= ButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Black,
                        disabledContentColor = Color.White,
                        disabledContainerColor =  Color.Black
                    ),
                    modifier = Modifier.width(200.dp)
                ){
                    Text(
                        text = "로그인 하기",
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = {},
                    colors= ButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.White,
                        disabledContentColor = Color.Black,
                        disabledContainerColor =  Color.White
                    ),
                    border = BorderStroke(2.dp, Color.Black),
                    modifier = Modifier.width(200.dp)
                ){
                    Text(
                        text = "로그인 하기",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun LogInPreview() {
    LoginScreen()
}