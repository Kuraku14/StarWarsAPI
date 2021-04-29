package com.kuraku.starwars.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class Person (
  var name: String,
  var url: String,
  val gender: String,
  val hair_color: String,
  val eye_color: String,
  val birth_year: String,
  val mass: String,
  val skin_color: String,
  val height: String,
  val homeworld: String,
  val species: List<String>,
  val starships: List<String>,
  val vehicles: List<String>
): Detailable