package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters

import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventProfilePictureItemView
import ru.myproevent.ui.presenters.events.event.EventPresenter

class ProfilePictureFormItemPresenter(private val eventPresenter: EventPresenter) :
    ItemPresenter<IProeventProfilePictureItemView>() {
    override fun bindView(view: IProeventProfilePictureItemView) {
        val pos = view.pos
        with(view) {
            with(eventPresenter.eventScreenListPresenter.eventScreenItems[pos] as EventScreenItem.ProfileImageForm) {
                setText(text)
            }
        }
    }
}