package ru.myproevent.domain.models.entities.contact

enum class Status(val value: String) {
    ALL("ALL"),
    ACCEPTED("ACCEPTED"), // TODO: почему в коде не используется этот статус, его можно удалить?
    DECLINED("DECLINED"),
    PENDING("PENDING"),
    REQUESTED("REQUESTED");

    companion object {
        fun fromString(status: String) = valueOf(status)
    }
}