import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

import scala.io.Source

case class MeteoRaw(
                     station_abbr: String,
                     reference_timestamp: String,
                     sre000d0: Option[Double], // radiation
                     rre150d0: Option[Double], // rain
                     oli000d0: Option[Double]  // lightning / storm
                   )

object MeteoClassifier extends App {
  val yearFilter = 2020
  val startMonth = 6
  val endMonth = 9

  val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

  val rawData = Source.fromURL("https://data.geo.admin.ch/ch.meteoschweiz.ogd-smn/sio/ogd-smn_sio_d_recent.csv").mkString

  val reader = rawData.asCsvReader[MeteoRaw](rfc.withHeader)

  val entries = reader
    .toList
    .flatMap {
      case Right(row) =>
        val date = LocalDateTime.parse(row.reference_timestamp, formatter)

        if (
          date.getYear == yearFilter &&
            date.getMonthValue >= startMonth &&
            date.getMonthValue <= endMonth
        ) {
          val rain = row.rre150d0.getOrElse(0.0)
          val radiation = row.sre000d0.getOrElse(0.0)
          val storm = row.oli000d0.getOrElse(0.0)

          val category =
            if (storm > 0.5) "Orageux"
            else if (rain > 0.2) "Pluvieux"
            else if (radiation > 150 && rain == 0.0) "Ensoleillé"
            else "Autre"

          Some((date.toLocalDate, row.station_abbr, category))
        } else None

      case _ => None
    }



  // Filtrage final : uniquement les jours intéressants
  val filtered = entries.filter(e => Set("Ensoleillé", "Pluvieux", "Orageux").contains(e._3))


  filtered.take(10).foreach(println)
}
*/