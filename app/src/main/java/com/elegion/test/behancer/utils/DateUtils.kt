package com.elegion.test.behancer.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun format(time: Long): String {
        val date = Date(time * 1000L)
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }
}