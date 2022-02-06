package ru.myproevent.domain.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeInterval(
    var start: Long,
    var end: Long
) : Parcelable