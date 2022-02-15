package ru.myproevent.ui.presenters.authorization.code

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.R
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
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
            if (error is retrofit2.HttpException) {
                if (error.code() == 401) {
                    viewState.showMessage(getString(R.string.code_error_message_01))
                    viewState.showCodeErrorMessage(getString(R.string.code_error_message_01))
                    return
                }
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

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var interAccessInfoRepository: IInternetAccessInfoRepository

    fun continueRegistration(code: String) {
        if (code.isBlank()) {
            viewState.showMessage(getString(R.string.enter_4digit_email_code))
            viewState.showCodeErrorMessage(getString(R.string.enter_4digit_email_code))
            return
        }
        if (code.length != 4) {
            viewState.showMessage(getString(R.string.code_must_contain_4_digits))
            viewState.showCodeErrorMessage(getString(R.string.code_must_contain_4_digits))
            return
        }
        with(code.toIntOrNull()) {
            if (this == null) {
                viewState.showMessage(getString(R.string.code_must_contain_only_digits))
                viewState.showCodeErrorMessage(getString(R.string.code_must_contain_only_digits))
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