package ru.myproevent.ui.presenters.events.event.map_image_picker.map_creator

import android.os.Bundle
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface MapCreatorView : BaseMvpView {
    fun setResult(requestKey: String, result: Bundle)
}