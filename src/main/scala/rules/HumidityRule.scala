package rules

import model.{Cell, Grid, Weather}

import scala.util.Random

object HumidityRule {

  def isSouthExposed(cell: Cell, grid: Grid): Boolean = {
    grid.get(cell.x, cell.y + 1).exists(_.z < cell.z)
  }

  def updateHumidity(cell: Cell, weather: Weather, southExposed: Boolean, precipitation : Float): Float = {
    val baseLoss = weather match {
      case Weather.Sunny =>  Random.nextFloat() * (0.08 - 0.03) + 0.03 //Random.between(0.03f, 0.08f)
      case Weather.Stormy => -0.005f
      case Weather.Rainy => -precipitation*2
      case _ => 0.0f
    }
    val exposureMultiplier = if (southExposed && weather == Weather.Sunny) 1.1f else 1.0f
    val humidityChange = baseLoss * exposureMultiplier

    val result = (cell.humidity - humidityChange).toFloat
    if (result < 0.00f) 0.00f
    else if (result > 1.00f) 1.00f
    else result
  }


  implicit class FloatOps(f: Float) {
    def clamp(min: Float, max: Float): Float = math.max(min, math.min(max, f))
  }
}
