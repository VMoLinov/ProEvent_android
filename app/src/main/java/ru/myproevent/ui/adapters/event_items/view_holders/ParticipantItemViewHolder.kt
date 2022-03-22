package ru.myproevent.ui.adapters.event_items.view_holders

import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.databinding.ItemContactBinding
import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IParticipantItemView

class ParticipantItemViewHolder(
    private val binding: ItemContactBinding,
    private val presenter: IItemPresenter<IParticipantItemView>
) :
    RecyclerView.ViewHolder(binding.root),
    IParticipantItemView {

    init {
        itemView.setOnClickListener {
            presenter.onItemClick(
                this
            )
        }
    }

    override var pos = -1

    override fun setName(name: String) = with(binding) {
        tvName.text = name
    }

    override fun setStatus(status: String) = with(binding) {
        tvDescription.text = status
    }
}