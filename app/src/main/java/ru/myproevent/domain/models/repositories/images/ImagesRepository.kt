package ru.myproevent.domain.models.repositories.images

import ru.myproevent.domain.models.IProEventDataSource
import javax.inject.Inject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import ru.myproevent.domain.models.UUIDBody
import java.io.File


class ImagesRepository @Inject constructor(private val api: IProEventDataSource) :
    IImagesRepository {

    override fun saveImage(file: File): Call<UUIDBody> {
        val filePart = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", "image", filePart)
        return api.saveImage("file", multipartBody)
    }

    override fun deleteImage(uuid: String): Call<ResponseBody> {
        return api.deleteImage(uuid)
    }
}
