package ru.myproevent.domain.models.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class Profile(
    @SerializedName("userId")
    var id: Long,
    var email: String? = null,
    var fullName: String? = null,
    var nickName: String? = null,
    @SerializedName("msisdn")
    var phone: String? = null,
    var position: String? = null,
    var birthdate: String? = null,
    var imgUri: String? = null,
    var description: String? = null
) : Parcelable