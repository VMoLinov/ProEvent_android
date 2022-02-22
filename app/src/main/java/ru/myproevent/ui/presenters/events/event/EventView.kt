package ru.myproevent.ui.presenters.events.event

import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.ui.presenters.BaseMvpView

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
    fun showAbsoluteBar(
        title: String,
        iconResource: Int?,
        iconTintResource: Int?,
        onCollapseScroll: Int,
        onCollapse: () -> Unit,
        onEdit: () -> Unit
    )

    fun hideAbsoluteBar()
    fun unlockNameEdit()
    fun unlockLocationEdit()

    @OneExecution
    fun cancelEdit()
    fun showEditOptions()
    fun hideEditOptions()
    fun showActionOptions()
    fun showDateEditOptions(position: Int)
    fun hideDateEditOptions()
    fun lockEdit()
    fun removeParticipant(id: Long, pickedParticipantsIds: List<Long>)
    fun removeDate(date: TimeInterval, pickedDates: List<TimeInterval>)
}
