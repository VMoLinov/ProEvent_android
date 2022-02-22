package ru.myproevent.domain.models.repositories.profiles

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response
import ru.myproevent.domain.models.ContactDto
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.ProfileIdListDto
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Contact.Status
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.utils.toContact
import ru.myproevent.domain.utils.toProfile
import javax.inject.Inject

class ProEventProfilesRepository @Inject constructor(
    private val api: IProEventDataSource
) : IProEventProfilesRepository {

    override fun getProfile(id: Long): Single<Profile?> = Single.fromCallable {
        val response = api.getProfile(id).execute()
        if (response.isSuccessful) {
            return@fromCallable response.body()!!.toProfile()
        }
        throw HttpException(response)
    }.subscribeOn(Schedulers.io())

    override fun getMiniProfiles(ids: List<Long>): Single<List<Profile>> = Single.fromCallable {
        val response = api.getMiniProfiles(ProfileIdListDto(ids)).execute()
        if (response.isSuccessful) {
            return@fromCallable response.body()!!.map { it.toProfile() }
        }
        throw HttpException(response)
    }.subscribeOn(Schedulers.io())

    override fun saveProfile(profile: Profile): Completable =
        Completable.fromCallable {
            val oldProfileResponse = api.getProfile(profile.id).execute()
            val newProfileResponse = compareProfiles(oldProfileResponse, profile)
            if (!newProfileResponse.isSuccessful) {
                throw HttpException(newProfileResponse)
            }
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

    private fun compareProfiles(
        oldProfileResponse: Response<Profile>,
        profile: Profile
    ): Response<Profile> {
        return if (oldProfileResponse.isSuccessful && oldProfileResponse.body() != null) {
            val oldProfile = oldProfileResponse.body()!!
            api.editProfile(profile.merge(oldProfile)).execute()
        } else if (oldProfileResponse.errorBody() != null) {
            throw HttpException(oldProfileResponse)
        } else {
            api.createProfile(profile).execute()
        }
    }
}
