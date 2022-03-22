package ru.myproevent.ui.presenters.events.event.map_image_picker

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.github.terrakok.cicerone.Router
import ru.myproevent.ProEventApp
import ru.myproevent.domain.models.repositories.images.IImagesRepository
import ru.myproevent.domain.utils.temp.Cache
import ru.myproevent.ui.presenters.BaseMvpPresenter
import java.io.File
import javax.inject.Inject

class MapImagePickerPresenter(private val uri: Uri, localRouter: Router) :
    BaseMvpPresenter<MapImagePickerView>(localRouter) {

    @Inject
    lateinit var imagesRepository: IImagesRepository

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.initImageCropper(uri)
    }

    fun saveMapImage(){

        val cacheUri = Cache().saveToCacheAndGetUri(MediaStore.Images.Media.getBitmap(ProEventApp.instance.contentResolver, uri))
        val path = cacheUri.path.orEmpty()
        Log.d("[ERROR]", "path: $path")

        imagesRepository
            .saveImage(File(path))
            .observeOn(uiScheduler)
            .subscribe({ uuidBody ->
                localRouter.navigateTo(screens.mapCreator(uuidBody.uuid))
            }, {
                viewState.showMessage("Не удалось сохранить изображение: $it")
            })
            .disposeOnDestroy()
    }
}