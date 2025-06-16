package visualSimulation

import Renderer.CanvasRenderer
import simulation.VisualSimulationRunner
import config._
import model.CellState
import rules.WeatherRule.generateWeatherList

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{VBox, HBox}
import scalafx.scene.canvas.Canvas
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.animation.AnimationTimer
import scalafx.scene.text.Font
import scalafx.scene.chart.{XYChart, LineChart, NumberAxis}
import scalafx.collections.ObservableBuffer
import scalafx.stage.Screen

object SimulationUI extends JFXApp3 {

  override def start(): Unit = {
    var currentTimer: Option[AnimationTimer] = None

    val screenBounds = Screen.primary.visualBounds
    val screenWidth = screenBounds.width
    val screenHeight = screenBounds.height

    val treeSlider = new Slider(0, 1, 0.2)
    val rainfallSlider = new Slider(0.03, 0.14, 0.08)
    val stepSlider = new Slider(10, 200, 100)
    val gridTypeCombo = new ComboBox[String](Seq("Flat", "sinusoidal", "mountain", "valley")) {
      value = "sinusoidal"
    }

    val widthSpinner = new Spinner[Int](10, 100, 20) {
      editable = true
    }
    val heightSpinner = new Spinner[Int](5, 100, 10) {
      editable = true
    }

    val speedSlider = new Slider(0.1, 5.0, 1.0) {
      majorTickUnit = 1
      minorTickCount = 4
      showTickLabels = true
      showTickMarks = true
    }

    val showHumidityCheckBox = new CheckBox("Show Humidity") {
      selected = true
    }

    val pauseButton = new Button("⏸ Pause")
    var paused = false
    pauseButton.onAction = _ => {
      paused = !paused
      pauseButton.text = if (paused) "▶️ Resume" else "⏸ Pause"
    }

    val runButton = new Button("▶️ Run Simulation")

    val canvasWidth = screenWidth * 0.9 // 90% of screen width
    val canvasHeight = screenHeight * 0.3 // 30% of screen height
    val canvas = new Canvas(canvasWidth, canvasHeight)

    val gc = canvas.graphicsContext2D

    val stepLabel = new Label("Step: 0") {
      font = Font("Arial", 16)
    }
    val weatherLabel = new Label("🌤 Weather: N/A") {
      font = Font("Arial", 16)
    }
    val baseElevationSpinner = new Spinner[Int](0, 1000, 0) {
      editable = true
    }
    val maxElevationSpinner = new Spinner[Int](1, 2000, 20) {
      editable = true
    }

    val burntLabel = new Label("🔥 Burnt Ratio: 0.0%") {
      font = Font("Arial", 16)
    }
    val burntSeries = new javafx.scene.chart.XYChart.Series[Number, Number]()
    burntSeries.setName("Burnt Ratio")


    val humiditySeries = new javafx.scene.chart.XYChart.Series[Number, Number]()
    humiditySeries.setName("Avg Humidity")


    val burntChart = new LineChart[Number, Number](
      new NumberAxis("Step", 0, 200, 10),
      new NumberAxis("Burnt Ratio", 0, 1, 0.1)
    ) {
      title = "🔥 Burnt Ratio Over Time"
      data = ObservableBuffer(Seq(burntSeries): _*)

      prefHeight = 200
    }

    val humidityChart = new LineChart[Number, Number](
      new NumberAxis("Step", 0, 200, 10),
      new NumberAxis("Avg Humidity", 0, 1, 0.1)
    ) {
      title = "💧 Average Humidity Over Time"
      data = ObservableBuffer(Seq(humiditySeries): _*)
      prefHeight = 200
    }


    runButton.onAction = _ => {
      // Stop any previous simulation
      currentTimer.foreach(_.stop())

      val treeProb = treeSlider.value.value.toFloat
      val rainfall = rainfallSlider.value.value.toFloat
      val steps = stepSlider.value.value.toInt
      val gridType = gridTypeCombo.value.value
      val width = widthSpinner.getValue
      val height = heightSpinner.getValue
      val baseElevation = baseElevationSpinner.getValue
      val maxElevation = maxElevationSpinner.getValue

      val gridConfig = GridGenerationConfig(
        width = width,
        height = height,
        baseElevation = baseElevation,
        maxElevation = maxElevation,
        treeProbability = treeProb,
        terrainType = gridType
      )

      // Adjustable ratios
      val sunnyRatio = 0.71
      val rainyRatio = 0.22
      val stormyRatio = 0.07

      val sunnyCount = (steps * sunnyRatio).toInt
      val rainyCount = (steps * rainyRatio).toInt
      val stormyCount = steps - sunnyCount - rainyCount // ensure total matches steps

      val weatherList = generateWeatherList(
        sunnyCount = sunnyCount,
        rainyCount = rainyCount,
        stormyCount = stormyCount
      )


      val simParams = SimulationParameters(
        steps = steps,
        weatherList = weatherList,
        rainfallPrecipitation = rainfall
      )

      val frames = VisualSimulationRunner.runFrames(gridConfig, simParams)

      var index = 0
      var lastFrameTime = 0L

      burntSeries.data().clear()
      humiditySeries.data().clear()

      val timer = AnimationTimer { now =>
        if (!paused && index < frames.length) {
          val speedMultiplier = speedSlider.value.value
          val delayNanos = (1e9 / speedMultiplier).toLong

          if (now - lastFrameTime >= delayNanos) {
            val (grid, weather) = frames(index)
            val showHumidity = showHumidityCheckBox.selected.value
            CanvasRenderer.drawIsometricGrid(gc, grid, showHumidity = showHumidity)

            stepLabel.text = s"Step: $index"
            weatherLabel.text = s"${weatherIcon(weather)} Weather: $weather"

            val burntRatio = grid.burntRatio

            burntLabel.text = f"🔥 Burnt Ratio: ${burntRatio * 100}%.1f%%"


            index += 1

            burntSeries.data() += XYChart.Data[Number, Number](index, grid.burntRatio)
            humiditySeries.data() += XYChart.Data[Number, Number](index, grid.averageHumidity)

            lastFrameTime = now
          }
        }
      }

      // Save and start new timer
      currentTimer = Some(timer)
      timer.start()
    }


    def weatherIcon(weather: model.Weather): String = weather match {
      case model.Weather.Sunny => "☀️"
      case model.Weather.Rainy => "🌧"
      case model.Weather.Stormy => "⛈"
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Forest Fire Simulation (Canvas)"
      width = screenWidth * 0.8
      height = screenHeight * 0.8
      scene = new Scene {
        root = new ScrollPane {
          // Make ScrollPane fixed height to enable scrolling when content overflows
          prefHeight = screenHeight * 0.8
          prefWidth = screenWidth * 0.8
          fitToWidth = true

          content = new VBox(20) {
            padding = Insets(20)
            children = Seq(
              new VBox(10) {
                style = "-fx-border-color: lightgray; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 15 0;"
                children = Seq(
                  new HBox(10, new Label("Tree Probability:"), treeSlider),
                  new HBox(10, new Label("Rainfall:"), rainfallSlider),
                  new HBox(10, new Label("Simulation Days:"), stepSlider),
                  new HBox(10, new Label("Grid Type:"), gridTypeCombo),
                  new HBox(10, new Label("Width:"), widthSpinner, new Label("Height:"), heightSpinner),
                  new HBox(10, new Label("Base Elevation:"), baseElevationSpinner, new Label("Max Elevation:"), maxElevationSpinner),
                  new HBox(10, new Label("Speed (x):"), speedSlider),
                  new HBox(10, showHumidityCheckBox, pauseButton),
                  runButton
                )
              },
              new VBox(10) {
                children = Seq(
                  new HBox(20, stepLabel, weatherLabel),
                  canvas,
                  burntChart,
                  humidityChart
                )
              }
            )
          }
        }
      }
    }

  }
}
