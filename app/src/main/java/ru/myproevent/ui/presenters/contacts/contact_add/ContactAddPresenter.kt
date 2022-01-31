package ru.myproevent.ui.presenters.contacts.contact_add

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import ru.myproevent.domain.models.repositories.contacts.IProEventContactsRepository
import ru.myproevent.domain.models.providers.internet_access_info.IInternetAccessInfoProvider
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class ContactAddPresenter(localRouter: Router) : BaseMvpPresenter<ContactAddView>(localRouter) {
    private inner class ContactAddObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            localRouter.newRootScreen(screens.contacts())
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            interAccessInfoProvider
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver(error.message))
                .disposeOnDestroy()
        }
    }

    @Inject
    lateinit var contactsRepository: IProEventContactsRepository

    @Inject
    lateinit var profilesRepository: IProEventProfilesRepository

    @Inject
    lateinit var interAccessInfoProvider: IInternetAccessInfoProvider

    private var isSearchMode = true

    // TODO: заменить id на email когда на сервере появится функция поиска профиля по email
    fun addContact(id: Long) {
        contactsRepository
            .addContact(id)
            .observeOn(uiScheduler)
            .subscribeWith(ContactAddObserver())
            .disposeOnDestroy()
    }

    override fun onBackPressed(): Boolean {
        return if (!isSearchMode) {
            viewState.showSearchForm()
            false
        } else {
            super.onBackPressed()
        }
    }
}