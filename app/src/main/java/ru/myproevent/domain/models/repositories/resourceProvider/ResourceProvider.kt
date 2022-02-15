package ru.myproevent.domain.models.repositories.resourceProvider

import android.content.res.Resources
import javax.inject.Inject

class ResourceProvider @Inject constructor(private val resources: Resources) : IResourceProvider {
    override fun getString(id: Int) = resources.getString(id)
    override fun getString(id: Int, vararg formatArgs: Any?) = resources.getString(id, *formatArgs)
}