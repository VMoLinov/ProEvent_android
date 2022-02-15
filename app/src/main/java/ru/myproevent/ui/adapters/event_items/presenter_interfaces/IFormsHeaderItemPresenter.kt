package ru.myproevent.ui.adapters.event_items.presenter_interfaces

import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.EVENT_SCREEN_ITEM_ID
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventFormsHeaderItemView

interface IFormsHeaderItemPresenter : IItemPresenter<IProeventFormsHeaderItemView> {
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

    val absoluteFormsHeaderPresenter: AbsoluteFormsHeaderPresenter
    fun onFirstVisibleItemPositionChangeListener(position: Int)
    fun onEditClick(view: IProeventFormsHeaderItemView)
}