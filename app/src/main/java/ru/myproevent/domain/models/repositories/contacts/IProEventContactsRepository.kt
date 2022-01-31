package ru.myproevent.domain.models.repositories.contacts

import io.reactivex.Completable
import io.reactivex.Single
import ru.myproevent.domain.models.Page
import ru.myproevent.domain.models.entities.contact.Status

interface IProEventContactsRepository {
    fun getContacts(page: Int, size: Int, status: Status): Single<Page>
    fun addContact(id: Long): Completable
    fun deleteContact(id: Long): Completable
    fun acceptContact(id: Long): Completable
    fun declineContact(id: Long): Completable
}

