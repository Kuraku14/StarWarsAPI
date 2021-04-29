package com.kuraku.starwars.models

/**
 * A list of Person, with meta data
 */
data class People (
  val count: Int,
  val next: String?,
  val previous: String?,
  val results: ArrayList<Person>
)