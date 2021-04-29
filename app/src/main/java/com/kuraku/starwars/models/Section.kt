package com.kuraku.starwars.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class Section (
    var type: String,
    var name: String?,
    var url: String
): Detailable {
    // This can be removed once we support all the different endpoints.
    // Currently a catch-all of the various names used for lists throughout
    val species: List<String>? = null
    val starships: List<String>? = null
    val vehicles: List<String>? = null
    val characters: List<String>? = null
    val pilots: List<String>? = null
    val people: List<String>? = null
    val residents: List<String>? = null
}