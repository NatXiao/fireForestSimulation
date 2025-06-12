package Renderer

import model._

object ConsoleElevationRenderer {
  def formatElevation(h: Float): String = {
    f"$h%1.1f" // format en 1 chiffre après la virgule, ex: 0.7
  }

  def render(grid: Grid): Unit = {
    for (y <- grid.rangeY) {
      val row = for (x <- grid.rangeX) yield {
        grid.get(x, y).map(cell => formatElevation(cell.z)).getOrElse("   ")
      }
      println(row.mkString(" "))
    }
  }
}
