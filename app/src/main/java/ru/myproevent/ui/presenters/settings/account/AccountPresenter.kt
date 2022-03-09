package ru.myproevent.ui.presenters.settings.account

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import ru.myproevent.R
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.repositories.images.IImagesRepository
import ru.myproevent.domain.models.providers.internet_access_info.IInternetAccessInfoProvider
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.domain.utils.GlideLoader
import ru.myproevent.ui.presenters.BaseMvpPresenter
import java.io.File
import javax.inject.Inject

class AccountPresenter(localRouter: Router) : BaseMvpPresenter<AccountView>(localRouter) {
    private var curProfile: Profile? = null
    private val editedSet = mutableMapOf<Int, Boolean>()

    private inner class ProfileEditObserver(private var profile: Profile) :
        DisposableCompletableObserver() {
        override fun onComplete() {
            curProfile = profile
            editedSet.keys.forEach { editedSet[it] = false }
            viewState.setFieldEdited(editedSet)
            viewState.showProfile(profile)
            editedSet.clear()
            viewState.showMessage(getString(R.string.changes_saved))
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            interAccessInfoProvider
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(
                    InterAccessInfoObserver(getString(R.string.impossible_error_02, error))
                )
                .disposeOnDestroy()
        }
    }

    private inner class ProfileGetObserver : DisposableSingleObserver<Profile>() {
        override fun onSuccess(profileDto: Profile) {
            curProfile = profileDto
            viewState.showProfile(profileDto)
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            if (error is retrofit2.HttpException) {
                when (error.code()) {
                    404 -> viewState.makeProfileEditable()
                }
                return
            }
            interAccessInfoProvider
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(
                    InterAccessInfoObserver(getString(R.string.impossible_error_03, error))
                )
                .disposeOnDestroy()
        }
    }

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var profilesRepository: IProEventProfilesRepository

    @Inject
    lateinit var interAccessInfoProvider: IInternetAccessInfoProvider

    @Inject
    lateinit var imagesRepository: IImagesRepository

    override fun onFirstViewAttach() {
        getProfile()
    }

    fun saveProfile(
        name: String,
        phone: String,
        dateOfBirth: String,
        position: String,
        role: String,
        uuid: String?
    ) {
        val profile = Profile(
            id = loginRepository.getLocalId()!!,
            fullName = name,
            phone = phone,
            position = position,
            birthdate = dateOfBirth,
            description = role,
            imgUri = uuid
        )
        profilesRepository
            .saveProfile(profile)
            .observeOn(uiScheduler)
            .subscribeWith(ProfileEditObserver(profile))
            .disposeOnDestroy()
    }

    fun saveImage(file: File, callback: ((String?) -> Unit)? = null) {
        imagesRepository
            .saveImage(file)
            .observeOn(uiScheduler)
            .subscribe({
                callback?.invoke(it.uuid)
            }, {
                callback?.invoke(null)
            })
            .disposeOnDestroy()
    }

    fun deleteImage(uuid: String) {
        imagesRepository.deleteImage(uuid).subscribe().disposeOnDestroy()
    }

    fun getProfile() {
        profilesRepository
            .getProfile(loginRepository.getLocalId()!!)
            .observeOn(uiScheduler)
            .subscribeWith(ProfileGetObserver())
            .disposeOnDestroy()
    }

    // TODO: вынести URL в ресурсы или константы
    fun getGlideUrl(uuid: String) = GlideUrl(
        GlideLoader.URL_PATH + uuid,
        LazyHeaders.Builder()
            .addHeader("Authorization", "Bearer ${loginRepository.getLocalToken()}")
            .build()
    )

    fun cancelEdit() = curProfile?.let { viewState.showProfile(it) }

    fun clickOnEditIcon(id: Int) {
        editedSet[id] = true
        viewState.setFieldEdited(editedSet)
    }
}
