package ru.myproevent.ui.adapters.event_items

enum class EVENT_SCREEN_ITEM_ID {
    /**
     * NO_ID используется для элементов которые генерируются динамически
     * (то есть те элементы существование которых зависит от данных, например элементы участников.
     * Статические же элементы, например название мероприятия, есть на экране при любых условиях)
     */
    NO_ID,
    EVENT_PICTURE,
    EVENT_NAME,
    LOCATION,
    DATES_HEADER,
    DESCRIPTION_HEADER,
    MAPS_HEADER,
    POINTS_HEADER,
    PARTICIPANTS_HEADER
}