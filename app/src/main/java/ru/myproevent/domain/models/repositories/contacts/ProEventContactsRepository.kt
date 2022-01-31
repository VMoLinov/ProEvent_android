package ru.myproevent.domain.models.repositories.contacts

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.Page
import ru.myproevent.domain.models.entities.contact.Status
import javax.inject.Inject

class ProEventContactsRepository @Inject constructor(private val api: IProEventDataSource) :
    IProEventContactsRepository {

    override fun getContacts(page: Int, size: Int, status: Status): Single<Page> {
        return if (status == Status.ALL) {
            api.getContacts(page, size)
        } else {
            api.getContacts(page, size, status.value)
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