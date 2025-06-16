package config

import model.Weather

case class SimulationParameters(
                                 steps: Int,
                                 weatherList: List[Weather],
                                 rainfallPrecipitation: Float,
                                 regrowTree: Float = 0.01f,
                                 humanIntervention: Boolean = false
                               )
