package ru.myproevent.ui.adapters.event_items.presenter_interfaces

import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.*

interface IEventScreenListPresenter {
    val proeventProfilePictureFormItemPresenter: IItemPresenter<IProeventProfilePictureItemView>
    val proeventTextFormItemPresenter: IItemPresenter<IProeventTextFormItemView>
    val proeventFormsHeaderItemPresenter: IFormsHeaderItemPresenter
    val participantItemPresenter: IItemPresenter<IParticipantItemView>
    val eventDateItemPresenter: IEventDateItemPresenter
    val noItemsPlaceholderItemPresenter: IItemPresenter<INoItemsPlaceholderItemView>
    val textBoxPresenter: IItemPresenter<ITextBoxItemView>
    fun getCount(): Int
    fun getType(position: Int): Int
}
