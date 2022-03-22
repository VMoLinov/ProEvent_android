package ru.myproevent.ui.adapters.event_items.view_holders

import android.text.Spannable
import android.text.SpannableString
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.ItemNoItemsPlaceholderBinding
import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.INoItemsPlaceholderItemView
import ru.myproevent.ui.views.CenteredImageSpan

class NoItemsPlaceholderViewHolder(
    private val binding: ItemNoItemsPlaceholderBinding,
    private val presenter: IItemPresenter<INoItemsPlaceholderItemView>
) :
    RecyclerView.ViewHolder(binding.root),
    INoItemsPlaceholderItemView {

    init {
        itemView.setOnClickListener {
            presenter.onItemClick(
                this
            )
        }
    }

    override var pos = -1

    override fun setDescription(text: String, spanImageRes: Int, spanImagePos: Int) = with(binding) {
        setImageSpan(noItems, text, spanImageRes, spanImagePos)
    }

    private fun setImageSpan(view: TextView, text: String, iconRes: Int, position: Int) {
        val span: Spannable = SpannableString(text)
        val image = CenteredImageSpan(
            ProEventApp.instance.applicationContext,
            iconRes
        )
        span.setSpan(image, position, position+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        view.text = span
    }
}