package ru.myproevent.ui.presenters.contacts.contact_add

import com.github.terrakok.cicerone.Router
import io.reactivex.Single
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import ru.myproevent.R
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.repositories.contacts.IProEventContactsRepository
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import java.util.regex.Pattern
import javax.inject.Inject

private val VALID_EMAIL_ADDRESS_REGEX: Pattern =
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

class ContactAddPresenter(localRouter: Router) : BaseMvpPresenter<ContactAddView>(localRouter) {
    private inner class ContactAddObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            viewState.showMessage(getString(R.string.contact_added))
        }

        override fun onError(error: Throwable) {
            if (error is HttpException && error.code() == 404){
                viewState.showMessage(getString(R.string.you_already_have_contact))
                return
            }
            error.printStackTrace()
            interAccessInfoRepository
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
    lateinit var interAccessInfoRepository: IInternetAccessInfoRepository

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    private var isSearchMode = true

    fun findContact(email: String) {
        val email = email.trim()
        if (!emailIsCorrect(email)) {
            viewState.showMessage(getString(R.string.incorrect_email))
            return
        }

        profilesRepository.searchProfiles(email)
            .observeOn(uiScheduler)
            .subscribe({ profiles ->
                if (profiles.isEmpty()) {
                    viewState.showInvitationForm()
                    return@subscribe
                }

                findRightProfile(email, profiles)
                    .observeOn(uiScheduler)
                    .subscribe({
                        addContact(it!!)
                    }, {
                        if (it is NoSuchElementException) {
                            viewState.showInvitationForm()
                        } else {
                            viewState.showMessage(getString(R.string.error_occurred, it.message))
                        }
                    }).disposeOnDestroy()
            }, {
                viewState.showMessage(getString(R.string.error_occurred, it.message))
            }).disposeOnDestroy()
    }

    private fun emailIsCorrect(email: String): Boolean =
        VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()

    private fun addContact(profile: Profile) {
        contactsRepository
            .addContact(profile.id)
            .observeOn(uiScheduler)
            .subscribeWith(ContactAddObserver())
            .disposeOnDestroy()
    }

    private fun findRightProfile(email: String, profiles: List<Profile>): Single<Profile?> {
        val singles = profiles.map {
            profilesRepository.getProfile(it.id)
        }
        return Single.merge(singles)
            .filter { it.email.equals(email) }
            .singleOrError()
            .subscribeOn(Schedulers.io())
    }

    fun inviteContact(email: String) {
        if (!emailIsCorrect(email)) {
            viewState.showMessage(getString(R.string.incorrect_email))
            return
        }

        loginRepository.inviteUser(email)
            .observeOn(uiScheduler)
            .subscribe({
                viewState.showMessage(getString(R.string.join_invitation_is_sent))
            }, {
                viewState.showMessage(getString(R.string.error_occurred, it.message))
            }).disposeOnDestroy()

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