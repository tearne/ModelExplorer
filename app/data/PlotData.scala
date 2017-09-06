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
case class SeriesBand(lower: Seq[(Double, Double)], upper: Seq[(Double, Double)]) extends PlotBlob {
  val typeDescription = "time-series-band"
  // x values must be shared, and upper must be >= lower
  assume(!lower.zip(upper).exists{case ((x1,l),(x2,u)) => x1 != x2 && l > u})
}
object SeriesBand{
  implicit val jsonWrites = Json.writes[SeriesBand]

  def buildFromSeries(serieses: Seq[Series]): SeriesBand = {
    println(serieses)

    val byXvalue: Seq[Seq[(Double, Double)]] =
      serieses
        .map(series => series.x.zip(series.y))
        .transpose

    val xBlocks: Seq[Set[Double]] = byXvalue.map(_.map(_._1).toSet)
    assume(!xBlocks.exists(xBlock => !(xBlock.size == 1)))                  // x vals must be common in each block
    assume(!xBlocks.map(_.head).sliding(2).exists{case Seq(a,b) => a >= b}) // Blocks x vals must be increasing

    val bands: Seq[(Double, Double, Double)] = byXvalue.map { vertical =>
      val x = vertical.head._1
      val ys = vertical.map(_._2)
      (x, ys.min, ys.max)
    }

    val lower = bands.map{case (x,l,_) => (x, l)}
    val upper = bands.map{case (x,_,u) => (x, u)}

    SeriesBand(lower, upper)
  }
}

case class Distribution(values: Seq[(Double, Double)]) extends PlotBlob {
  val typeDescription = "distribution"
}
object Distribution{
  implicit val jsonWrites = Json.writes[Distribution]

  def buildFromNumbers(numbers: Seq[Number]) = {
    val numBins = 10

    val values = numbers.map(_.value)
    val min = values.min
    val max = values.max

    val binWidth = (max - min) / numBins.toDouble
    val binCounts: Seq[(Int, Int)] = values
      .map(v => ((v - min)/binWidth).toInt)
      .groupBy(identity)
      .mapValues(_.size)
      .toSeq
      .sortBy(_._1)

    val points: Seq[(Double, Double)] = binCounts
        .map{case (bin, count) =>
          val x = min + bin * binWidth
          x -> count.toDouble
        }

    Distribution(points)
  }
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
  def buildFrom(raw: Seq[Run], filterQuery  : Map[String, Seq[String]]): PlotData = {

    val filtered: Seq[Run] = Seq.empty //TODO filters e.g. "beta>=0.3"
    val inNames: Seq[String] = raw.head.blobs.filter(_._2.isInput).keys.toSeq
    val outNames: Seq[String] = raw.head.blobs.filterNot(_._2.isInput).keys.toSeq

    // Lots of Series -> SeriesBand
    // Lots of Number -> Distribution

    def buildFrom(rawBlobs: Seq[Run], names: Seq[String]) = {
      names.map{name =>
        val plotBlob = rawBlobs.head.blobs(name).description match {
          case Series.description =>
            SeriesBand.buildFromSeries(raw.map{_.blobs(name).asInstanceOf[Series]})
          case Number.description =>
            Distribution.buildFromNumbers(raw.map{_.blobs(name).asInstanceOf[Number]})

        }
        name -> plotBlob
      }.toMap
    }

    PlotData(
      inputs = buildFrom(raw, inNames),
      outputsRaw = buildFrom(raw, outNames),
      //TODO filtered
      queryMap = filterQuery
    )
  }
}