package ru.myproevent.ui.presenters.authorization.registration

import android.annotation.SuppressLint
import com.github.terrakok.cicerone.Router
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import java.util.regex.Pattern
import javax.inject.Inject

val VALID_EMAIL_ADDRESS_REGEX: Pattern =
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
val VALID_PASSVORD_REGEX: Pattern =
    Pattern.compile("^(?=.*[0-9])(?=.*[а-яё])(?=\\S+\$).{6,}\$", Pattern.CASE_INSENSITIVE)

class RegistrationPresenter(localRouter: Router) : BaseMvpPresenter<RegistrationView>(localRouter) {
    private inner class SignupObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            localRouter.navigateTo(screens.code())
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.adapter.rxjava2.HttpException) {
                if (error.code() == 409) {
                    viewState.showMessage("Для введённого email уже есть аккаунт")
                    viewState.showEmailErrorMessage("Для введённого email уже есть аккаунт")
                    return
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

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var interAccessInfoRepository: IInternetAccessInfoRepository

    fun signup() {
        localRouter.navigateTo(screens.authorization())
    }

    fun continueRegistration(agreement: Boolean, email: String, password: String, confirmedPassword: String) {
        // TODO: спросить у дизайнера нужен ли progress bar
        var errorMessage: String? = ""
        if (!VALID_PASSVORD_REGEX.matcher(password).find()) {
            errorMessage += "Пароль должен содержать " +
                    "минимум одну кириллическую букву, " +
                    "одну цифру, " +
                    "иметь длину не менее 6 символов.\n"
            viewState.showPasswordErrorMessage(
                "Пароль должен содержать:\n" +
                        "• минимум одну кириллическую букву\n" +
                        "• одну цифру\n" +
                        "• иметь длину не менее 6 символов."
            )
        }
        if (password != confirmedPassword) {
            errorMessage += "Пароли не совпадают.\n"
            viewState.showPasswordConfirmErrorMessage("Пароли не совпадают.\n")
        }

        if (email.isEmpty()) {
            errorMessage += "Поле с email не может быть пустым."
            viewState.showEmailErrorMessage("Поле с email не может быть пустым.")
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            errorMessage += "Неправильно заполнен email."
            viewState.showEmailErrorMessage("Неправильно заполнен email.")
        }

        if (!errorMessage.isNullOrBlank()) {
            viewState.showMessage(errorMessage)
            return
        }
        loginRepository
            .signup(agreement, email, password)
            .observeOn(uiScheduler)
            .subscribeWith(SignupObserver()).disposeOnDestroy()
    }

    fun emailEdited() {
        viewState.showEmailErrorMessage(null)
    }

    fun passwordEdited() {
        viewState.showPasswordErrorMessage(null)
    }

    fun passwordConfirmEdited() {
        viewState.showPasswordConfirmErrorMessage(null)
    }

    fun typedEmail(partEmail: String) {
        loginRepository.getEmailHint(partEmail)
            .observeOn(uiScheduler)
            .subscribe { it -> viewState.setEmailHint(it) }
            .disposeOnDestroy()
    }
}