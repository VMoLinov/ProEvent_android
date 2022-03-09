package ru.myproevent.domain.models.repositories.images

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.myproevent.R
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.UUIDBody
import ru.myproevent.domain.models.repositories.resourceProvider.IResourceProvider
import java.io.File
import javax.inject.Inject


class ImagesRepository @Inject constructor(
    private val api: IProEventDataSource,
    private val resourceProvider: IResourceProvider
) :
    IImagesRepository {

    override fun saveImage(file: File): Single<UUIDBody> = with(resourceProvider) {
        val filePart = file.asRequestBody(getString(R.string.image_png).toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData(
            getString(R.string.file),
            getString(R.string.image),
            filePart
        )
        return api.saveImage(getString(R.string.file), multipartBody)
            .subscribeOn(Schedulers.io())
    }

    override fun deleteImage(uuid: String): Completable {
        return api.deleteImage(uuid).subscribeOn(Schedulers.io())
    }
}
