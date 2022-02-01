package ru.myproevent.domain.models.repositories.images

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.UUIDBody
import java.io.File
import javax.inject.Inject


class ImagesRepository @Inject constructor(private val api: IProEventDataSource) :
    IImagesRepository {

    override fun saveImage(file: File): Single<UUIDBody> {
        val filePart = file.asRequestBody("image/png".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", "image", filePart)
        return api.saveImage("file", multipartBody).subscribeOn(Schedulers.io())
    }

    override fun deleteImage(uuid: String): Completable {
        return api.deleteImage(uuid).subscribeOn(Schedulers.io())
    }
}
