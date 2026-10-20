package com.tecsup.lab10.data

import com.google.gson.annotations.SerializedName

data class SerieModel(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String,
    @SerializedName("release_date") val release_date: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("category") val category: String
)