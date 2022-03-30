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
    var description: String? = null,
    val deleted: Boolean = true
) : Parcelable {

    internal fun merge(oldProfile: Profile): Profile {
        if (email.isNullOrBlank()) email = oldProfile.email
        if (fullName.isNullOrBlank()) fullName = oldProfile.fullName
        if (nickName.isNullOrBlank()) nickName = oldProfile.nickName
        if (phone.isNullOrBlank()) phone = oldProfile.phone
        if (position.isNullOrBlank()) position = oldProfile.position
        if (birthdate.isNullOrBlank()) birthdate = oldProfile.birthdate
        if (description.isNullOrBlank()) description = oldProfile.description
        if (imgUri.isNullOrBlank()) imgUri = oldProfile.imgUri
        return this
    }
}
