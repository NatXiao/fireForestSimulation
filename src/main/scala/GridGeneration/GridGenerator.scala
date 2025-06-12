package GridGeneration

import config.GenerationConfig
import model.{Cell, CellState, Grid}

import scala.annotation.tailrec
import scala.util.Random

object GridGenerator {

  def generateGrid(config: GenerationConfig): Grid = {
    val elevation = generateElevation(config.width, config.height, config.maxElevation, config.elevationVariation)
    //val elevation = generateElevation2(config.width, config.height, config.maxElevation)
    val lake =
      if (config.lake)
        detectLakes(elevation)
      else
        Set.empty[(Int, Int)]
    val potentialSources = for {
      y <- 1 until elevation.length - 1
      x <- 1 until elevation.head.length - 1
      elev = elevation(y)(x)
      if elev > config.maxElevation * 0.4 && elev < config.maxElevation * 0.8
    } yield (x, y)

    val river: Set[(Int, Int)] =
      if (config.river && potentialSources.nonEmpty)
        placeRiver(elevation, Random.shuffle(potentialSources).head)
      else
        Set.empty

    val cells2D = generateCells(elevation, lake, river, config.treeProbability)
    Grid(cells2D)
  }

  def generateElevation(
                         width: Int,
                         height: Int,
                         maxElevation: Int,
                         variation: Float
                       ): Vector[Vector[Int]] = {

    val centerX = width / 2
    val centerY = height / 2
    val maxDist = math.sqrt(centerX * centerX + centerY * centerY)

    // 🔀 Choix aléatoire entre montagne ou vallée centrale
    val isCentralMountain = true //scala.util.Random.nextBoolean()
    val offsetAngle = scala.util.Random.nextDouble() * 2 * math.Pi // pour vallées latérales
    val asymmetry = scala.util.Random.nextBoolean()

    Vector.tabulate(height, width) { (y, x) =>
      val dx = x - centerX
      val dy = y - centerY
      val dist = math.sqrt(dx * dx + dy * dy) / maxDist

      // ➕ Optionnel : ajout d'une asymétrie (vallée/montagne pas forcément au centre)
      val directionWeight = if (asymmetry) {
        val angle = math.atan2(dy, dx)
        val diff = math.abs(math.sin(angle - offsetAngle))
        0.3 + 0.7 * diff // entre 0.3 et 1.0
      } else 1.0

      // Formule d'altitude
      val norm =
        if (isCentralMountain)
          math.pow(1.0 - dist, variation) * directionWeight
        else
          math.pow(dist, variation) * directionWeight

      val elevation = (norm * maxElevation).toInt
      elevation
    }
  }


  def placeRiver(elevation: Vector[Vector[Int]], start: (Int, Int)): Set[(Int, Int)] = {
    def isBorder(x: Int, y: Int): Boolean =
      x == 0 || y == 0 || x == elevation(0).length - 1 || y == elevation.length - 1

    def cardinalNeighbors(x: Int, y: Int): Seq[(Int, Int)] =
      Seq((x+1,y), (x-1,y), (x,y+1), (x,y-1)).filter {
        case (nx, ny) => ny >= 0 && ny < elevation.length && nx >= 0 && nx < elevation(0).length
      }

    @tailrec
    def descend(current: (Int, Int), acc: Set[(Int, Int)]): Set[(Int, Int)] = {
      val (x, y) = current
      if (isBorder(x, y)) acc + current
      else {
        val neighbors = cardinalNeighbors(x, y)
        val next = neighbors.minBy(n => elevation(n._2)(n._1))
        if (elevation(next._2)(next._1) >= elevation(y)(x)) acc + current
        else descend(next, acc + current)
      }
    }

    descend(start, Set.empty)
  }

  def detectLakes(elevation: Vector[Vector[Int]]): Set[(Int, Int)] = {
    val height = elevation.length
    val width = elevation.head.length
    val visited = Array.fill(height, width)(false)
    var lakes = Set.empty[(Int, Int)]

    def cardinalNeighbors(x: Int, y: Int): Seq[(Int, Int)] =
      Seq((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)).filter {
        case (nx, ny) => ny >= 0 && ny < height && nx >= 0 && nx < width
      }

    def isBasin(points: Set[(Int, Int)]): Boolean =
    // A basin should not touch the border
      !points.exists { case (x, y) =>
        x == 0 || y == 0 || x == width - 1 || y == height - 1
      }

    def floodFill(x: Int, y: Int, threshold: Int): Set[(Int, Int)] = {
      var toVisit = Set((x, y))
      var basin = Set.empty[(Int, Int)]

      while (toVisit.nonEmpty) {
        val (cx, cy) = toVisit.head
        toVisit = toVisit - ((cx, cy))

        if (!visited(cy)(cx) && elevation(cy)(cx) <= threshold) {
          visited(cy)(cx) = true
          basin += ((cx, cy))
          toVisit ++= cardinalNeighbors(cx, cy).filterNot(p => visited(p._2)(p._1))
        }
      }
      basin
    }

    val maxElevation = elevation.flatten.max
    val threshold = (maxElevation * 0.3).toInt

    for {
      y <- 1 until height - 1
      x <- 1 until width - 1
      if !visited(y)(x) && elevation(y)(x) <= threshold
    } {
      val basin = floodFill(x, y, threshold)
      if (basin.size > 8 && isBasin(basin)) {
        lakes ++= basin
      }
    }

    lakes
  }


  def isAdjacentToWater(x: Int, y: Int, water: Set[(Int, Int)]): Boolean = {
      val neighbors = Seq((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1))
      neighbors.exists(water.contains)
    }

    def computeHumidity(
                         x: Int,
                         y: Int,
                         z: Int,
                         water: Set[(Int, Int)],
                         elevation: Vector[Vector[Int]],
                         rand: Random
                       ): Float = {
      val base =
        if (water.contains((x, y))) 1.0f
        else if (isAdjacentToWater(x, y, water)) 0.6f
        else {
          val elevationFactor = (1.0f - z / elevation.flatten.max) * 0.3f
          val randomness = rand.nextFloat()
          0.5f - elevationFactor
        }

      base.min(1.0f)
    }


    def generateCells(
                       elevation: Vector[Vector[Int]],
                       lake: Set[(Int, Int)],
                       river: Set[(Int, Int)],
                       treeProbability: Float
                     ): Vector[Vector[Cell]] = {
      val water = lake ++ river
      val rand = new Random()

      Vector.tabulate(elevation.length, elevation.head.length) { (y, x) =>
        val z = elevation(y)(x)
        val pos = (x, y)

        val humidity = computeHumidity(x, y, z, water, elevation, rand)

        val state =
          if (water.contains(pos)) CellState.Water
          else if (rand.nextFloat() < treeProbability) CellState.Tree
          else CellState.Soil

        Cell(x, y, z, state, humidity)
      }
    }


}