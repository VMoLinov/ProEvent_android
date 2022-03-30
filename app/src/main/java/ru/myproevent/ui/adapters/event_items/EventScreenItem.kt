package ru.myproevent.ui.adapters.event_items

import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.forms_header_item_presenters.IHeaderPresenter
import java.util.*

/**
 * @param itemId может использоваться для поиска конкретного EventScreenItem в списке.
 *        Однако если есть возможность получить EventScreenItem из списка по позиции,
 *        то для экономии вычислительных ресурсов следует использовать именно позицию.
 *
 *        Почему val itemId open: https://stackoverflow.com/a/44129826
 */

// TODO: отрефакторить: itemId используется чтобы найти статический элемент(тот который есть на экране при любых условиях).
//                     Возможно стоит избавиться от itemId и находить статические элементы просто по ссылке(сравнивания ссылокы объектов, передавая в .find экземпляр объекта)?
sealed class EventScreenItem(open val itemId: EVENT_SCREEN_ITEM_ID, val type: ItemType) {
    data class ProfileImageForm(override val itemId: EVENT_SCREEN_ITEM_ID, val text: String) :
        EventScreenItem(itemId, ItemType.PROFILE_PICTURE_FORM)

    data class TextForm(
        override val itemId: EVENT_SCREEN_ITEM_ID,
        val title: String,
        val hint: String,
        var value: String,
        override var isEditLocked: Boolean,
        var isEditOptionAvailable: Boolean
    ) : EventScreenItem(itemId, ItemType.TEXT_FORM), LockableEdit

    data class FormsHeader<V : ListItem>(
        override val itemId: EVENT_SCREEN_ITEM_ID,
        val title: String,
        var isExpanded: Boolean,
        val items: TreeSet<V>,
        var editOptionIcon: Int?,
        var presenter: IHeaderPresenter? = null
    ) : EventScreenItem(itemId, ItemType.FORM_HEADER)

    data class ParticipantItem(
        val participantId: Long,
        override val header: FormsHeader<ListItem>
    ) : ListItem(ItemType.PARTICIPANT_ITEM) {
        override fun compareTo(other: ListItem) = when (other) {
            is ParticipantItem -> {
                // TODO: поменять, добавить поле с именем и если имени нет то сравнивать id
                (participantId - other.participantId).toInt()
            }
            else -> throw RuntimeException("Отсутствует операция сравнения ParticipantItem-а и переданного compareTo аргумента")
        }

    }

    data class EventDateItem(
        val timeInterval: TimeInterval,
        override val header: FormsHeader<ListItem>
    ) : ListItem(ItemType.EVENT_DATE_ITEM) {
        override fun compareTo(other: ListItem) = when (other) {
            is EventDateItem -> timeInterval.compareTo(other.timeInterval)
            else -> throw RuntimeException("Отсутствует операция сравнения EventDateItem-а и переданного compareTo аргумента")
        }
    }

    data class NoItemsPlaceholder(
        val description: String,
        val spanImageRes: Int,
        val spanImagePos: Int,
        override val header: FormsHeader<ListItem>
    ) : ListItem(ItemType.NO_ITEMS_PLACEHOLDER) {
        override fun compareTo(other: ListItem) = when (other) {
            is NoItemsPlaceholder -> 0 // TODO: я не понял зачем, но похоже даже если в TreeSet нет элементов, при добавлении первого элемента через .add всё равно вызывается compareTo(видимо сам с собой).
            //       Поэтому реализованно это по сути не нужное сравнение.
            else -> throw RuntimeException("Отсутствует операция сравнения NoItemsPlaceholder-а и переданного compareTo аргумента")
        }
    }

    data class TextBox(
        var value: String,
        override val header: FormsHeader<ListItem>,
        override var isEditLocked: Boolean,
        var hasFocusIntent: Boolean
    ) : ListItem(ItemType.TEXT_BOX), LockableEdit {
        override fun compareTo(other: ListItem) = when (other) {
            is TextBox -> 0 // TODO: я не понял зачем, но похоже даже если в TreeSet нет элементов, при добавлении первого элемента через .add всё равно вызывается compareTo(видимо сам с собой).
            //       Поэтому реализованно это по сути не нужное сравнение.
            else -> throw RuntimeException("Отсутствует операция сравнения TextBox-а и переданного compareTo аргумента")
        }
    }

    // TODO: рефакторинг: попробовать избавиться от ItemType и вместо этого преобразовывать идентификатор класса в число и обратно или как-то использовать для этого дженерики?
    //                    Проблема текущего подхода в том что при определении класса можно указать неправильный ItemType
    enum class ItemType {
        PROFILE_PICTURE_FORM,
        TEXT_FORM,
        FORM_HEADER,
        PARTICIPANT_ITEM,
        EVENT_DATE_ITEM,
        NO_ITEMS_PLACEHOLDER,
        TEXT_BOX
    }

    interface LockableEdit {
        var isEditLocked: Boolean
    }

    abstract class ListItem(type: ItemType) : EventScreenItem(EVENT_SCREEN_ITEM_ID.NO_ID, type),
        Comparable<ListItem> {
        abstract val header: FormsHeader<ListItem>
    }
}