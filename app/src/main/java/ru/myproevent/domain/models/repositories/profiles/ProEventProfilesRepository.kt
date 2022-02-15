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
import java.util.*
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
    override fun saveProfile(profile: Profile): Completable =
        Completable.fromCallable {
            val oldProfileResponse = api.getProfile(profile.id).execute()
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
                    if (profile.phone == null) {
                        profile.phone = oldProfile.phone
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
                    if (profile.imgUri.isNullOrEmpty()) {
                        profile.imgUri = oldProfile.imgUri
                    }
                    api.editProfile(profile).execute()
                } else {
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
