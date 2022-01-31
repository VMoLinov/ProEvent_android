package ru.myproevent.domain.utils

import android.content.res.Resources
import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.myproevent.domain.models.EventDto
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.models.entities.Address
import ru.myproevent.domain.models.entities.contact.Contact
import ru.myproevent.domain.models.entities.contact.Status
import ru.myproevent.domain.models.entities.Event
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.load(url: String) {
    Glide.with(context).load(url).into(this)
}

fun ProfileDto.toContact(status: Status?) =
    Contact(userId, status, fullName, nickName, msisdn, position, birthdate, imgUri, description)

fun Contact.toProfileDto() =
    ProfileDto(userId, fullName, nickName, msisdn, position, birthdate, imgUri, description)

fun EventDto.toEvent(datePattern: String = "yyyy-MM-dd'T'HH:mm:ss"): Event {
    val dateFormat = SimpleDateFormat(datePattern)
    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
    return Event(
        id,
        name,
        ownerUserId,
        Event.Status.fromString(eventStatus),
        dateFormat.parse(startDate),
        dateFormat.parse(endDate),
        description,
        participantsUserIds,
        city,
        address?.let { Address.fromString(it) },
        mapsFileIds,
        pointsPointIds,
        imageFile
    )
}

fun Event.toEventDto(datePattern: String = "yyyy-MM-dd'T'HH:mm:ss"): EventDto {
    val dateFormat = SimpleDateFormat(datePattern)
    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
    return EventDto(
        id,
        name,
        ownerUserId,
        eventStatus.value,
        dateFormat.format(startDate),
        dateFormat.format(endDate),
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
    if (addressVariables.size != 3) {
        address = null
    } else {
        address = try {
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