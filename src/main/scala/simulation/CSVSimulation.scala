package simulation

import GridGeneration.{FlatGrid, FlatGridconfig}
import config._
import model._
import Renderer._
import rules.WeatherRule.generateWeatherList

object CSVSimulation {

  def simulateAndCollect(initialGrid: Grid, params: SimulationParameters): List[(Int, Double)] = {
    val steps = params.simulationStep
    val weatherSeq = params.weatherList

    @scala.annotation.tailrec
    def loop(grid: Grid, remaining: Int, weatherSeq: List[Weather], acc: List[(Int, Double)]): List[(Int, Double)] = {
      val step = steps - remaining
      val currentWeather = weatherSeq.headOption.getOrElse(Weather.Sunny)

      // Compute % burned area (nb Soil cells / total nb Tree+Soil+Fire)
      val allCells = grid.cells.flatten
      val totalRelevantCells = allCells.count(c => c.state == CellState.Tree || c.state == CellState.Fire || c.state == CellState.Soil)
      val burnedCells = allCells.count(c => c.state == CellState.Soil)

      val burnedRatio = if (totalRelevantCells > 0) burnedCells.toDouble / totalRelevantCells else 0.0

      println("=" * 40 + s" Step $step " + "=" * 40)
      println(s"🌤 Weather: $currentWeather")
      ConsoleRenderer.render(grid)
      println("*" * 20)
      ConsoleHumidityRenderer.render(grid)
      println(f">> Burned ratio: $burnedRatio%.2f")

      if (remaining > 0) {
        val nextGrid = Simulation.runStep(grid, params, currentWeather)
        loop(nextGrid, remaining - 1, weatherSeq.drop(1), acc :+ (step, burnedRatio))
      } else {
        acc
      }
    }

    val initialResult = List[(Int, Double)]()
    loop(initialGrid, steps, weatherSeq, initialResult)
  }

}
