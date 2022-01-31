package ru.myproevent.ui.presenters.settings.account

import android.net.Uri
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.models.repositories.images.IImagesRepository
import ru.myproevent.domain.models.providers.internet_access_info.IInternetAccessInfoProvider
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class AccountPresenter(localRouter: Router) : BaseMvpPresenter<AccountView>(localRouter) {
    private inner class ProfileEditObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            viewState.showMessage("Изменения сохранены")
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            interAccessInfoProvider
                .hasInternetConnection()
                .observeOn(uiScheduler)
                .subscribeWith(InterAccessInfoObserver("Этого не должно было произойти(ProfileEditObserver):\n${error}"))
                .disposeOnDestroy()
        }
    }

    private inner class ProfileGetObserver : DisposableSingleObserver<ProfileDto>() {
        override fun onSuccess(profileDto: ProfileDto) {
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
            interAccessInfoProvider
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
    lateinit var interAccessInfoProvider: IInternetAccessInfoProvider

    @Inject
    lateinit var imagesRepository: IImagesRepository

    fun saveProfile(
        name: String,
        phone: String,
        dateOfBirth: String,
        position: String,
        role: String,
        newProfilePictureUri: Uri?
    ) {
        profilesRepository
            .saveProfile(
                ProfileDto(
                    userId = loginRepository.getLocalId()!!,
                    fullName = name,
                    msisdn = phone,
                    position = position,
                    birthdate = dateOfBirth,
                    description = role
                ),
                newProfilePictureUri
            )
            .observeOn(uiScheduler)
            .subscribeWith(ProfileEditObserver())
            .disposeOnDestroy()
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
}
