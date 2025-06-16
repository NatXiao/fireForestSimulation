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

  def burntRatio: Double = {
    val total = rangeX.size * rangeY.size
    if (total == 0) 0.0
    else {
      val burnt = rangeY.flatMap { y =>
        rangeX.map(x => get(x, y).exists(_.state == CellState.Fire))
      }.count(identity)
      burnt.toDouble / total
    }
  }

  def averageHumidity: Double = {
    val cells = rangeY.flatMap(y => rangeX.flatMap(x => get(x, y)))
    if (cells.isEmpty) 0.0
    else cells.map(_.humidity).sum / cells.length
  }

  def rangeX: Range = cells.head.indices
  def rangeY: Range = cells.indices

  def width: Int = cells.headOption.map(_.size).getOrElse(0)
  def height: Int = cells.size
}