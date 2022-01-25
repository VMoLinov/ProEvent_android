package ru.myproevent.domain.models.repositories.profiles

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import ru.myproevent.domain.models.ContactDto
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Contact.Status
import ru.myproevent.domain.models.repositories.images.IImagesRepository
import ru.myproevent.domain.utils.toContact
import java.io.File
import java.util.*
import javax.inject.Inject

class ProEventProfilesRepository @Inject constructor(
    private val api: IProEventDataSource,
    private val imagesRepository: IImagesRepository
) : IProEventProfilesRepository {

    override fun getProfile(id: Long): Single<ProfileDto?> = Single.fromCallable {
        val response = api.getProfile(id).execute()
        if (response.isSuccessful) {
            return@fromCallable response.body()
        }
        throw HttpException(response)
    }.subscribeOn(Schedulers.io())

    // TODO: ошибки здесь обрабатывабтся не правильно
    override fun saveProfile(profile: ProfileDto, newProfilePictureUri: Uri?): Completable =
        Completable.fromCallable {
            val newProfilePictureResponse = newProfilePictureUri?.let {
                imagesRepository.saveImage(File(it.path.orEmpty())).execute()
            }
            val oldProfileResponse = api.getProfile(profile.userId).execute()
            val newProfileResponse =
                if (oldProfileResponse.isSuccessful) {
                    // TODO: это штука могла быть не Successful не только потому что профиля нет
                    val oldProfile = oldProfileResponse.body()!!
                    if (profile.email == null) {
                        profile.email = oldProfile.email
                    }
                    if (profile.fullName == null) {
                        profile.fullName = oldProfile.fullName
                    }
                    if (profile.nickName == null) {
                        profile.nickName = oldProfile.nickName
                    }
                    if (profile.msisdn == null) {
                        profile.msisdn = oldProfile.msisdn
                    }
                    if (profile.position == null) {
                        profile.position = oldProfile.position
                    }
                    if (profile.birthdate == null) {
                        profile.birthdate = oldProfile.birthdate
                    }
                    if (profile.description == null) {
                        profile.description = oldProfile.description
                    }
                    if (profile.imgUri == null) {
                        profile.imgUri = oldProfile.imgUri
                    }
                    if (newProfilePictureResponse != null && newProfilePictureResponse.isSuccessful) {
                        profile.imgUri = newProfilePictureResponse.body()!!.uuid
                    } else {
                        throw HttpException(newProfilePictureResponse)
                    }
                    if (!oldProfile.imgUri.isNullOrBlank()) {
                        with(imagesRepository.deleteImage(oldProfile.imgUri!!).execute()) {
                            if (!isSuccessful) {
                                throw HttpException(this)
                            }
                        }
                    }
                    api.editProfile(profile).execute()
                } else {
                    if (newProfilePictureResponse != null) {
                        if (newProfilePictureResponse.isSuccessful) {
                            profile.imgUri = newProfilePictureResponse.body()!!.uuid
                        } else {
                            throw HttpException(newProfilePictureResponse)
                        }
                    }
                    api.createProfile(profile).execute()
                }
            if (!newProfileResponse.isSuccessful) {
                throw HttpException(newProfileResponse)
            }
            return@fromCallable null
        }.subscribeOn(Schedulers.io())

    override fun getContact(contactDto: ContactDto): Single<Contact> {
        return Single.fromCallable {
            val response = api.getProfile(contactDto.id).execute()
            if (response.isSuccessful) {
                return@fromCallable response.body()!!
                    .toContact(Status.fromString(contactDto.status))
            }
            throw HttpException(response)
        }.subscribeOn(Schedulers.io())
    }
}
