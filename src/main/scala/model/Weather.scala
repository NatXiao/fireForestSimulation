package model

sealed trait Weather
object Weather {
  case object Sunny extends Weather
  case object Rainy extends Weather
  case object Stormy extends Weather

}
