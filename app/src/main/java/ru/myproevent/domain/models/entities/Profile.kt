package ru.myproevent.domain.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Profile(
    var id: Long,
    var email: String? = null,
    var fullName: String? = null,
    var nickName: String? = null,
    var phone: String? = null,
    var position: String? = null,
    var birthdate: String? = null,
    var imgUri: String? = null,
    var description: String? = null
) : Parcelable