package config

case class GridGenerationConfig(
                                 width: Int,
                                 height: Int,
                                 baseElevation: Int = 0,
                                 maxElevation: Int = 20,
                                 treeProbability: Float,
                                 terrainType: String
                               )
