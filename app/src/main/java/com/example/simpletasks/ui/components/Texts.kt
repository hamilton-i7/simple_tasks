package com.example.simpletasks.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simpletasks.R

@Composable
fun AppNameText(modifier: Modifier = Modifier, color: Color = MaterialTheme.colors.onSurface) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h4.copy(
                textIndent = TextIndent(restLine = 20.sp)
            ),
            color = color,
            maxLines = 2,
            modifier = Modifier.width(100.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_app_logo_splash_screen),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }
}

@Composable
fun CreatorNameText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.hamilton),
        style = MaterialTheme.typography.h4,
        color = MaterialTheme.colors.primary.copy(alpha = 0.65f),
        modifier = modifier
            .padding(bottom = dimensionResource(id = R.dimen.space_between_70))
    )
}

@Composable
fun CreatorByNameText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.by_hamilton),
        style = MaterialTheme.typography.subtitle1,
        color = Color.Black.copy(alpha = 0.6f),
        modifier = modifier
    )
}

@Composable
fun ErrorText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.error
    )
}