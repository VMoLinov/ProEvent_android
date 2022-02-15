package ru.myproevent.ui.presenters.contacts.contact

import com.github.terrakok.cicerone.Router
import ru.myproevent.R
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class ContactPresenter(localRouter: Router) : BaseMvpPresenter<ContactView>(localRouter) {

    @Inject
    lateinit var profilesRepository: IProEventProfilesRepository

    fun loadProfile(id: Long) {
        profilesRepository.getProfile(id)
            .observeOn(uiScheduler)
            .subscribe({ profile ->
                fillFields(profile!!)
            }, {
                viewState.showMessage(getString(R.string.error_occurred, it.message))
            }).disposeOnDestroy()
    }

    private fun fillFields(profile: Profile) = with(profile) {
        val title = when {
            !fullName.isNullOrBlank() -> fullName!!
            !nickName.isNullOrBlank() -> nickName!!
            else -> getString(R.string.id_01, id)
        }
        viewState.setTitle(title)

        if (!birthdate.isNullOrBlank()) viewState.setBirthDate(birthdate!!)
        if (!position.isNullOrBlank()) viewState.setPosition(position!!)
        if (!phone.isNullOrBlank()) viewState.setPhone(phone!!)
    }
}