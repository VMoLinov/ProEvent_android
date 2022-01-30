package ru.myproevent.domain.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val userId: Long,
    val status: Status? = null,
    var fullName: String? = null,
    var nickName: String? = null,
    var msisdn: String? = null,
    var position: String? = null,
    var birthdate: String? = null,
    var imgUri: String? = null,
    var description: String? = null
) : Parcelable {
    enum class Action { ADD, ACCEPT, CANCEL, DECLINE, DELETE }

    enum class Status(val value: String) {
        ALL("ALL"),
        ACCEPTED("ACCEPTED"),
        DECLINED("DECLINED"),
        PENDING("PENDING"),
        REQUESTED("REQUESTED");

        companion object {
            fun fromString(status: String) = valueOf(status)
        }
    }
}