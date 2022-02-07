package ru.myproevent.ui.presenters.settings.account

import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.repositories.images.IImagesRepository
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import java.io.File
import javax.inject.Inject

class AccountPresenter(localRouter: Router) : BaseMvpPresenter<AccountView>(localRouter) {
    private var curProfile: Profile? = null

    private inner class ProfileEditObserver(private var profile: Profile) :
        DisposableCompletableObserver() {
        override fun onComplete() {
            curProfile = profile
            viewState.showProfile(profile)
            viewState.showMessage("Изменения сохранены")
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            interAccessInfoRepository
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver("Этого не должно было произойти(ProfileEditObserver):\n${error}"))
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
            if (error is retrofit2.adapter.rxjava2.HttpException) {
                when (error.code()) {
                    404 -> viewState.makeProfileEditable()
                }
                return
            }
            interAccessInfoRepository
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver("Этого не должно было произойти (ProfileGetObserver):\n${error}"))
                .disposeOnDestroy()
        }
    }

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var profilesRepository: IProEventProfilesRepository

    @Inject
    lateinit var interAccessInfoRepository: IInternetAccessInfoRepository

    @Inject
    lateinit var imagesRepository: IImagesRepository

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
        "http://178.249.69.107:8762/api/v1/storage/$uuid",
        LazyHeaders.Builder()
            .addHeader("Authorization", "Bearer ${loginRepository.getLocalToken()}")
            .build()
    )

    fun cancelEdit() = curProfile?.let { viewState.showProfile(it) }
}
