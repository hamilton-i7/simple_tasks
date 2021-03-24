package com.example.simpletasks.data.label

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

data class Label(
    @StringRes  val name: Int,
    @ColorRes val color: Int,
)