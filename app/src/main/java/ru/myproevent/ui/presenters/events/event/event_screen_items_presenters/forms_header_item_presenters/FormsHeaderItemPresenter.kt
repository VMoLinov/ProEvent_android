package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.forms_header_item_presenters

import ru.myproevent.ui.adapters.event_items.EVENT_SCREEN_ITEM_ID
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IFormsHeaderItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventFormsHeaderItemView
import ru.myproevent.ui.presenters.events.event.EventPresenter
import java.util.*

// TODO: рефакторинг eventScreenItems обернуть в класс, чтобы методы eventScreenItems можно было найти также как и методы других di
class FormsHeaderItemPresenter(
    private val eventPresenter: EventPresenter
) :
    IFormsHeaderItemPresenter() {

    // TODO: отрефакторить: сделать private
    override val setOfExpandedItems = mutableSetOf<EVENT_SCREEN_ITEM_ID>()

    private fun addHeaderItemsToScreenItems(
        headerPosition: Int,
        items: TreeSet<EventScreenItem.ListItem>
    ) {
        eventPresenter.eventScreenListPresenter.eventScreenItems.addAll(headerPosition + 1, items)
    }

    private fun removeHeaderItemsFromScreenItems(headerPosition: Int) {
        eventPresenter.eventScreenListPresenter.eventScreenItems.subList(
            headerPosition + 1,
            headerPosition + 1 + (eventPresenter.eventScreenListPresenter.eventScreenItems[headerPosition] as EventScreenItem.FormsHeader<EventScreenItem.ListItem>).items.size
        ).clear()
    }

    private fun onExpandEvent(headerPosition: Int) {
        eventPresenter.viewState.hideKeyboard()
        with(eventPresenter.eventScreenListPresenter.eventScreenItems[headerPosition] as EventScreenItem.FormsHeader<EventScreenItem.ListItem>) {
            if (!isExpanded) {
                addHeaderItemsToScreenItems(headerPosition, items)
                setOfExpandedItems.add(eventPresenter.eventScreenListPresenter.eventScreenItems[headerPosition].itemId)
                //viewState.eventScreenListNotifyItemRangeInserted(headerPosition + 1, items.size)
            } else {
                removeHeaderItemsFromScreenItems(headerPosition)
                setOfExpandedItems.remove(eventPresenter.eventScreenListPresenter.eventScreenItems[headerPosition].itemId)
                //viewState.eventScreenListNotifyItemRangeRemoved(headerPosition + 1, items.size)
            }
            this.isExpanded = !this.isExpanded
        }
        eventPresenter.viewState.updateEventScreenList()
    }

    override val absoluteFormsHeaderPresenter =
        object : IFormsHeaderItemPresenter.AbsoluteFormsHeaderPresenter() {
            override fun showAbsoluteFormsHeader(currAbsoluteFormsHeaderId: EVENT_SCREEN_ITEM_ID) {
                super.showAbsoluteFormsHeader(currAbsoluteFormsHeaderId)
                val header =
                    eventPresenter.eventScreenListPresenter.eventScreenItems.find { item -> item.itemId == currAbsoluteFormsHeaderId } as EventScreenItem.FormsHeader<EventScreenItem.ListItem>
                eventPresenter.viewState.showAbsoluteFormsHeader(title = header.title,
                    editIcon = header.editOptionIcon,
                    editIconTint = null,
                    onCollapse = {
                        onExpandEvent(headerPosition = eventPresenter.eventScreenListPresenter.eventScreenItems.indexOfFirst { it == header })
                        hideAbsoluteFormsHeader()
                    },
                    onCollapseScrollToPosition = eventPresenter.eventScreenListPresenter.eventScreenItems.indexOfFirst { it == header },
                    // TODO: отрефакторить: избавиться от использования headerPosition и передавать вместо этого прямую сслку на header
                    onEdit = { header.presenter!!.onEditOptionClick(eventPresenter.eventScreenListPresenter.eventScreenItems.indexOfFirst { item -> item.itemId == header.itemId }) })
            }

            override fun hideAbsoluteFormsHeader() {
                super.hideAbsoluteFormsHeader()
                eventPresenter.viewState.hideAbsoluteBar()
            }
        }

    override fun onFirstVisibleItemPositionChangeListener(position: Int) {
        with(eventPresenter.eventScreenListPresenter.eventScreenItems[position]) {
            if (this is EventScreenItem.FormsHeader<*> && this.isExpanded) {
                absoluteFormsHeaderPresenter.showAbsoluteFormsHeader(this.itemId)
            } else if (this is EventScreenItem.ListItem && eventPresenter.eventScreenListPresenter.eventScreenItems[position + 1] is EventScreenItem.ListItem) {
                absoluteFormsHeaderPresenter.showAbsoluteFormsHeader(this.header.itemId)
            } else {
                absoluteFormsHeaderPresenter.hideAbsoluteFormsHeader()
            }
        }
    }

    override fun onEditClick(view: IProeventFormsHeaderItemView) {
        (eventPresenter.eventScreenListPresenter.eventScreenItems[view.pos] as EventScreenItem.FormsHeader<*>).presenter!!.onEditOptionClick(
            view.pos
        )
    }

    override fun onItemClick(view: IProeventFormsHeaderItemView) =
        onExpandEvent(headerPosition = eventPresenter.eventScreenListPresenter.eventScreenItems.indexOfFirst { item ->
            if (item is EventScreenItem.FormsHeader<*>) {
                item.itemId == view.itemId
            } else {
                false
            }
        })

    override fun bindView(view: IProeventFormsHeaderItemView) {
        val pos = view.pos
        with(eventPresenter.eventScreenListPresenter.eventScreenItems[pos] as EventScreenItem.FormsHeader<*>) {
            view.itemId = itemId
            view.setTitle(title)
            view.setExpandState(isExpanded)
            view.setEditOptionIcon(editOptionIcon)
        }
    }
}