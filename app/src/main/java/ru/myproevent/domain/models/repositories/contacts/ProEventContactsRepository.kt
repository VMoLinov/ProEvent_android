package ru.myproevent.domain.models.repositories.contacts

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Contact.Status
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.domain.utils.toContact
import javax.inject.Inject

class ProEventContactsRepository @Inject constructor(
    private val api: IProEventDataSource,
    private val profilesRepository: IProEventProfilesRepository
) :
    IProEventContactsRepository {

    override fun getContacts(status: Status): Single<List<Contact>> {
        return if (status == Status.ALL) {
            api.getContacts(1, Int.MAX_VALUE)
        } else {
            api.getContacts(1, Int.MAX_VALUE, status.toString())
        }.flatMap { data ->
            val contactDtos = data.content
            profilesRepository.getMiniProfiles(contactDtos.map { it.id }).map { profiles ->
                profiles.map { profile ->
                    val status =
                        Status.fromString(contactDtos.find { it.id == profile.id }!!.status)
                    profile.toContact(status)
                }
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun addContact(id: Long): Completable {
        return api.addContact(id).subscribeOn(Schedulers.io())
    }

    override fun deleteContact(id: Long): Completable {
        return api.deleteContact(id).subscribeOn(Schedulers.io())
    }

    override fun acceptContact(id: Long): Completable {
        return api.acceptContact(id).subscribeOn(Schedulers.io())
    }

    override fun declineContact(id: Long): Completable {
        return api.declineContact(id).subscribeOn(Schedulers.io())
    }
}