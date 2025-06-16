package GridGeneration

import config.GridGenerationConfig
import model.{Cell, CellState, Grid}

import scala.util.Random

object GridGeneration {

  def generateGrid(config: GridGenerationConfig): Grid = {
    config.terrainType match {
      case "sinusoidal" => Grid(generateCellsSin(config))
      case "mountain"   => Grid(generateCellsMountain(config))
      case "valley"     => Grid(generateCellsValley(config))
      case _            => Grid(generateCellsFlat(config))
    }
  }

  private def generateCellsFlat(config: GridGenerationConfig): Vector[Vector[Cell]] = {
    val width = config.width
    val height = config.height
    val base = config.baseElevation
    val max = config.maxElevation
    val treeProb = config.treeProbability

    val elevationStep = if (width > 1) (max - base).toFloat / (width - 1) else 0

    Vector.tabulate(height, width) { (y, x) =>
      val z = base + (elevationStep * x).toInt  // Flat, linearly increasing from left to right
      val state = if (Random.nextFloat() < treeProb) CellState.Tree else CellState.Soil
      val humidity = 0.5f
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
      val normalizedX = x.toFloat / (width - 1) // 0 to 1
      val z = base + ((Math.sin(normalizedX * Math.PI) * (max - base)).toFloat).toInt
      val state = if (Random.nextFloat() < treeProb) CellState.Tree else CellState.Soil
      val humidity = 0.5f
      Cell(x, y, z, state, humidity)
    }
  }

  private def generateCellsMountain(config: GridGenerationConfig): Vector[Vector[Cell]] = {
    val width = config.width
    val height = config.height
    val base = config.baseElevation
    val max = config.maxElevation
    val treeProb = config.treeProbability

    val centerX = (width - 1) / 2.0
    val centerY = (height - 1) / 2.0
    val maxDistance = math.sqrt(centerX*centerX + centerY*centerY)

    Vector.tabulate(height, width) { (y, x) =>
      val dist = math.sqrt(math.pow(x - centerX, 2) + math.pow(y - centerY, 2))
      val normalizedDist = dist / maxDistance // 0 center, 1 at corner
      // Elevation decreases as we move away from center
      val z = base + ((1 - normalizedDist) * (max - base)).toInt
      val state = if (Random.nextFloat() < treeProb) CellState.Tree else CellState.Soil
      val humidity = 0.5f
      Cell(x, y, z, state, humidity)
    }
  }

  private def generateCellsValley(config: GridGenerationConfig): Vector[Vector[Cell]] = {
    val width = config.width
    val height = config.height
    val base = config.baseElevation
    val max = config.maxElevation
    val treeProb = config.treeProbability

    val centerX = (width - 1) / 2.0
    val centerY = (height - 1) / 2.0
    val maxDistance = math.sqrt(centerX*centerX + centerY*centerY)

    Vector.tabulate(height, width) { (y, x) =>
      val dist = math.sqrt(math.pow(x - centerX, 2) + math.pow(y - centerY, 2))
      val normalizedDist = dist / maxDistance // 0 center, 1 at corner
      // Elevation increases as we move away from center, valley in center
      val z = base + (normalizedDist * (max - base)).toInt
      val state = if (Random.nextFloat() < treeProb) CellState.Tree else CellState.Soil
      val humidity = 0.5f
      Cell(x, y, z, state, humidity)
    }
  }
}
