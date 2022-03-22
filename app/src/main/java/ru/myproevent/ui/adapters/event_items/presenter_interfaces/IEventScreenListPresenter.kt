package ru.myproevent.ui.adapters.event_items.presenter_interfaces

import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.*

interface IEventScreenListPresenter {
    val profilePictureFormItemPresenter: IItemPresenter<IProeventProfilePictureItemView>
    val textFormItemPresenter: IItemPresenter<IProeventTextFormItemView>
    val formsHeaderItemPresenter: IFormsHeaderItemPresenter
    val participantItemPresenter: IItemPresenter<IParticipantItemView>
    val eventDateItemPresenter: IEventDateItemPresenter
    val noItemsPlaceholderItemPresenter: IItemPresenter<INoItemsPlaceholderItemView>
    val textBoxPresenter: IItemPresenter<ITextBoxItemView>
    fun getCount(): Int
    fun getType(position: Int): Int
}
