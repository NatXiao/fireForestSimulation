package simulation

import GridGeneration.GridGeneration
import config._
import rules.WeatherRule.generateWeatherList

import java.io.PrintWriter

object CSVMainSimulation extends App {

  val gridGenerationConfig = GridGenerationConfig(
    width = 50,
    height = 100,
    baseElevation = 0,
    maxElevation = 0,
    treeProbability = 0.2f,
    sinusoidal = false
  )

  val weatherList = generateWeatherList(
    sunnyCount = 71,
    rainyCount = 22,
    stormyCount = 7
  )
  val simulationStep = 100
  val simulationsPerRainfall = 10

  val rainfallValues = (0 to 100).map(i => 0.03 + i * (0.14 - 0.03) / 100.0) // 101 values

  val outputPath = "results/100x200/sweep_avg_result_flat.csv"
  val writer = new PrintWriter(outputPath)
  writer.println("RainfallPrecipitation,Step,AverageBurnedRatio")

  for (rainfall <- rainfallValues) {
    println(s"Running $simulationsPerRainfall simulations for rainfall = $rainfall")

    // Prepare to accumulate sums of burned ratios per step
    val sumRatios = Array.fill(simulationStep)(0.0)

    for (_ <- 1 to simulationsPerRainfall) {
      val params = SimulationParameters(
        humanIntervention = false,
        weatherList = weatherList,
        regrowTree = 0.01f,
        rainfallPrecipitation = rainfall.toFloat,
        steps = simulationStep
      )

      val initialGrid = GridGeneration.generateFlatGrid(gridGenerationConfig)
      //val initialGrid = GridGenerator.generateGrid(config)
      val result = CSVSimulationRunner.simulateAndCollect(initialGrid, params)

      result.foreach { case (step, burnedRatio) =>
        sumRatios(step) += burnedRatio
      }
    }

    // Now average and write to CSV
    for (step <- 0 until simulationStep) {
      val avg = sumRatios(step) / simulationsPerRainfall
      writer.println(f"$rainfall%.4f,$step,$avg%.4f")
    }
  }

  writer.close()
  println(s"All simulations complete. Results saved to $outputPath")
}
