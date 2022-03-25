package ru.myproevent.ui.presenters.events.event

import android.net.Uri
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.ui.presenters.BaseMvpView
import java.text.FieldPosition

@AddToEnd
interface EventView : BaseMvpView {
    fun showAbsoluteFormsHeader(
        title: String,
        editIcon: Int?,
        editIconTint: Int?,
        onCollapse: () -> Unit,
        onCollapseScrollToPosition: Int,
        onEdit: () -> Unit
    )

    fun hideAbsoluteBar()
    fun showEditOptions()
    fun hideEditOptions()
    fun enableSaveOptions()
    fun disableSaveOptions()
    fun enableActionOptions()

    fun init()
    fun updateEventScreenList()
    fun eventScreenListNotifyItemRangeInserted(positionStart: Int, itemCount: Int)
    fun eventScreenListNotifyItemRangeRemoved(positionStart: Int, itemCount: Int)

    @OneExecution
    fun hideKeyboard()

    @OneExecution
    fun launchImagePicker(pickResultCallback: (Uri?) -> Unit)
}
