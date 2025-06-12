import config._
import model._
import GridGeneration.{FlatGrid, FlatGridconfig}
import rules.WeatherRule.generateWeatherList
import simulation.{ CSVSimulation}

import java.io.PrintWriter

object SweepMain extends App {

  val gridGenerationConfig = FlatGridconfig(
    width = 20,
    heigth = 10,
    baseElevation = 1000,
    maxElevation = 0,
    treeProbability = 0.2f,
    sinusoidal = true
  )

  val weatherList = generateWeatherList( // repartition for Sion
    sunnyCount = 71,
    rainyCount = 22,
    stormyCount = 7
  )
  val simulationStep = 100

  val rainfallValues = (0 to 100).map(i => 0.3 + i * (1.4 - 0.3) / 100.0) // 101 values from 0.3 to 1.4

  val outputPath = "results/sweepresult_sinus2.csv"
  val writer = new PrintWriter(outputPath)
  writer.println("RunIndex,RainfallPrecipitation,Step,BurnedRatio")

  var runIndex = 0

  for (rainfall <- rainfallValues) {

    println(s"Running simulation for rainfallPrecipitation = $rainfall")

    val params = SimulationParameters(
      year = 2020,
      startMonth = 6,
      endMonth = 9,
      humanIntervention = false,
      weatherList = weatherList,
      regrowTree = 0.01f,
      rainfallPrecipitation = rainfall.toFloat,
      simulationStep = simulationStep
    )

    val initialGrid = FlatGrid.generateFlatGrid(gridGenerationConfig)

    val result = CSVSimulation.simulateAndCollect(initialGrid, params)

    result.foreach { case (step, burnedRatio) =>
      writer.println(s"$runIndex,$rainfall,$step,$burnedRatio")
    }

    runIndex += 1
  }

  writer.close()

  println(s"All simulations done. Results saved to $outputPath")

}
