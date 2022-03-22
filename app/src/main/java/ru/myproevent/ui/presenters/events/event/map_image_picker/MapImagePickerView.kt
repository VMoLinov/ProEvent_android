package ru.myproevent.ui.presenters.events.event.map_image_picker

import android.net.Uri
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface MapImagePickerView : BaseMvpView {
    fun initImageCropper(uri: Uri)
}