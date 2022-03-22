package ru.myproevent.domain.models.repositories.event_maps

import io.reactivex.Single

interface IEventMapsRepository{
    fun saveEventMap(mapName: String, mapImageUUID: String): Single<Long>
}