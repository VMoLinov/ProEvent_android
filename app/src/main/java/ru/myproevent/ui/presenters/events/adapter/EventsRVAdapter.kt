package ru.myproevent.ui.presenters.events.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.ItemEventBinding
import ru.myproevent.domain.utils.GlideLoader
import ru.myproevent.ui.presenters.events.IEventItemView

class EventsRVAdapter(val presenter: IEventsListPresenter) :
    RecyclerView.Adapter<EventsRVAdapter.ViewHolder>() {

    private val imageLoader =
        GlideLoader().apply { ProEventApp.instance.appComponent.inject(this) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = presenter.getCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        presenter.bindView(holder.apply { pos = position })

    inner class ViewHolder(private val vb: ItemEventBinding) : RecyclerView.ViewHolder(vb.root),
        IEventItemView {

        init {
            itemView.setOnClickListener { presenter.onItemClick(this) }
            vb.ivEditEvent.setOnClickListener { (presenter.onEditButtonClick(this)) }
        }

        override fun setName(name: String) {
            vb.tvEventName.text = name
        }

        override fun setTime(time: String) {
            vb.tvTime.text = time
        }

        override fun loadImg(uuid: String?) {
            /** TODO исправить причину
            в моём случае метод вызывается с position = 4, 6, 7, снова 4, 6, 7
            ожидаемый результат: картинки отображаются только на 5, 7 и 8 элементе списка
            получаемый результат: картинки отображаются на  2, 4, 5, 7 и 8 элементе списка */
            if (uuid.isNullOrEmpty()) {
                vb.ivImg.setImageResource(R.drawable.ic_events_small)
            } else {
                imageLoader.loadCircle(vb.ivImg, uuid)
            }
        }

        override var pos = -1
    }
}
