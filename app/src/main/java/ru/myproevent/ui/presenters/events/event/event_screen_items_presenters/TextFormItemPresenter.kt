package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters

import android.util.Log
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventTextFormItemView
import ru.myproevent.ui.presenters.events.event.EventPresenter

// TODO: учточнить можно ли так передавать viewState?
class TextFormItemPresenter(
    private val eventPresenter: EventPresenter
) : ItemPresenter<IProeventTextFormItemView>() {
    override fun bindView(view: IProeventTextFormItemView) {
        val pos = view.pos
        with(view) {
            with(eventPresenter.eventScreenListPresenter.eventScreenItems[pos] as EventScreenItem.TextForm) {
                setTitle(title)
                setHint(hint)
                setValue(value)
                Log.d(
                    "[MYLOG]",
                    "pos: $pos; setValue: $value; eventScreenItems[pos].itemId: ${eventPresenter.eventScreenListPresenter.eventScreenItems[pos].itemId}"
                )
                setEditOption(isEditOptionAvailable)
                setEditLock(isEditLocked)
                setOnEditUnlockListener {
                    isEditLocked = false
                    eventPresenter.viewState.showEditOptions()
                }
                setOnEditOptionHideListener {
                    isEditOptionAvailable = false
                }
                setOnValueChangedListener { newValue ->
                    Log.d(
                        "[MYLOG]",
                        "eventScreenItems[pos].itemId: ${eventPresenter.eventScreenListPresenter.eventScreenItems[pos].itemId} setOnValueChangedListener newValue: $newValue"
                    )
                    value = newValue
                }
            }
        }
    }
}