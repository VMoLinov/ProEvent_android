package ru.myproevent.ui.presenters.events.event.map_image_picker.map_creator

import android.os.Bundle
import com.github.terrakok.cicerone.Router
import ru.myproevent.domain.models.repositories.event_maps.IEventMapsRepository
import ru.myproevent.domain.models.repositories.images.IImagesRepository
import ru.myproevent.domain.utils.CONTACTS_KEY
import ru.myproevent.domain.utils.MAP_ID_KEY
import ru.myproevent.domain.utils.MAP_PICKER_RESULT_KEY
import ru.myproevent.domain.utils.PARTICIPANTS_PICKER_RESULT_KEY
import ru.myproevent.ui.presenters.BaseMvpPresenter
import java.io.File
import javax.inject.Inject

class MapCreatorPresenterPresenter(private val mapImageUUID: String, localRouter: Router) :
    BaseMvpPresenter<MapCreatorView>(localRouter) {

    @Inject
    lateinit var eventMapsRepository: IEventMapsRepository

    fun addMap(mapName: String) {
        eventMapsRepository
            .saveEventMap(mapName, mapImageUUID)
            .observeOn(uiScheduler)
            .subscribe({ uuidBody ->
                localRouter.backTo(screens.currentlyOpenEventScreen())
            }, {
                viewState.showMessage("Не удалось создать карту")
            })
            .disposeOnDestroy()
        viewState.setResult(
            MAP_PICKER_RESULT_KEY,
            Bundle().apply { putLong(MAP_ID_KEY, 1) }
        )
    }
}