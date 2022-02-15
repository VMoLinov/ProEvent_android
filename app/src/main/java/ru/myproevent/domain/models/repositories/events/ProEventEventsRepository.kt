package ru.myproevent.domain.models.repositories.events

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.myproevent.domain.models.EventDto
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.domain.utils.toEvent
import ru.myproevent.domain.utils.toEventDto
import javax.inject.Inject

class ProEventEventsRepository @Inject constructor(
    private val api: IProEventDataSource,
    private val loginRepository: IProEventLoginRepository
) : IProEventEventsRepository {

    override fun saveEvent(event: Event): Single<Event> {
        return api.saveEvent(event.toEventDto()).map { it.toEvent() }.subscribeOn(Schedulers.io())
    }

    override fun editEvent(event: Event): Completable {
        return Completable.fromSingle(
            api.editEvent(event.toEventDto()).subscribeOn(Schedulers.io())
        )
    }

    override fun deleteEvent(event: Event): Completable {
        return api.deleteEvent(event.toEventDto()).subscribeOn(Schedulers.io())
    }

    override fun deleteEvent(id: Long): Completable {
        val eventDto = EventDto(
            id,
            "",
            loginRepository.getLocalId()!!,
            "",
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        return api.deleteEvent(eventDto).subscribeOn(Schedulers.io())
    }

    override fun getEvent(id: Long): Single<Event> {
        return api.getEvent(id).map { it.toEvent() }.subscribeOn(Schedulers.io())
    }

    override fun getEvents(): Single<List<Event>> {
        Log.d("[BUG]", "loginRepository.getLocalId(): ${loginRepository.getLocalId()}")
        val v = api
            .getEventsForUser(
                loginRepository.getLocalId()!!
            )
        return v.map { it.map { it.toEvent() } }
            .subscribeOn(Schedulers.io())
    }
}