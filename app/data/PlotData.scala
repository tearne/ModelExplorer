package data

import play.api.libs.json.{JsString, JsValue, Json, _}

sealed trait PlotBlob {
  val typeDescription: String
}
object PlotBlob {
  implicit val jsonWrites = new Writes[PlotBlob]{
    override def writes(o: PlotBlob): JsValue = {

      val typeDesc: JsObject = Json.obj("type" -> JsString(o.typeDescription))
      typeDesc ++ {o match {
        case tsb: SeriesBand => SeriesBand.jsonWrites.writes(tsb)
        case dist: Distribution => Distribution.jsonWrites.writes(dist)
      }}
    }
  }
}
case class SeriesBand(lower: Seq[(BigDecimal, BigDecimal)], upper: Seq[(BigDecimal, BigDecimal)]) extends PlotBlob {
  val typeDescription = "time-series-band"
  // x values must be shared, and upper must be >= lower
  assume(!lower.zip(upper).exists{case ((x1,l),(x2,u)) => x1 != x2 && l > u})
}
object SeriesBand{
  implicit val jsonWrites = Json.writes[SeriesBand]
}

case class Distribution(values: Seq[(BigDecimal, BigDecimal)]) extends PlotBlob {
  val typeDescription = "distribution"
}
object Distribution{
  implicit val jsonWrites = Json.writes[Distribution]
}

case class PlotData(
  inputs: Map[String, PlotBlob],
  outputsRaw: Map[String, PlotBlob],
  outputsFiltered: Map[String, PlotBlob] = Map.empty,
  queryMap: Map[String, Seq[String]] = Map.empty
){
  assume(outputsFiltered.size == 0 || outputsFiltered.size == outputsRaw.size)
}

object PlotData{
  implicit val jsonWrites = Json.writes[PlotData]
  def buildFrom(raw: Seq[RunData], filtered: Seq[RunData]): PlotData = {
    val inNames: Seq[String] = raw.head.inputs.keys.toSeq
    val outNames: Seq[String] = raw.head.outputs.keys.toSeq

    // Lots of Series -> SeriesBand
    // Lots of Number -> Distribution

    val t: Map[String, PlotBlob] = inNames.map{inParamName =>
      inParamName -> raw.head.inputs(inParamName).description match {
        case Series.description =>
          SeriesBand.buildFromSeries(raw.map{_.inputs(inParamName).asInstanceOf[Series]})
        case Number.description =>
          Distribution.buildFromNumbers(raw.map{_.inputs(inParamName).asInstanceOf[Number]})

      }
    }

    PlotData(
      ???,
      buildFrom(raw),
      buildFrom(filtered)
    )
  }
}