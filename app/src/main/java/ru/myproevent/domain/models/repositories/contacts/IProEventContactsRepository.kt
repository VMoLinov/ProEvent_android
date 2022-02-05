package ru.myproevent.domain.models.repositories.contacts

import io.reactivex.Completable
import io.reactivex.Single
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Contact.Status

interface IProEventContactsRepository {
    fun getContacts(status: Status = Status.ALL): Single<List<Contact>>
    fun addContact(id: Long): Completable
    fun deleteContact(id: Long): Completable
    fun acceptContact(id: Long): Completable
    fun declineContact(id: Long): Completable
}

