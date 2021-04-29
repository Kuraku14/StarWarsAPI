package com.kuraku.starwars.models

/**
 * Image associated with a Person
 *
 * @property id Person id
 * @property image Image url
 */
data class PersonImage (
  val id: Int,
  val image: String?
)