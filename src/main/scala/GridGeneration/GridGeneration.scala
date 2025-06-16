package GridGeneration

import config.GridGenerationConfig
import model.{Cell, CellState, Grid}

import scala.util.Random


object GridGeneration {
  def generateFlatGrid(config: GridGenerationConfig): Grid = {
    if (config.sinusoidal) {
      val cells2D = generateCellsSin(config)
      Grid(cells2D)
    }
    else {
      val cells2D = generateCells(config)
      Grid(cells2D)
    }
}

  private def generateCells(config: GridGenerationConfig): Vector[Vector[Cell]] = {
    val width = config.width
    val height = config.height
    val base = config.baseElevation
    val max = config.maxElevation
    val treeProb = config.treeProbability

    val elevationStep = if (width > 1) (max - base).toFloat / (width - 1) else 0

    Vector.tabulate(height, width) { (y, x) =>
      val z = base + (elevationStep * x).toInt  // Elevation increases from left to right
      val state = if (Random.nextFloat() < treeProb) CellState.Tree else CellState.Soil
      val humidity =  0.5f//Random.between(0.0f, 1.0f)
      Cell(x, y, z, state, humidity)
    }
  }

  private def generateCellsSin(config: GridGenerationConfig): Vector[Vector[Cell]] = {
    val width = config.width
    val height = config.height
    val base = config.baseElevation
    val max = config.maxElevation
    val treeProb = config.treeProbability

    Vector.tabulate(height, width) { (y, x) =>
      val normalizedX = x.toFloat / (width - 1) // Range 0 to 1
      val z = base + ((Math.sin(normalizedX * Math.PI) * (max - base)).toFloat).toInt
      val state = if (Random.nextFloat() < treeProb) CellState.Tree else CellState.Soil
      val humidity = 0.5f
      Cell(x, y, z, state, humidity)
    }
  }

}
