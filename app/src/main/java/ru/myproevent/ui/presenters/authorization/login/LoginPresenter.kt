package ru.myproevent.ui.presenters.authorization.login

import android.widget.Toast
import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.ProEventApp
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class LoginPresenter(localRouter: Router) : BaseMvpPresenter<LoginView>(localRouter) {
    private inner class AuthObserver(val login: String) : DisposableCompletableObserver() {
        override fun onComplete() {
            profileRepository
                .saveProfile(
                    ProfileDto(
                        userId = loginRepository.getLocalId()!!,
                        nickName = login
                    )
                )
                .observeOn(uiScheduler)
                .subscribeWith(LoginSaveObserver())
                .disposeOnDestroy()
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            interAccessInfoRepository
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver(error.message))
                .disposeOnDestroy()
        }
    }

    private inner class LoginSaveObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            viewState.finishAuthorization()
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            interAccessInfoRepository
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver(error.message))
                .disposeOnDestroy()
        }
    }

    @Inject
    lateinit var interAccessInfoRepository: IInternetAccessInfoRepository

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var profileRepository: IProEventProfilesRepository

    fun confirmLogin(login: String) {
        if (loginRepository.getLocalEmail() == null || loginRepository.getLocalPassword() == null) {
            Toast.makeText(
                ProEventApp.instance,
                "Этого не должно было произойти: loginRepository.getLocalEmail() == null || loginRepository.getLocalPassword() == null",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        // TODO: уточнить у дизайнера нужен ли progress_bar
        loginRepository
            .login(loginRepository.getLocalEmail()!!, loginRepository.getLocalPassword()!!, true)
            .observeOn(uiScheduler)
            .subscribeWith(AuthObserver(login))
            .disposeOnDestroy()
    }
}