package data

import play.api.libs.json._
import play.api.libs.json.Reads._

import scala.util.Random

case class Number(value: Double) extends Blob {
  val description = Number.description
}
object Number {
  val description = "Number"
  implicit val numberFormats = Json.format[Number]
}
case class Series(x: Seq[Double], y: Seq[Double]) extends Blob {
  val description = Series.description
}
object Series {
  val description = "Series"
  implicit val seriesFormats = Json.format[Series]
}

sealed trait Blob{
  val description: String
}
object Blob {
  implicit val blobReads = new Reads[Blob]{
    override def reads(json: JsValue): JsResult[Blob] = {
      (json \ "description").as[String] match {
        case "number" => JsSuccess(json.as[Number])
        case "series" => JsSuccess(json.as[Series])
      }
    }
  }

  implicit val blobWrites = new Writes[Blob]{
    override def writes(o: Blob): JsValue = {
      Json.obj("description" -> JsString(o.description)) ++ {o match {
        case n: Number => Number.numberFormats.writes(n)
        case s: Series => Series.seriesFormats.writes(s)
      }}
    }
  }
}

case class RunData(inputs: Map[String, Blob], outputs: Map[String, Blob])
object RunData {
  implicit val runFormats = Json.format[RunData]
}

object Test extends App {

  def makeRunJson(): String = {
    val run = RunData(
      Map(
        "beta" -> Number(Random.nextDouble()),
        "gamma" -> Number(Random.nextDouble())
      ),
      Map(
        "outbreak-size" -> Number(Random.nextInt(10000)),
        "culls" -> Series(Seq(2008,2009,2010), Seq(Random.nextInt(1000),Random.nextInt(1000),Random.nextInt(1000)))
      )
    )
    Json.prettyPrint(Json.toJson(run))
  }

  println(makeRunJson())
}
