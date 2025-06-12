package rules

import model.Cell

object RegrowthRule {
  def canRegrow(cell : Cell, proba : Float) : Boolean = {
    (cell.humidity > 0.7f && scala.util.Random.nextFloat() < 0.07)|| scala.util.Random.nextFloat() < proba
  }
}
