package ru.myproevent.domain.models.repositories.resourceProvider

interface IResourceProvider {
    fun getString(id: Int): String
    fun getString(id: Int, vararg formatArgs: Any?): String
}
