package ru.myproevent.ui.presenters.settings.security

import android.widget.Toast
import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import ru.myproevent.ProEventApp
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject


// TODO: рефакторинг: данный presenter практически копирует AccountPresenter. Возможно стоит вынести общий функционал в абстрактынй класс
class SecurityPresenter(localRouter: Router) : BaseMvpPresenter<SecurityView>(localRouter) {
    private inner class ProfileEditObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            viewState.showMessage("Изменения сохранены")
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            interAccessInfoRepository
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver("Этого не должно было произойти(ProfileEditObserver):\n${error}"))
                .disposeOnDestroy()
        }
    }

    private var userProfile: ProfileDto? = null

    private inner class ProfileGetObserver : DisposableSingleObserver<ProfileDto>() {
        override fun onSuccess(profileDto: ProfileDto) {
            userProfile = profileDto
            viewState.showProfile(profileDto)
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.adapter.rxjava2.HttpException) {
                when (error.code()) {
                    404 -> viewState.makeProfileEditable()
                }
                return
            }
            interAccessInfoRepository
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver("Этого не должно было произойти (ProfileGetObserver):\n${error}"))
                .disposeOnDestroy()
        }
    }

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var profilesRepository: IProEventProfilesRepository

    @Inject
    lateinit var interAccessInfoRepository: IInternetAccessInfoRepository

    fun saveProfile(email: String, login: String) {
        // TODO:
        if(userProfile == null){
            Toast.makeText(ProEventApp.instance, "Операция сохранения в данный момент не доступна, так как профиль ещё не загрузился", Toast.LENGTH_LONG).show()
            return
        }
        userProfile!!.apply {
            this.email = email
            this.nickName = login
        }
        profilesRepository
            .saveProfile(userProfile!!)
            .observeOn(uiScheduler)
            .subscribeWith(ProfileEditObserver())
            .disposeOnDestroy()
    }

    fun getProfile() {
        profilesRepository
            .getProfile(loginRepository.getLocalId()!!)
            .observeOn(uiScheduler)
            .subscribeWith(ProfileGetObserver())
            .disposeOnDestroy()
    }
}