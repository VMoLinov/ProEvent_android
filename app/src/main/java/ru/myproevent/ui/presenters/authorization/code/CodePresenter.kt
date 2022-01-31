package ru.myproevent.ui.presenters.authorization.code

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.domain.models.providers.internet_access_info.IInternetAccessInfoProvider
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class CodePresenter(localRouter: Router) : BaseMvpPresenter<CodeView>(localRouter) {
    private inner class VerificationObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            localRouter.newRootScreen(screens.login())
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.adapter.rxjava2.HttpException) {
                if (error.code() == 401) {
                    viewState.showMessage("4-значный код введен неверно")
                    viewState.showCodeErrorMessage("4-значный код введен неверно")
                    return
                }
                viewState.showMessage("Произошла ошибка: ${error.code()}")
                return
            }
            interAccessInfoProvider
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver(error.message))
                .disposeOnDestroy()
        }
    }

    private inner class RefreshObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            viewState.showMessage("На почту отправлен новый код")
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.adapter.rxjava2.HttpException) {
                viewState.showMessage("Произошла ошибка: ${error.code()}")
                return
            }
            interAccessInfoProvider
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver(error.message))
                .disposeOnDestroy()
        }
    }

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var interAccessInfoProvider: IInternetAccessInfoProvider

    fun continueRegistration(code: String) {
        if (code.isNullOrBlank()) {
            viewState.showMessage("Введите 4-значеный код, котрый пришёл на почту")
            viewState.showCodeErrorMessage("Введите 4-значеный код, котрый пришёл на почту")
            return
        }
        if (code.length != 4) {
            viewState.showMessage("Код должен содержать 4 цифры")
            viewState.showCodeErrorMessage("Код должен содержать 4 цифры")
            return
        }
        with(code.toIntOrNull()) {
            if (this == null) {
                viewState.showMessage("Код должен содержать только цифровые символы")
                viewState.showCodeErrorMessage("Код должен содержать только цифровые символы")
                return
            }
            // TODO: спросить у дизайнера нужено ли здесь отображать progress bar
            loginRepository
                .verificate(loginRepository.getLocalEmail()!!, this)
                .observeOn(uiScheduler)
                .subscribeWith(VerificationObserver())
                .disposeOnDestroy()
        }
    }

    fun authorize() {
        localRouter.navigateTo(screens.authorization())
    }

    fun getEmail() = loginRepository.getLocalEmail()

    fun codeEdit() {
        viewState.showCodeErrorMessage(null)
    }

    fun refreshCode() {
        loginRepository
            .refreshCheckCode(loginRepository.getLocalEmail()!!)
            .observeOn(uiScheduler)
            .subscribeWith(RefreshObserver())
            .disposeOnDestroy()
    }
}