package ru.myproevent.domain.models.repositories.images

import io.reactivex.Completable
import io.reactivex.Single
import ru.myproevent.domain.models.UUIDBody
import java.io.File

interface IImagesRepository {
    fun saveImage(file: File): Single<UUIDBody>
    fun deleteImage(uuid: String): Completable
}
