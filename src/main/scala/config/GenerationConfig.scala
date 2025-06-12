package config

case class GenerationConfig(
                        width: Int,
                        height: Int,
                        maxElevation: Int,
                        elevationVariation : Int = 10,
                        treeProbability: Float = 0.6f,
                        lake : Boolean = true,
                        river : Boolean = true
                      )
