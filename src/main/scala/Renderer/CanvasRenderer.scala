package Renderer

import model._
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

object CanvasRenderer {
  val cellSize = 20.0

  def drawGrid(gc: GraphicsContext, grid: Grid, showHumidity: Boolean): Unit = {
    gc.clearRect(0, 0, gc.canvas.width(), gc.canvas.height())

    for (x <- grid.rangeX; y <- grid.rangeY) {
      val maybeCell = grid.get(x, y)

      maybeCell.foreach { cell =>
        val baseColor = cell.state match {
          case CellState.Tree  => Color.ForestGreen
          case CellState.Fire  => Color.Red
          case CellState.Water => Color.Blue
          case CellState.Soil  => Color.SaddleBrown
        }
        if (showHumidity) {
          // Blue overlay based on humidity (0.0 to 1.0)
          val humidityOverlay = Color.Blue.deriveColor(0, 1, 1, cell.humidity.toDouble)
        // Blend colors (you can tweak for more dramatic effect)
          val blendedColor = baseColor.interpolate(humidityOverlay, 0.5)

          gc.fill = blendedColor
          gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize)
        }
        else {
          gc.fill = baseColor
          gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize)
        }
      }
    }
  }

  def drawIsometricGrid(
                         gc: GraphicsContext,
                         grid: Grid,
                         showHumidity: Boolean = true
                       ): Unit = {

    val canvasWidth = gc.canvas.width.value
    val canvasHeight = gc.canvas.height.value

    val cols = grid.width
    val rows = grid.height

    // Calculate tile size to fit canvas
    val maxTiles = cols + rows
    val tileWidth = canvasWidth / maxTiles
    val tileHeight = canvasHeight / maxTiles

    val offsetX = canvasWidth / 2
    val offsetY = 20.0

    gc.clearRect(0, 0, canvasWidth, canvasHeight)

    for (y <- grid.rangeY; x <- grid.rangeX) {
      grid.get(x, y).foreach { cell =>
        val isoX = (x - y) * tileWidth / 2 + offsetX
        val isoY = (x + y) * tileHeight / 2 + offsetY - cell.z * 0.05 // elevation offset

        val color = cell.state match {
          case CellState.Tree => Color.ForestGreen
          case CellState.Fire => Color.Red
          case CellState.Water => Color.DeepSkyBlue
          case CellState.Soil => Color.SaddleBrown
        }

        val brightness = Math.min(1.0, cell.z / 100.0 + 0.3)
        gc.fill = color.interpolate(Color.Black, 1.0 - brightness)

        // Diamond points
        val pointsX = Array(
          isoX,
          isoX + tileWidth / 2,
          isoX,
          isoX - tileWidth / 2
        )
        val pointsY = Array(
          isoY - tileHeight / 2,
          isoY,
          isoY + tileHeight / 2,
          isoY
        )

        gc.fillPolygon(pointsX, pointsY, 4)

        if (showHumidity) {
          gc.fill = Color.White
          gc.fillText(f"${cell.humidity}%.1f", isoX - tileWidth / 2, isoY)
        }
      }
    }
  }


}
