package simulation

import GridGeneration.GridGeneration
import Renderer._
import config._
import model._
import rules.WeatherRule.generateWeatherList

import scala.annotation.tailrec

  object ConsoleMainSimulation extends App {

    val gridGenerationConfig =  GridGenerationConfig(
    width = 20,
    height = 10,
    baseElevation = 0,
    maxElevation = 100,
    treeProbability = 0.2f,
      sinusoidal = true
    )

    val dataURL = "https://data.geo.admin.ch/ch.meteoschweiz.ogd-smn/sio/ogd-smn_sio_d_recent.csv"

    //private val initialGrid = GridGenerator.generateGrid(config)
    private val initialGrid = GridGeneration.generateFlatGrid(gridGenerationConfig)
    private val weatherList = generateWeatherList( // repartition for Sion
      sunnyCount = 71,
      rainyCount = 22,
      stormyCount = 7
    )

    val params = SimulationParameters(
      humanIntervention = false,
      weatherList = weatherList,
      regrowTree = 0.01f ,
      rainfallPrecipitation = 0.08f, //should be between 30 and 140 ==> for phase transition // value mm per meter for a day
      steps = 100
    )

    private val steps = params.steps
    ConsoleElevationRenderer.render(initialGrid)

    @tailrec
    private def simulate(grid: Grid, remaining: Int, weatherSeq : List[Weather]): Unit = {
      val step = steps - remaining
      val currentWeather =  weatherSeq.headOption.getOrElse(Weather.Sunny)

      println("=" * 40 + s" Step $step " + "=" * 40)
      println(s"🌤 Weather: $currentWeather")
      ConsoleRenderer.render(grid)
      println("*" * 20)
      ConsoleHumidityRenderer.render(grid)

      if (remaining > 0) {
        val nextGrid = SimulationRunner.runStep(grid, params, currentWeather)
        simulate(nextGrid, remaining - 1, weatherSeq.drop(1))
      }
    }

    simulate(initialGrid, steps, weatherList)


}
