package com.wasabi.deadly.rxkotlin.spendings

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Spending(
    val id: Int,
    val title: String,
    val description: String,
    val value: Double
) : Parcelable {

    fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result["id"] = id
        result["title"] = title
        result["description"] = description
        result["value"] = value
        return result
    }

}