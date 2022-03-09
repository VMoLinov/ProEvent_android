package ru.myproevent.domain.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeInterval(
    /** the milliseconds since January 1, 1970, 00:00:00 GMT **/
    var start: Long,
    /** the milliseconds since January 1, 1970, 00:00:00 GMT **/
    var end: Long
) : Parcelable