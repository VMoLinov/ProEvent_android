package ru.myproevent.domain.utils

import android.content.res.Resources
import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.myproevent.domain.models.ContactDto
import ru.myproevent.domain.models.EventDto
import ru.myproevent.domain.models.ProfileMiniDto
import ru.myproevent.domain.models.entities.Address
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Contact.Status
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.entities.Profile
import java.util.*

fun ImageView.load(url: String) {
    Glide.with(context).load(url).into(this)
}

fun Profile.toProfile() =
    Profile(id, email, fullName, nickName, phone, position, birthdate, imgUri, description)

fun ProfileMiniDto.toProfile() =
    Profile(userId, null, fullName, nickName, null, null, null, imgUri, description)

fun Profile.toProfileDto() =
    Profile(id, email, fullName, nickName, phone, position, birthdate, imgUri, description)

fun Profile.toContact(status: Status?) =
    Contact(id, status, email, fullName, nickName, phone, position, birthdate, imgUri, description)

fun Contact.toContactDto() = ContactDto(id, status.toString())

fun EventDto.toEvent(): Event {
    return Event(
        id,
        name,
        ownerUserId,
        Event.Status.fromString(eventStatus),
        eventDates ?: TreeSet(),
        description,
        participantsUserIds,
        city,
        address?.let { Address.fromString(it) },
        mapsFileIds,
        pointsPointIds,
        imageFile
    )
}

fun Event.toEventDto(): EventDto {
    return EventDto(
        id,
        name,
        ownerUserId,
        status.toString(),
        dates,
        description,
        participantsUserIds,
        city,
        address?.formatToString(),
        mapsFileIds,
        pointsPointIds,
        imageFile
    )
}

fun Address.Companion.fromString(str: String): Address? {
    val address: Address?

    val addressVariables = str.split(" || ")
    address = if (addressVariables.size != 3) {
        null
    } else {
        try {
            Address(
                addressVariables[1].toDouble(),
                addressVariables[2].toDouble(),
                addressVariables[0]
            )
        } catch (e: NumberFormatException) {
            null
        }
    }
    return address
}

fun Address.formatToString(): String {
    return "$addressLine || $latitude || $longitude"
}

fun pxValue(dp: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp,
    Resources.getSystem().displayMetrics
)
