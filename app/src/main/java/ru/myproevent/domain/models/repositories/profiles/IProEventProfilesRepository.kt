package ru.myproevent.domain.models.repositories.profiles

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Single
import ru.myproevent.domain.models.ContactDto
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.entities.Contact

interface IProEventProfilesRepository {
    fun saveProfile(profile: Profile, newProfilePictureUri: Uri?) : Completable
    fun getProfile(id: Long) : Single<Profile?>
    fun getMiniProfiles(ids: List<Long>) : Single<List<Profile>>
    fun getContact(contactDto: ContactDto): Single<Contact>
}
