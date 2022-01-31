package ru.myproevent.domain.models.repositories.proevent_login

import io.reactivex.Completable
import io.reactivex.Single
import ru.myproevent.domain.models.HintResponse
import ru.myproevent.domain.models.Suggestion

interface IProEventLoginRepository {
    fun getLocalToken(): String?
    fun getLocalId(): Long?
    fun getLocalEmail(): String?
    fun getLocalPassword(): String?
    fun login(email: String, password: String, rememberMe : Boolean): Completable
    fun logoutFromThisDevice()
    fun signup(agreement: Boolean, email: String, password: String): Completable
    fun verificate(email: String, code: Int): Completable
    fun refreshCheckCode(email: String): Completable
    fun resetPassword(email: String): Completable
    fun setNewPassword(code: Int, email: String, password: String): Completable
    fun getEmailHint(part_email: String): Single<List<Suggestion>>
}