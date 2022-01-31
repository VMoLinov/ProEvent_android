package ru.myproevent.domain.models.repositories.profiles

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Single
import ru.myproevent.domain.models.ContactDto
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.models.entities.contact.Contact

interface IProEventProfilesRepository {
    fun saveProfile(profile: ProfileDto, newProfilePictureUri: Uri?) : Completable
    fun getProfile(id: Long) : Single<ProfileDto?>
    fun getContact(contactDto: ContactDto): Single<Contact>
}
