package rules

import model.Weather.{Stormy, Sunny}
import model.{Cell, CellState, Grid, Weather}

object IgnitionRules {
  def canIgnite(cell : Cell, weather : Weather): Boolean = {
    (weather, cell.humidity) match {
      case (Sunny, h) if h <= 0.07 => true
      case (Stormy, h) if h <= 0.2 && scala.util.Random.nextDouble() < 0.3 => true
      case _ => false
    }
  }

}
