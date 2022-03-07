package ru.myproevent.domain.models.repositories.proevent_login

import android.util.Base64
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import ru.myproevent.R
import ru.myproevent.domain.models.*
import ru.myproevent.domain.models.repositories.local_proevent_user_token.ITokenLocalRepository
import ru.myproevent.domain.models.repositories.resourceProvider.IResourceProvider
import java.util.*
import javax.inject.Inject

class ProEventLoginRepository @Inject constructor(
    private val api: IProEventDataSource,
    private val tokenLocalRepository: ITokenLocalRepository,
    private val resourceProvider: IResourceProvider
) :
    IProEventLoginRepository {
    private var localToken: String? = null
        set(value) {
            field = value

            if (value == null) {
                tokenLocalRepository.removeTokenFromLocalStorage()
            } else if (rememberMe) {
                tokenLocalRepository.saveTokenInLocalStorage(value)
            }
        }

    private var localEmail: String? = null

    private var localPassword: String? = null

    private var rememberMe = true

    override fun getLocalToken(): String? {
        if (localToken != null) {
            return localToken
        }
        localToken = tokenLocalRepository.getTokenOrNull()
        return localToken
    }

    private fun decodeEmailFromLocalToken(): String? {
        val token = getLocalToken() ?: return null

        var start = token.indexOf('.') + 1
        var end = token.indexOf('.', start)
        val jSonStr = decodeJWT(token.substring(start, end))

        start = jSonStr.indexOf(resourceProvider.getString(R.string.token_sub)) + 6
        end = jSonStr.indexOf(',', start) - 1

        return jSonStr.substring(start, end)
    }

    override fun getLocalEmail() =
        if (localEmail != null) {
            localEmail
        } else {
            localEmail = decodeEmailFromLocalToken()
            localEmail
        }

    override fun getLocalPassword() = localPassword

    override fun getLocalId(): Long? {
        val token = getLocalToken() ?: return null

        var start = token.indexOf('.') + 1
        var end = token.indexOf('.', start)
        val jSonStr = decodeJWT(token.substring(start, end))

        start = jSonStr.indexOf(resourceProvider.getString(R.string.token_id)) + 4
        end = jSonStr.indexOf(',', start)
        return jSonStr.substring(start, end).toLong()
    }

    // TODO: убрать toLowerCase() для email, когда на сервере пофиксят баг с email чувствительным к регистру
    override fun login(email: String, password: String, rememberMe: Boolean): Completable {
        this.rememberMe = rememberMe
        return api.login(LoginBody(email.lowercase(Locale.getDefault()), password))
            .flatMapCompletable { body ->
                this.localToken = body.token
                this.localEmail = email
                this.localPassword = password
                Completable.complete()
            }
            // TODO: вынести Schedulers.io() в Dagger
            .subscribeOn(Schedulers.io())
    }

    override fun logoutFromThisDevice() {
        localToken = null
    }

    // TODO: убрать toLowerCase() для email, когда на сервере пофиксят баг с email чувствительным к регистру
    override fun signup(agreement: Boolean, email: String, password: String) =
        api.signup(SignupBody(agreement, email.lowercase(Locale.getDefault()), password))
            .flatMapCompletable { body ->
                this.localEmail = email
                this.localPassword = password
                Completable.complete()
            }
            .subscribeOn(Schedulers.io())

    // TODO: убрать toLowerCase() для email, когда на сервере пофиксят баг с email чувствительным к регистру
    override fun verificate(email: String, code: Int) =
        api.verificate(VerificationBody(code, email.lowercase(Locale.getDefault())))
            .subscribeOn(Schedulers.io())

    // TODO: убрать toLowerCase() для email, когда на сервере пофиксят баг с email чувствительным к регистру
    override fun refreshCheckCode(email: String) =
        api.refreshCheckCode(RefreshBody(email.lowercase(Locale.getDefault())))
            .subscribeOn(Schedulers.io())

    // TODO: убрать toLowerCase() для email, когда на сервере пофиксят баг с email чувствительным к регистру
    override fun resetPassword(email: String): Completable =
        api.resetPassword(ResetPasswordBody(email.lowercase(Locale.getDefault())))
            .subscribeOn(Schedulers.io())

    // TODO: убрать toLowerCase() для email, когда на сервере пофиксят баг с email чувствительным к регистру
    override fun setNewPassword(code: Int, email: String, password: String) =
        api.setNewPassword(NewPasswordBody(code, email.lowercase(Locale.getDefault()), password))
            .subscribeOn(Schedulers.io())


    override fun inviteUser(email: String): Completable {
        return api.inviteUser(email).subscribeOn(Schedulers.io())
    }

    private fun decodeJWT(str: String): String {
        val decodedBytes: ByteArray = Base64.decode(str, Base64.URL_SAFE)
        return String(decodedBytes, Charsets.UTF_8)
    }

    override fun saveFirebaseToken(token: String) = api.registerFirebaseToken(TokenBody(token))
        .subscribeOn(Schedulers.io())

    override fun deleteFirebaseToken(token: String) =
        api.deleteFirebaseToken(TokenBody(token)).subscribeOn(Schedulers.io())
}
