package simulation

import GridGeneration.GridGeneration
import config.{GridGenerationConfig, SimulationParameters}
import model._
import rules.WeatherRule.generateWeatherList

import scala.annotation.tailrec

object VisualSimulationRunner {
  def runFrames(
                 gridConfig: GridGenerationConfig,
                 simParams: SimulationParameters
               ): Seq[(Grid, Weather)] = {

    val initialGrid = GridGeneration.generateGrid(gridConfig)

    @tailrec
    def simulate(grid: Grid, remaining: Int, weatherSeq: List[Weather], frames: Seq[(Grid, Weather)]): Seq[(Grid, Weather)] = {
      if (remaining <= 0 || weatherSeq.isEmpty) frames
      else {
        val currentWeather = weatherSeq.head
        val nextGrid = SimulationRunner.runStep(grid, simParams, currentWeather)
        simulate(nextGrid, remaining - 1, weatherSeq.tail, frames :+ (nextGrid, currentWeather))
      }
    }

    val firstWeather = simParams.weatherList.headOption.getOrElse(Weather.Sunny)
    simulate(initialGrid, simParams.steps, simParams.weatherList, Seq((initialGrid, firstWeather)))
  }
}
