package com.combyne.mytube.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getTimeStamp(dateTime:String): Long? {
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.getDefault())
       return dateTimeFormatter.parse(dateTime)?.time
    }

    fun getDateTimeString(timestamp: Long):String{
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.getDefault())
        return dateTimeFormatter.format(Date(timestamp))
    }
}