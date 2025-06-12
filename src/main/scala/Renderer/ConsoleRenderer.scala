package Renderer

import model._

object ConsoleRenderer {

    val RESET = "\u001B[0m"
    val GREEN = "\u001B[32m"
    val RED = "\u001B[31m"
    val BLUE = "\u001B[34m"
    val GREY = "\u001B[90m"

    def cellSymbol(cell: Cell): String = cell.state match {
      case CellState.Tree => s"${GREEN}T$RESET"
      case CellState.Fire => s"${RED}F$RESET"
      case CellState.Water => s"${BLUE}W$RESET"
      case CellState.Soil => s"$GREY.$RESET"
    }

    def render(grid: Grid): Unit = {
        for (y <- grid.rangeY) {
          val row = for (x <- grid.rangeX) yield {
            grid.get(x, y).map(cellSymbol).getOrElse(" ")
          }
          println(row.mkString(" "))
        }
    }
}
