package ru.myproevent.domain.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Event(
    var id: Long?,
    var name: String,
    var ownerUserId: Long,
    var status: Status,
    var dates: TreeSet<TimeInterval>,
    var description: String?,
    var participantsUserIds: LongArray?,
    var city: String?,
    var address: Address?,
    var mapsFileIds: LongArray?,
    var pointsPointIds: LongArray?,
    var imageFile: String?
) : Parcelable {
    @Parcelize
    enum class Status : Parcelable {
        ALL, ACTUAL, COMPLETED, CANCELLED;

        override fun toString() = name

        companion object {
            fun fromString(status: String) = valueOf(status)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Event
        if (ownerUserId != other.ownerUserId) return false
        if (dates != other.dates) return false
        return true
    }

    override fun hashCode(): Int {
        var result = ownerUserId.hashCode()
        result = 31 * result + dates.hashCode()
        return result
    }
}
