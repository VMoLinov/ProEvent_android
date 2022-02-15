package ru.myproevent.ui.adapters.event_items.view_holders

import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.databinding.ItemProfilePictureBinding
import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventProfilePictureItemView

class ProeventProfilePictureFormViewHolder(
    private val binding: ItemProfilePictureBinding,
    private val presenter: IItemPresenter<IProeventProfilePictureItemView>
) :
    RecyclerView.ViewHolder(binding.root),
    IProeventProfilePictureItemView {

    init {
        itemView.setOnClickListener {
            presenter.onItemClick(
                this
            )
        }
    }

    override var pos = -1

    override fun setText(text: String) = with(binding) {
        editEventImage.text = text
    }
}