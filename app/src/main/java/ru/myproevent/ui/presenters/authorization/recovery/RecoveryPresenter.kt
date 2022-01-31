package ru.myproevent.ui.presenters.authorization.recovery

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.domain.models.providers.internet_access_info.IInternetAccessInfoProvider
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class RecoveryPresenter(localRouter: Router) : BaseMvpPresenter<RecoveryView>(localRouter) {
    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var interAccessInfoProvider: IInternetAccessInfoProvider

    private inner class PasswordResetObserver(val email: String) : DisposableCompletableObserver() {
        override fun onComplete() {
            localRouter.navigateTo(screens.newPassword(email))
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.adapter.rxjava2.HttpException) {
                when (error.code()) {
                    400 -> {
                        viewState.showMessage("Неверный формат e-mail. У нас таких нет")
                        return
                    }
                    404 -> {
                        viewState.showMessage("Для указанного e-mail нет аккаунта. Иди зарегистрируйся")
                        return
                    }
                }
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

    fun resetPassword(email: String) {
        if (email.isNullOrBlank()) {
            viewState.showMessage("Невалидный E-mail")
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