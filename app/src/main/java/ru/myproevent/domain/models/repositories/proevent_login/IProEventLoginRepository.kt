package ru.myproevent.domain.models.repositories.proevent_login

import io.reactivex.Completable

interface IProEventLoginRepository {
    fun getLocalToken(): String?
    fun getLocalId(): Long?
    fun getLocalEmail(): String?
    fun getLocalPassword(): String?
    fun login(email: String, password: String, rememberMe: Boolean): Completable
    fun logoutFromThisDevice()
    fun signup(agreement: Boolean, email: String, password: String): Completable
    fun verificate(email: String, code: Int): Completable
    fun refreshCheckCode(email: String): Completable
    fun resetPassword(email: String): Completable
    fun setNewPassword(code: Int, email: String, password: String): Completable
    fun saveFirebaseToken(token: String): Completable
    fun deleteFirebaseToken(token: String): Completable
    fun inviteUser(email: String): Completable
}
