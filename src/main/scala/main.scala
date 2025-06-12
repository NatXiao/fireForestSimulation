package simulation

import config._
import model._
import GridGeneration.{FlatGrid, FlatGridconfig}
import rules.WeatherRule.generateWeatherList
import java.io.PrintWriter

object Main extends App {

  val gridGenerationConfig = FlatGridconfig(
    width = 20,
    heigth = 10,
    baseElevation = 0,
    maxElevation = 30,
    treeProbability = 0.2f
  )

  val initialGrid = FlatGrid.generateFlatGrid(gridGenerationConfig)

  val params = SimulationParameters(
    year = 2020,
    startMonth = 6,
    endMonth = 9,
    humanIntervention = false,
    weatherList = generateWeatherList(70, 27, 3),
    regrowTree = 0.01f,
    rainfallPrecipitation = 0.3f,
    simulationStep = 100
  )

  val outputPath = "results/simulation_001.csv"

  val result = CSVSimulation.simulateAndCollect(initialGrid, params)

  writeCsv(result, outputPath)

  println(s"Results written to $outputPath")

  def writeCsv(data: List[(Int, Double)], path: String): Unit = {
    val writer = new PrintWriter(path)
    writer.println("Step,BurnedRatio")
    data.foreach { case (step, burnedRatio) =>
      writer.println(s"$step,$burnedRatio")
    }
    writer.close()
  }
}
