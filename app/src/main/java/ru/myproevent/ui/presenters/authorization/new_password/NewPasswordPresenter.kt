package ru.myproevent.ui.presenters.authorization.new_password

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.domain.models.providers.internet_access_info.IInternetAccessInfoProvider
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class NewPasswordPresenter(localRouter: Router) : BaseMvpPresenter<NewPasswordView>(localRouter) {
    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var interAccessInfoProvider: IInternetAccessInfoProvider

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

    private inner class LoginObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            viewState.finishAuthorization()
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

    private inner class NewPasswordSetObserver(val email: String, val password: String, val rememberPassword: Boolean) : DisposableCompletableObserver() {
        override fun onComplete() {
            loginRepository
                .login(email, password, rememberPassword)
                .observeOn(uiScheduler)
                .subscribeWith(LoginObserver())
                .disposeOnDestroy()
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.adapter.rxjava2.HttpException) {
                viewState.showMessage("Произошла ошибка: $error")
                return
            }
            interAccessInfoProvider
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver(error.message))
                .disposeOnDestroy()
        }
    }

    fun refreshCode(email: String) {
        loginRepository
            .refreshCheckCode(email)
            .observeOn(uiScheduler)
            .subscribeWith(RefreshObserver())
            .disposeOnDestroy()
    }

    fun setNewPassword(code: String, email: String, password: String, confirmedPassword: String, rememberPassword: Boolean) {
        var errorMessage: String? = ""
        if (code.isNullOrBlank()) {
            errorMessage += "Введите 4-значеный код, котрый пришёл на почту.\n"
            viewState.showCodeErrorMessage("Введите 4-значеный код, котрый пришёл на почту")
        } else if(code.toIntOrNull() == null){
            errorMessage += "Код должен содержать только цифровые символы\n"
            viewState.showCodeErrorMessage("Код должен содержать только цифровые символы")
        }
        if (code.length != 4) {
            errorMessage += "Код должен содержать 4 цифры.\n"
            viewState.showCodeErrorMessage("Код должен содержать 4 цифры")
        }
        if (password != confirmedPassword) {
            errorMessage += "Пароли не совпадают.\n"
            viewState.showPasswordConfirmErrorMessage("Пароли не совпадают.\n")
        }
        if (!errorMessage.isNullOrBlank()) {
            viewState.showMessage(errorMessage)
            return
        }
        loginRepository
            .setNewPassword(code.toInt(), email, password)
            .observeOn(uiScheduler)
            .subscribeWith(NewPasswordSetObserver(email, password, rememberPassword)).disposeOnDestroy()
    }

    fun authorize() = localRouter.navigateTo(screens.authorization())

    fun codeEdit() {
        viewState.showCodeErrorMessage(null)
    }
}
