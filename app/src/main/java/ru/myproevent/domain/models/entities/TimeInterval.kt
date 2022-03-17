package ru.myproevent.domain.models.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class TimeInterval(
    @SerializedName("startDate")
    var start: Long,
    @SerializedName("endDate")
    var end: Long
) : Parcelable, Serializable, Comparable<TimeInterval> {

    override fun compareTo(other: TimeInterval): Int {
        return when {
            start > other.start -> 1
            start == other.start -> 0
            else -> -1
        }
    }
}
