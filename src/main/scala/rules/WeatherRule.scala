package rules

import model.Weather

object WeatherRule {
  def randomWeather(): Weather = {
    val all = Seq(Weather.Sunny, Weather.Rainy, Weather.Stormy)
    scala.util.Random.shuffle(all).head
  }

  def generateWeatherList(
                           sunnyCount: Int,
                           rainyCount: Int,
                           stormyCount: Int
                         ): List[Weather] = {
    val sunnyDays  = List.fill(sunnyCount)(Weather.Sunny)
    val rainyDays  = List.fill(rainyCount)(Weather.Rainy)
    val stormyDays = List.fill(stormyCount)(Weather.Stormy)

    val allDays = sunnyDays ++ rainyDays ++ stormyDays
    scala.util.Random.shuffle(allDays)
  }


  //def specificWeather(totalDays : Int) : List[Weather]= {

  //}

}
