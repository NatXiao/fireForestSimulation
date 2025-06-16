package simulation

import config.SimulationParameters
import model.{CellState, Grid, Weather}
import rules._

object SimulationRunner {
  def runStep(grid: Grid, params: SimulationParameters, currentWeather: Weather): Grid = {
    grid.mapCells { cell =>

      val southExposed = HumidityRule.isSouthExposed(cell, grid)
      val adjustedHumidity = HumidityRule.updateHumidity(cell, currentWeather, southExposed, params.rainfallPrecipitation)
      val updatedCell = cell.copy(humidity = adjustedHumidity)

      updatedCell.state match {
        case CellState.Fire =>
          if (ExtinctionRule.survives(updatedCell)) updatedCell.copy(state = CellState.Tree)
          else updatedCell.copy(state = CellState.Soil)

        case CellState.Tree =>
          val ignition = IgnitionRules.canIgnite(updatedCell, currentWeather) ||
            FirePropagationRule.shouldCatchFire(updatedCell, grid, params)
          if (ignition) updatedCell.copy(state = CellState.Fire)
          else updatedCell

        case CellState.Soil =>
          if (RegrowthRule.canRegrow(updatedCell, params.regrowTree)) {
            println("tree grow!")
            updatedCell.copy(state = CellState.Tree)
          } else updatedCell

        case _ => updatedCell
      }
    }
  }
}
