package ru.myproevent.ui.presenters.authorization.registration

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.R
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import java.util.regex.Pattern
import javax.inject.Inject

val VALID_EMAIL_ADDRESS_REGEX: Pattern =
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
val VALID_PASSWORD_REGEX: Pattern =
    Pattern.compile("^(?=.*[0-9])(?=.*[а-яё])(?=\\S+\$).{6,}\$", Pattern.CASE_INSENSITIVE)

class RegistrationPresenter(localRouter: Router) : BaseMvpPresenter<RegistrationView>(localRouter) {
    private inner class SignupObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            localRouter.navigateTo(screens.code())
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.HttpException) {
                if (error.code() == 409) {
                    viewState.showMessage(getString(R.string.email_is_already_registered))
                    viewState.showEmailErrorMessage(getString(R.string.email_is_already_registered))
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

    fun continueRegistration(
        agreement: Boolean,
        email: String,
        password: String,
        confirmedPassword: String
    ) {
        // TODO: спросить у дизайнера нужен ли progress bar
        val errorMessage = StringBuilder()
        if (!VALID_PASSWORD_REGEX.matcher(password).find()) {
            errorMessage.append(getString(R.string.password_requirements_01))
            viewState.showPasswordErrorMessage(getString(R.string.password_requirements_02))
        }
        if (password != confirmedPassword) {
            errorMessage.append(getString(R.string.passwords_not_same)).append(".\n")
            viewState.showPasswordConfirmErrorMessage(getString(R.string.passwords_not_same) + ".\n")
        }

        if (email.isEmpty()) {
            errorMessage.append(getString(R.string.email_cant_be_empty))
            viewState.showEmailErrorMessage(getString(R.string.email_cant_be_empty))
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            errorMessage.append(getString(R.string.incorrect_email))
            viewState.showEmailErrorMessage(getString(R.string.incorrect_email))
        }

        if (errorMessage.isNotBlank()) {
            viewState.showMessage(errorMessage.toString())
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