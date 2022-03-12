package ru.myproevent.domain.models.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeInterval(
    @SerializedName("startDate")
    var start: Long,
    @SerializedName("endDate")
    var end: Long
) : Parcelable, Comparable<TimeInterval> {

    override fun compareTo(other: TimeInterval): Int {
        return when {
            start > other.start -> 1
            start == other.start -> 0
            else -> -1
        }
    }
}
