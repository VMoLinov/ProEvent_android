package ru.myproevent.domain.models.entities

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class Contact(
    id: Long,
    val status: Status? = null,
    email: String? = null,
    fullName: String? = null,
    nickName: String? = null,
    phone: String? = null,
    position: String? = null,
    birthdate: String? = null,
    imgUri: String? = null,
    description: String? = null
) : Profile(id, email, fullName, nickName, phone, position, birthdate, imgUri, description) {

    @Parcelize
    enum class Action : Parcelable { ADD, ACCEPT, CANCEL, DECLINE, DELETE }

    @Parcelize
    enum class Status : Parcelable {
        ALL, ACCEPTED, DECLINED, PENDING, REQUESTED;

        override fun toString() = name

        companion object {
            fun fromString(status: String) = valueOf(status)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(status?.ordinal ?: -1)
        parcel.writeString(email)
        parcel.writeString(fullName)
        parcel.writeString(nickName)
        parcel.writeString(phone)
        parcel.writeString(position)
        parcel.writeString(birthdate)
        parcel.writeString(imgUri)
        parcel.writeString(description)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact = with(parcel) {
            val status = parcel.readInt().let {
                if (it == -1) null
                else Status.values()[it]
            }
            Contact(
                readLong(),
                status,
                readString(),
                readString(),
                readString(),
                readString(),
                readString(),
                readString(),
                readString(),
                readString()
            )
        }

        override fun newArray(size: Int) = arrayOfNulls<Contact?>(size)
    }
}