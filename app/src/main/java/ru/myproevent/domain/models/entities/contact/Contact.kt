package ru.myproevent.domain.models.entities.contact

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
) : Parcelable