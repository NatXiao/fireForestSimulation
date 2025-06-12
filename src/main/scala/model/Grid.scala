package model

case class Grid(cells: Vector[Vector[Cell]]){

  def get(x: Int, y: Int): Option[Cell] =
    for {
      row <- cells.lift(y)
      cell <- row.lift(x)
    } yield cell

  def mapCells(f: Cell => Cell): Grid = {
    val updated = cells.map(_.map(f))
    Grid(updated)
  }

  def rangeX: Range = cells.head.indices

  def rangeY: Range = cells.indices

}