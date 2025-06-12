package rules

import config.SimulationParameters
import model.{Cell, CellState, Grid}

object FirePropagationRule {

  def shouldCatchFire(cell: Cell, grid: Grid, params: SimulationParameters): Boolean = {
    val influence = influenceZone(cell, params)

    val neighbors = influence.flatMap { case (nx, ny) =>
      grid.get(nx, ny)
    }

    neighbors.exists { neighbor =>
      val isBurning = neighbor.state == CellState.Fire
      val elevationFactor = neighbor.z < cell.z // monte ?
      val humidityFactor = cell.humidity < 0.5

      isBurning &&
        (humidityFactor || elevationFactor)
    }
  }

  def influenceZone(center: Cell, params: SimulationParameters): Seq[(Int, Int)] = {
    val baseRadius = 1
    val range = -baseRadius to baseRadius

    for {
      dx <- range
      dy <- range
    } yield (
      center.x + dx,
      center.y + dy
    )
  }


}
