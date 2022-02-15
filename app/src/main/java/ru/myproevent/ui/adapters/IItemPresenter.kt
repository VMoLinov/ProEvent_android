package ru.myproevent.ui.adapters

interface IItemPresenter<V> {
    fun onItemClick(view: V)
    fun bindView(view: V)
}