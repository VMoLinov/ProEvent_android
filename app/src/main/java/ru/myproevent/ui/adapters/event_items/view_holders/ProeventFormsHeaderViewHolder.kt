package ru.myproevent.ui.adapters.event_items.view_holders

import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.R
import ru.myproevent.databinding.ItemProeventFormsHeaderBinding
import ru.myproevent.ui.adapters.event_items.EVENT_SCREEN_ITEM_ID
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IFormsHeaderItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventFormsHeaderItemView

class ProeventFormsHeaderViewHolder(
    private val binding: ItemProeventFormsHeaderBinding,
    private val presenter: IFormsHeaderItemPresenter
) :
    RecyclerView.ViewHolder(binding.root),
    IProeventFormsHeaderItemView {
    override var pos = -1

    override lateinit var itemId: EVENT_SCREEN_ITEM_ID

    init {
        with(binding.formsHeader) {
            onExpandClickListener = {
                presenter.onItemClick(
                    this@ProeventFormsHeaderViewHolder
                )
            }
            onEditItemsClickListener = {
                presenter.onEditClick(this@ProeventFormsHeaderViewHolder)
            }
        }
    }

    override fun setTitle(title: String) = with(binding) {
        formsHeader.title = title
    }

    override fun setExpandState(isExpanded: Boolean) = with(binding) {
        formsHeader.isExpanded = isExpanded
    }

    override fun setEditOptionIcon(editIcon: Int?) = with(binding) {
        formsHeader.setEditIcon(editIcon)
    }
}