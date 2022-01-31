package ru.myproevent.domain.models.repositories.images

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.UUIDBody
import java.io.File
import javax.inject.Inject


class ImagesRepository @Inject constructor(private val api: IProEventDataSource) :
    IImagesRepository {

    override fun saveImage(file: File): String {
        val string: String? = null
        val filePart = file.asRequestBody("image/png".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", "image", filePart)
        val call = object : Callback<UUIDBody> {
            override fun onResponse(call: Call<UUIDBody>, response: Response<UUIDBody>) {
                if (response.isSuccessful) {

                }
            }

            override fun onFailure(call: Call<UUIDBody>, t: Throwable) {
                TODO("Not yet implemented")
            }
        }
        api.saveImage("file", multipartBody).enqueue(call)
        return
    }

    override fun deleteImage(uuid: String): Call<ResponseBody> {
        return api.deleteImage(uuid)
    }
}
