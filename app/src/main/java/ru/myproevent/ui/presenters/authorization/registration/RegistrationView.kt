package ru.myproevent.ui.presenters.authorization.registration

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.domain.models.Suggestion
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface RegistrationView : BaseMvpView {
    fun showEmailErrorMessage(message: String?)
    fun showPasswordErrorMessage(message: String?)
    fun showPasswordConfirmErrorMessage(message: String?)
    fun setEmailHint(emailSuggestion: List<Suggestion>)
}