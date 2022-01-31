package ru.myproevent.domain.models.repositories.profiles

import io.reactivex.Completable
import io.reactivex.Single
import ru.myproevent.domain.models.ContactDto
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.models.entities.Contact

interface IProEventProfilesRepository {
    fun saveProfile(profile: ProfileDto): Completable
    fun getProfile(id: Long): Single<ProfileDto?>
    fun getContact(contactDto: ContactDto): Single<Contact>
}
