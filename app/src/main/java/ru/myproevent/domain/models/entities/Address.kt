package ru.myproevent.domain.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Address(
    val latitude: Double,
    val longitude: Double,
    val addressLine: String
) : Parcelable {
    companion object
}