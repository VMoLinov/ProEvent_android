package ru.myproevent.domain.models.providers.internet_access_info

import io.reactivex.Single

interface IInternetAccessInfoProvider {
    fun hasInternetConnection(): Single<Boolean>
}