package ru.myproevent.domain.models.repositories.event_maps

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.myproevent.domain.models.EventMapDto
import ru.myproevent.domain.models.IProEventDataSource
import javax.inject.Inject

class EventMapsRepository @Inject constructor(
    private val api: IProEventDataSource
) : IEventMapsRepository {
    override fun saveEventMap(mapName: String, mapImageUUID: String): Single<Long> =
        api.saveEventMap(EventMapDto(id = null, description = mapName, downloadUrl = mapImageUUID))
            .flatMap { mapDto -> Single.just(mapDto.id!!) }.subscribeOn(Schedulers.io())
}
