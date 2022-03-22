package ru.myproevent.ui.adapters.event_items

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IFormsHeaderItemPresenter

class FirstVisibleItemPositionChangeWatcher {
    private var lastFirstVisiblePosition = -1

    fun init(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, recyclerView: RecyclerView, headerPresenter: IFormsHeaderItemPresenter){
        val manager = recyclerView.layoutManager
        if (manager is LinearLayoutManager && adapter.itemCount > 0) {
            val llm = manager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visiblePosition = llm.findFirstVisibleItemPosition()
                    if (visiblePosition > -1 && visiblePosition != lastFirstVisiblePosition) {
                        headerPresenter.onFirstVisibleItemPositionChangeListener(visiblePosition)
                        lastFirstVisiblePosition = visiblePosition
                    }
                }
            })
        }
    }
}