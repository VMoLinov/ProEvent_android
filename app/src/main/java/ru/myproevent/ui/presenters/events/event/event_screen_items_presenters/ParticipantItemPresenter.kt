package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters

import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IParticipantItemView
import ru.myproevent.ui.presenters.events.event.EventPresenter

//TODO: отрефакторить: получать аргументы конструктора через di?
class ParticipantItemPresenter(
    private val eventPresenter: EventPresenter
) : ItemPresenter<IParticipantItemView>() {

    val participantProfiles = mutableMapOf<Long, Profile>()

    override fun onItemClick(view: IParticipantItemView) {
        val profile =
            participantProfiles[(eventPresenter.eventScreenListPresenter.eventScreenItems[view.pos] as EventScreenItem.ParticipantItem).participantId]!!
        eventPresenter.localRouter.navigateTo(eventPresenter.screens.eventParticipant(profile))
    }

    override fun bindView(view: IParticipantItemView) {
        val pos = view.pos
        with(view) {
            with(eventPresenter.eventScreenListPresenter.eventScreenItems[pos] as EventScreenItem.ParticipantItem) {
                participantProfiles[participantId]?.let {
                    setName(if (!it.fullName.isNullOrBlank()) it.fullName!! else if (!it.nickName.isNullOrBlank()) it.nickName!! else if (!it.email.isNullOrBlank()) it.email!! else "#$participantId")
                    setDescription(if (!it.description.isNullOrBlank()) it.description!! else if (!it.nickName.isNullOrBlank()) it.nickName!! else if (!it.email.isNullOrBlank()) it.email!! else "id пользователя: $participantId")
                    setStatus(it.deleted)
                } ?: run {
                    setName("[LOADING]")
                    setDescription("[LOADING]")
                }
            }
        }
    }
}