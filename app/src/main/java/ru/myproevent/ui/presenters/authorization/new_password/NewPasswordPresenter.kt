package ru.myproevent.ui.presenters.authorization.new_password

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.R
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class NewPasswordPresenter(localRouter: Router) : BaseMvpPresenter<NewPasswordView>(localRouter) {
    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var interAccessInfoRepository: IInternetAccessInfoRepository

    private inner class RefreshObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            viewState.showMessage(getString(R.string.new_code_has_been_sent_to_email))
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.HttpException) {
                viewState.showMessage(getString(R.string.error_occurred, error.code()))
                return
            }
            interAccessInfoRepository
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
            if (error is retrofit2.HttpException) {
                viewState.showMessage(getString(R.string.error_occurred, error.code()))
                return
            }
            interAccessInfoRepository
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver(error.message))
                .disposeOnDestroy()
        }
    }

    private inner class NewPasswordSetObserver(
        val email: String,
        val password: String,
        val rememberPassword: Boolean
    ) : DisposableCompletableObserver() {
        override fun onComplete() {
            loginRepository
                .login(email, password, rememberPassword)
                .observeOn(uiScheduler)
                .subscribeWith(LoginObserver())
                .disposeOnDestroy()
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.HttpException) {
                viewState.showMessage(getString(R.string.error_occurred, error))
                return
            }
            interAccessInfoRepository
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

    fun setNewPassword(
        code: String,
        email: String,
        password: String,
        confirmedPassword: String,
        rememberPassword: Boolean
    ) {
        val errorMessage = StringBuilder()
        if (code.isBlank()) {
            errorMessage.append(getString(R.string.enter_4digit_email_code)).append(".\n")
            viewState.showCodeErrorMessage(getString(R.string.enter_4digit_email_code))
        } else if (code.toIntOrNull() == null) {
            errorMessage.append(getString(R.string.code_must_contain_only_digits)).append(".\n")
            viewState.showCodeErrorMessage(getString(R.string.code_must_contain_only_digits))
        }
        if (code.length != 4) {
            errorMessage.append(getString(R.string.code_must_contain_4_digits)).append(".\n")
            viewState.showCodeErrorMessage(getString(R.string.code_must_contain_4_digits))
        }
        if (password != confirmedPassword) {
            errorMessage.append(getString(R.string.passwords_not_same)).append(".\n")
            viewState.showPasswordConfirmErrorMessage(getString(R.string.passwords_not_same) + ".\n")
        }
        if (errorMessage.isNotBlank()) {
            viewState.showMessage(errorMessage.toString())
            return
        }
        loginRepository
            .setNewPassword(code.toInt(), email, password)
            .observeOn(uiScheduler)
            .subscribeWith(NewPasswordSetObserver(email, password, rememberPassword))
            .disposeOnDestroy()
    }

    fun authorize() = localRouter.navigateTo(screens.authorization())

    fun codeEdit() {
        viewState.showCodeErrorMessage(null)
    }
}
