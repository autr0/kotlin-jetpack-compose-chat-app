package com.devautro.firebasechatapp.core.data

import com.devautro.firebasechatapp.core.data.model.DateTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun dateTimeToLocalTimeZone(dateTimeString: String): DateTime {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val localDateTime = LocalDateTime.parse(dateTimeString, formatter)

    val zoneId = ZoneId.from(formatter.parse(dateTimeString))

    val deviceZoneId = ZoneId.systemDefault()

    return if (zoneId != deviceZoneId) {
        val adjustedDateTime = localDateTime.atZone(zoneId).withZoneSameInstant(deviceZoneId).toLocalDateTime()

        val dateFormatter = DateTimeFormatter.ofPattern("dd LLL yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        DateTime(
            date = adjustedDateTime.format(dateFormatter),
            time = adjustedDateTime.format(timeFormatter)
        )
    } else {
        DateTime(
            date = localDateTime.format(DateTimeFormatter.ofPattern("dd LLL yyyy")),
            time = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        )
    }
}

fun timeDateToLocalTimeZone(dateTimeString: String): LocalDateTime {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val localDateTime = LocalDateTime.parse(dateTimeString, formatter)

    val zoneId = ZoneId.from(formatter.parse(dateTimeString))

    val deviceZoneId = ZoneId.systemDefault()

    return if (zoneId != deviceZoneId) {
        localDateTime.atZone(zoneId).withZoneSameInstant(deviceZoneId).toLocalDateTime()

    } else {
        localDateTime
    }
}