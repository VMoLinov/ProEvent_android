package ru.myproevent.domain.models.repositories.profiles

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import ru.myproevent.domain.models.ContactDto
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.ProfileIdListDto
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Contact.Status
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.repositories.images.IImagesRepository
import ru.myproevent.domain.utils.toContact
import ru.myproevent.domain.utils.toProfile
import ru.myproevent.domain.utils.toProfileDto
import java.io.File
import javax.inject.Inject

class ProEventProfilesRepository @Inject constructor(
    private val api: IProEventDataSource,
    private val imagesRepository: IImagesRepository
) : IProEventProfilesRepository {

    override fun getProfile(id: Long): Single<Profile?> = Single.fromCallable {
        val response = api.getProfile(id).execute()
        if (response.isSuccessful) {
            return@fromCallable response.body()!!.toProfile()
        }
        throw HttpException(response)
    }.subscribeOn(Schedulers.io())

    override fun getMiniProfiles(ids: List<Long>): Single<List<Profile>>  = Single.fromCallable {
        val response = api.getMiniProfiles(ProfileIdListDto(ids)).execute()
        if (response.isSuccessful) {
            return@fromCallable response.body()!!.map { it.toProfile() }
        }
        throw HttpException(response)
    }.subscribeOn(Schedulers.io())

    // TODO: ошибки здесь обрабатывабтся не правильно
    override fun saveProfile(profile: Profile, newProfilePictureUri: Uri?): Completable =
        Completable.fromCallable {
            val newProfilePictureResponse = newProfilePictureUri?.let {
                imagesRepository.saveImage(File(it.path.orEmpty())).execute()
            }
            val profileDto = profile.toProfileDto()
            val oldProfileResponse = api.getProfile(profileDto.userId).execute()
            val newProfileResponse =
                if (oldProfileResponse.isSuccessful) {
                    // TODO: это штука могла быть не Successful не только потому что профиля нет
                    val oldProfile = oldProfileResponse.body()!!
                    if (profileDto.email == null) {
                        profileDto.email = oldProfile.email
                    }
                    if (profileDto.fullName == null) {
                        profileDto.fullName = oldProfile.fullName
                    }
                    if (profileDto.nickName == null) {
                        profileDto.nickName = oldProfile.nickName
                    }
                    if (profileDto.msisdn == null) {
                        profileDto.msisdn = oldProfile.msisdn
                    }
                    if (profileDto.position == null) {
                        profileDto.position = oldProfile.position
                    }
                    if (profileDto.birthdate == null) {
                        profileDto.birthdate = oldProfile.birthdate
                    }
                    if (profileDto.description == null) {
                        profileDto.description = oldProfile.description
                    }
                    if (profileDto.imgUri == null) {
                        profileDto.imgUri = oldProfile.imgUri
                    }
                    if (newProfilePictureResponse != null) {
                        if (newProfilePictureResponse.isSuccessful) {
                            profileDto.imgUri = newProfilePictureResponse.body()!!.uuid
                        } else {
                            throw HttpException(newProfilePictureResponse)
                        }
                    }
                    if (!oldProfile.imgUri.isNullOrBlank()) {
                        with(imagesRepository.deleteImage(oldProfile.imgUri!!).execute()) {
                            if (!isSuccessful) {
                                throw HttpException(this)
                            }
                        }
                    }
                    api.editProfile(profileDto).execute()
                } else {
                    if (newProfilePictureResponse != null) {
                        if (newProfilePictureResponse.isSuccessful) {
                            profileDto.imgUri = newProfilePictureResponse.body()!!.uuid
                        } else {
                            throw HttpException(newProfilePictureResponse)
                        }
                    }
                    api.createProfile(profileDto).execute()
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
