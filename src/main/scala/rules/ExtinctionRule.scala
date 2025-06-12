package rules

import model.Cell

object ExtinctionRule {
  def survives(cell: Cell): Boolean = cell.humidity >= 0.7
}
