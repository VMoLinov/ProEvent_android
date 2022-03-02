package ru.myproevent.ui.presenters.settings.security

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.domain.models.Suggestion
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface SecurityView : BaseMvpView {
    fun showProfile(profile: Profile)
    fun makeProfileEditable()
    fun setEmailHint(emailSuggestion: List<Suggestion>)
}