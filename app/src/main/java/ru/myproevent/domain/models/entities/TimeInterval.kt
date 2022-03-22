package ru.myproevent.domain.models.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class TimeInterval(
    /** the milliseconds since January 1, 1970, 00:00:00 GMT **/
    @SerializedName("startDate")
    var start: Long,
    /** the milliseconds since January 1, 1970, 00:00:00 GMT **/
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
