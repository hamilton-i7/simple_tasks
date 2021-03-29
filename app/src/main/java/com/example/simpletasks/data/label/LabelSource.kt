package com.example.simpletasks.data.label

import com.example.simpletasks.R

object LabelSource {
    fun readLabels(): List<Label> = listOf(
        Label(R.string.default_color, R.color.default_color),
        Label(R.string.red, R.color.red),
        Label(R.string.blue, R.color.blue),
        Label(R.string.yellow, R.color.gold),
        Label(R.string.green, R.color.green),
        Label(R.string.teal, R.color.teal),
        Label(R.string.purple, R.color.purple),
        Label(R.string.pink, R.color.pink),
        Label(R.string.brown, R.color.brown),
    )
}