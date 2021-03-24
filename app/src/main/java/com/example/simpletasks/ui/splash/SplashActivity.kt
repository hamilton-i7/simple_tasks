package com.example.simpletasks.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpletasks.MainActivity
import com.example.simpletasks.R
import com.example.simpletasks.ui.components.AppNameText
import com.example.simpletasks.ui.components.CreatorNameText
import com.example.simpletasks.ui.theme.SimpleTasksTheme

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleTasksTheme {
                SplashScreen()
            }
        }

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}


@Composable
private fun SplashScreen() {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        SideColumn()
        CenterColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colors.surface)
        )
        SideColumn()
    }
}

@Composable
private fun SideColumn(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .width(dimensionResource(id = R.dimen.splash_column_width))
            .fillMaxHeight()
            .background(MaterialTheme.colors.primary)
    )
}

@Composable
private fun CenterColumn(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AppNameText(color = MaterialTheme.colors.onSurface)
        }
        CreatorNameText()
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SimpleTasksTheme {
        SplashScreen()
    }
}