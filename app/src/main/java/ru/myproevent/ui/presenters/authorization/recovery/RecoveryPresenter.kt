package ru.myproevent.ui.presenters.authorization.recovery

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.R
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class RecoveryPresenter(localRouter: Router) : BaseMvpPresenter<RecoveryView>(localRouter) {
    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var interAccessInfoRepository: IInternetAccessInfoRepository

    private inner class PasswordResetObserver(val email: String) : DisposableCompletableObserver() {
        override fun onComplete() {
            localRouter.navigateTo(screens.newPassword(email))
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.HttpException) {
                when (error.code()) {
                    400 -> {
                        viewState.showMessage(getString(R.string.invalid_email_format))
                        return
                    }
                    404 -> {
                        viewState.showMessage(getString(R.string.no_account_for_this_email))
                        return
                    }
                }
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

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            viewState.showMessage(getString(R.string.invalid_email))
            return
        }
        loginRepository
            .resetPassword(email)
            .observeOn(uiScheduler)
            .subscribeWith(PasswordResetObserver(email))
            .disposeOnDestroy()
    }

    fun authorize() {
        localRouter.navigateTo(screens.authorization())
    }
}