package ru.myproevent.domain.models

import android.os.Parcelable
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.entities.TimeInterval
import java.util.*

interface IProEventDataSource {
    @POST("auth/login")
    fun login(@Body loginBody: LoginBody): Single<LoginResponse?>

    @POST("auth/signup")
    fun signup(@Body signupBody: SignupBody): Single<SignupResponse?>

    @POST("auth/verificationCheckCode")
    fun verificate(@Body verificationBody: VerificationBody): Completable

    @POST("auth/refreshCheckCode")
    fun refreshCheckCode(@Body refreshBody: RefreshBody): Completable

    @POST("auth/firebase")
    fun registerFirebaseToken(@Body token: TokenBody): Completable

    @DELETE("auth/firebase")
    fun deleteFirebaseToken(@Body token: TokenBody): Completable

    @GET("auth/invite")
    fun inviteUser(@Query("email") email: String): Completable

    @POST("profiles")
    fun createProfile(@Body profile: Profile): Call<Profile>

    @PUT("profiles")
    fun editProfile(@Body profile: Profile): Call<Profile>

    @GET("profiles/user/{userId}")
    fun getProfile(@Path("userId") userId: Long): Call<Profile>

    @POST("profiles/list")
    fun getMiniProfiles(@Body ids: ProfileIdListDto): Call<List<ProfileMiniDto>>

    @GET("profiles/search")
    fun searchProfiles(@Query("search") search: String): Call<List<ProfileMiniDto>>

    @GET("contacts")
    fun getContacts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<Page>

    @GET("contacts")
    fun getContacts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("status") status: String
    ): Single<Page>

    @POST("contacts/{id}")
    fun addContact(@Path("id") id: Long): Completable

    @DELETE("contacts/{id}")
    fun deleteContact(@Path("id") id: Long): Completable

    @PUT("contacts/accept/{id}")
    fun acceptContact(@Path("id") id: Long): Completable

    @PUT("contacts/decline/{id}")
    fun declineContact(@Path("id") id: Long): Completable

    @POST("events")
    fun saveEvent(@Body event: EventDto): Single<EventDto>

    @PUT("events")
    fun editEvent(@Body event: EventDto): Single<EventDto>

    @HTTP(method = "DELETE", path = "events", hasBody = true)
    fun deleteEvent(@Body event: EventDto): Completable

    @GET("events/{eventId}")
    fun getEvent(@Path("eventId") eventId: Long): Single<EventDto>

    @GET("events/user/{userId}")
    fun getEventsForUser(@Path("userId") userId: Long): Single<List<EventDto>>

    @POST("auth/resetPassword")
    fun resetPassword(@Body resetPasswordBody: ResetPasswordBody): Completable

    @POST("auth/newPassword")
    fun setNewPassword(@Body newPasswordBody: NewPasswordBody): Completable

    @Multipart
    @POST("storage")
    fun saveImage(
        @Part("file") name: String,
        @Part image: MultipartBody.Part
    ): Single<UUIDBody>

    @DELETE("storage/{uuid}")
    fun deleteImage(@Path("uuid") uuid: String): Completable
}

data class TokenBody(val token: String)

data class LoginBody(val email: String, val password: String)
data class LoginResponse(val token: String)

data class SignupBody(val agreement: Boolean, val email: String, val password: String)
data class SignupResponse(val agreement: Boolean, val email: String, val password: String)

data class VerificationBody(val code: Int, val email: String)

data class RefreshBody(val email: String)

data class ResetPasswordBody(val email: String)

data class NewPasswordBody(val code: Int, val email: String, val password: String)

data class UUIDBody(val uuid: String)

//data class ProfileDto(
//    var userId: Long,
//    var email: String? = null,
//    var fullName: String? = null,
//    var nickName: String? = null,
//    var msisdn: String? = null,
//    var position: String? = null,
//    var birthdate: String? = null,
//    var imgUri: String? = null,
//    var description: String? = null
//)

data class ProfileMiniDto(
    val userId: Long,
    val fullName: String? = null,
    val nickName: String? = null,
    val imgUri: String? = null,
    val description: String? = null
)

data class ContactDto(val id: Long, val status: String)

data class Page(
    val content: List<ContactDto>,
    val pageable: Pageable,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)

data class Pageable(
    val offset: Long,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: Sort,
    val unPaged: Boolean
)

data class Sort(val empty: Boolean, val sorted: Boolean, val unsorted: Boolean)

data class EventDto(
    val id: Long?,
    val name: String,
    val ownerUserId: Long,
    val eventStatus: String,
    val eventDates: TreeSet<TimeInterval>?,
    val description: String?,
    val participantsUserIds: LongArray?,
    val city: String?,
    val address: String?,
    val mapsFileIds: LongArray?,
    val pointsPointIds: LongArray?,
    val imageFile: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as EventDto
        if (ownerUserId != other.ownerUserId) return false
        if (eventDates != other.eventDates) return false
        return true
    }

    override fun hashCode(): Int {
        var result = ownerUserId.hashCode()
        result = 31 * result + (eventDates?.hashCode() ?: 0)
        return result
    }
}
