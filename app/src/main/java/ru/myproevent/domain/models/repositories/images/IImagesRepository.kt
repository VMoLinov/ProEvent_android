package ru.myproevent.domain.models.repositories.images

import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File

interface IImagesRepository {
    fun saveImage(file: File): String
    fun deleteImage(uuid: String): Call<ResponseBody>
}
