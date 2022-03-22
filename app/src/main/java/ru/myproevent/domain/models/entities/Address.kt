package ru.myproevent.domain.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Address(
    val latitude: Double,
    val longitude: Double,
    val addressLine: String
) : Parcelable {
    companion object // Данный companion object нужен для extension функции ru.myproevent.domain.utils Address.Companion.fromString
}