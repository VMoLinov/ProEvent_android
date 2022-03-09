package ru.myproevent.ui.presenters.events.event

import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.ui.presenters.BaseMvpView
import java.text.FieldPosition

@AddToEnd
interface EventView : BaseMvpView {
    fun addParticipantItemView(profile: Profile)
    fun addDateItemView(timeInterval: TimeInterval, position: Int)
    fun enableDescriptionEdit()
    fun expandDescription()
    fun expandMaps()
    fun expandPoints()
    fun expandParticipants()
    fun expandDates()
    fun clearDates()
    fun clearParticipants()
    fun showAbsoluteFormsHeader(
        title: String,
        editIcon: Int?,
        editIconTint: Int?,
        onCollapse: () -> Unit,
        onCollapseScrollToPosition: Int,
        onEdit: () -> Unit
    )

    fun hideAbsoluteBar()
    fun unlockNameEdit()
    fun unlockLocationEdit()

    @OneExecution
    fun cancelEdit()
    fun showEditOptions()
    fun hideEditOptions()
    fun enableSaveOptions()
    fun disableSaveOptions()
    fun enableActionOptions()
    fun showDateEditOptions(position: Int)
    fun hideDateEditOptions()
    fun lockEdit()
    fun removeParticipant(id: Long, pickedParticipantsIds: List<Long>)
    fun removeDate(date: TimeInterval, pickedDates: List<TimeInterval>)

    fun init()
    fun updateEventScreenList()
    fun eventScreenListNotifyItemRangeInserted(positionStart: Int, itemCount: Int)
    fun eventScreenListNotifyItemRangeRemoved(positionStart: Int, itemCount: Int)

    @OneExecution
    fun hideKeyboard()
}
