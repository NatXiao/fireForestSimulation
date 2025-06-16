package Renderer

import model._

object ConsoleHumidityRenderer {
  def formatHumidity(h: Float): String = {
    f"$h%1.1f" // format en 1 chiffre après la virgule, ex: 0.7
  }

  def render(grid: Grid): Unit = {
    for (y <- grid.rangeY) {
      val row = for (x <- grid.rangeX) yield {
        grid.get(x, y).map(cell => formatHumidity(cell.humidity)).getOrElse("   ")
      }
      println(row.mkString(" "))
    }
  }

  def renderToString(grid: Grid): String = {
    val sb = new StringBuilder
    for (y <- grid.rangeY) {
      val row = for (x <- grid.rangeX) yield {
        grid.get(x, y).map(cell => formatHumidity(cell.humidity)).getOrElse("   ")
      }
      sb.append(row.mkString(" ") + "\n")
    }
    sb.toString()
  }
}
