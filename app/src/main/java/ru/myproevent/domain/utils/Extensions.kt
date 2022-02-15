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
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.load(url: String) {
    Glide.with(context).load(url).into(this)
}

fun Profile.toProfile() =
    Profile(id, email, fullName, nickName, phone, position, birthdate, imgUri, description)

fun ProfileMiniDto.toProfile() =
    Profile(userId, null, fullName, nickName, null, null, null, imgUri, null)

fun Profile.toProfileDto() =
    Profile(id, email, fullName, nickName, phone, position, birthdate, imgUri, description)

fun Profile.toContact(status: Contact.Status?) =
    Contact(id, status, email, fullName, nickName, phone, position, birthdate, imgUri, description)

fun Contact.toContactDto() = ContactDto(id, status.toString())
fun EventDto.toEvent(datePattern: String = "yyyy-MM-dd'T'HH:mm:ss"): Event {
    val dateFormat = SimpleDateFormat(datePattern)
    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
    return Event(
        id,
        name,
        ownerUserId,
        Event.Status.fromString(eventStatus),
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
        status.toString(),
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

// TODO: рефакторинг: возможно стоит использовать не каст, а чтонибудь другое. Пока не знаю как это сделать проще
fun LongArray.toParticipantItems(header: EventScreenItem.FormsHeader<EventScreenItem.ListItem>): Array<EventScreenItem.ParticipantItem>{
    val arrayOfLongs = this.toTypedArray()
    val result = arrayOfNulls<EventScreenItem.ParticipantItem>(size)
    for (index in indices)
        result[index] = EventScreenItem.ParticipantItem(arrayOfLongs[index], header)
    @Suppress("UNCHECKED_CAST")
    return result as Array<EventScreenItem.ParticipantItem>
}