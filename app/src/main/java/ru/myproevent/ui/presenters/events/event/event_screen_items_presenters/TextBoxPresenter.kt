package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters

import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.ITextBoxItemView
import ru.myproevent.ui.presenters.events.event.EventPresenter

class TextBoxPresenter(private val eventPresenter: EventPresenter) :
    ItemPresenter<ITextBoxItemView>() {
    override fun bindView(view: ITextBoxItemView) = with(view) {
        with(eventPresenter.eventScreenListPresenter.eventScreenItems[view.pos] as EventScreenItem.TextBox) {
            setValue(value)
            setOnValueChangedListener { newValue -> value = newValue }
            setEditLock(isEditLocked)
            if (hasFocusIntent) {
                requestFocus()
                hasFocusIntent = false
            }
        }
    }
}