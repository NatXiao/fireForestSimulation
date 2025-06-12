package model



case class Cell(x: Int, y : Int, z: Int, state: CellState, humidity: Float)

sealed trait CellState

object CellState{
  case object Soil extends CellState
  case object Tree extends CellState
  case object Fire extends CellState
  case object Water extends CellState
}