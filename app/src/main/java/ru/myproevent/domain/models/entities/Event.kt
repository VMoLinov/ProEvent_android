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
    var description: String?,
    var participantsUserIds: LongArray?,
    var city: String?,
    var address: Address?,
    var mapsFileIds: LongArray?,
    var pointsPointIds: LongArray?,
    var imageFile: String?,
) : Parcelable {
    @Parcelize
    enum class Status : Parcelable {
        ALL, ACTUAL, COMPLETED, CANCELLED;

        override fun toString() = name

        companion object {
            fun fromString(status: String) = valueOf(status)
        }
    }
}