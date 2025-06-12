package config

import model.Weather

case class SimulationParameters(
                                 year : Int,
                                 startMonth : Int,
                                 endMonth : Int,

                                 weatherList: List[Weather],
                                 rainfallPrecipitation : Float,

                                 regrowTree: Float,
                                 simulationStep : Int,
                                 humanIntervention:Boolean,
                               )
