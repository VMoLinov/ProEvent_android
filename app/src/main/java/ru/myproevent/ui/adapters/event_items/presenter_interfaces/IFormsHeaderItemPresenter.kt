package ru.myproevent.ui.adapters.event_items.presenter_interfaces

import ru.myproevent.ui.adapters.event_items.EVENT_SCREEN_ITEM_ID
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventFormsHeaderItemView
import ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.ItemPresenter

abstract class IFormsHeaderItemPresenter : ItemPresenter<IProeventFormsHeaderItemView>() {
    abstract class AbsoluteFormsHeaderPresenter {
        private var currAbsoluteFormsHeaderId: EVENT_SCREEN_ITEM_ID? = null

        fun updateAbsoluteFormsHeader(){
            currAbsoluteFormsHeaderId?.let { showAbsoluteFormsHeader(it) }
        }

        open fun showAbsoluteFormsHeader(currAbsoluteFormsHeaderId: EVENT_SCREEN_ITEM_ID) {
            this.currAbsoluteFormsHeaderId = currAbsoluteFormsHeaderId
        }

        open fun hideAbsoluteFormsHeader() {
            this.currAbsoluteFormsHeaderId = null
        }
    }

    abstract val setOfExpandedItems: MutableSet<EVENT_SCREEN_ITEM_ID>
    abstract val absoluteFormsHeaderPresenter: AbsoluteFormsHeaderPresenter
    abstract fun onFirstVisibleItemPositionChangeListener(position: Int)
    abstract fun onEditClick(view: IProeventFormsHeaderItemView)
}