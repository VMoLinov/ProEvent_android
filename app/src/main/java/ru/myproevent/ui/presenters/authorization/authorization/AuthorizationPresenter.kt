package ru.myproevent.ui.presenters.authorization.authorization

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class AuthorizationPresenter(localRouter: Router) :
    BaseMvpPresenter<AuthorizationView>(localRouter) {
    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var interAccessInfoRepository: IInternetAccessInfoRepository

    private inner class LoginObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            viewState.finishAuthorization()
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.HttpException) {
                when (error.code()) {
                    401, 404 -> viewState.authorizationDataInvalid()
                }
                return
            }
            interAccessInfoRepository
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver(error.message))
                .disposeOnDestroy()
        }
    }

    fun authorize(email: String, password: String, rememberMe: Boolean) {
        // TODO: спросить у дизайнера нужено ли здесь отображать progress bar
        loginRepository
            .login(email, password, rememberMe)
            .observeOn(uiScheduler)
            .subscribeWith(LoginObserver())
            .disposeOnDestroy()
    }

    fun openRegistration() {
        localRouter.navigateTo(screens.registration())
    }

    fun recoverPassword() {
        localRouter.navigateTo(screens.recovery())
    }
}