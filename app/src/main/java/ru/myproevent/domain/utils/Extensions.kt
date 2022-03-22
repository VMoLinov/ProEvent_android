package ru.myproevent.domain.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
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

fun Profile.toContact(status: Contact.Status?) =
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

// TODO: рефакторинг: возможно стоит использовать не каст, а чтонибудь другое. Пока не знаю как это сделать проще
fun LongArray.toParticipantItems(header: EventScreenItem.FormsHeader<EventScreenItem.ListItem>): Array<EventScreenItem.ParticipantItem>{
    val arrayOfLongs = this.toTypedArray()
    val result = arrayOfNulls<EventScreenItem.ParticipantItem>(size)
    for (index in indices)
        result[index] = EventScreenItem.ParticipantItem(arrayOfLongs[index], header)
    @Suppress("UNCHECKED_CAST")
    return result as Array<EventScreenItem.ParticipantItem>
}

// https://stackoverflow.com/a/9563438
/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
fun convertDpToPixel(dp: Float, context: Context): Float {
    return dp * (context.resources
        .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
fun convertPixelsToDp(px: Float, context: Context): Float {
    return px / (context.resources
        .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}