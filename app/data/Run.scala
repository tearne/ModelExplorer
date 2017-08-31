package data

import play.api.libs.json._
import play.api.libs.json.Reads._

import scala.util.Random

case class Number(value: Double, isInput: Boolean) extends Blob {
  val description = Number.description
}
object Number {
  val description = "Number"
  implicit val numberFormats = Json.format[Number]
}
case class Series(x: Seq[Double], y: Seq[Double], isInput: Boolean) extends Blob {
  val description = Series.description
}
object Series {
  val description = "Series"
  implicit val seriesFormats = Json.format[Series]
}

sealed trait Blob{
  val isInput: Boolean
  val description: String
}
object Blob {
  val blobReads = new Reads[Blob]{
    override def reads(json: JsValue): JsResult[Blob] = {
      (json \ "description").as[String] match {
        case "number" => json.validate[Number]
        case "series" => json.validate[Series]
      }
    }
  }

  val blobWrites = new Writes[Blob]{
    override def writes(o: Blob): JsValue = {
        Json.obj("description" -> JsString(o.description)) ++
        {o match {
          case n: Number => Number.numberFormats.writes(n)
          case s: Series => Series.seriesFormats.writes(s)
        }}
    }
  }

  implicit val blobFormats = Format(blobReads, blobWrites)
}

case class Run(blobs: Map[String, Blob])
object Run {
  implicit val runFormats = Json.format[Run]
}

object Test extends App {

  def makeRunJson(): String = {
    val run = Run(
      Map(
        "beta" -> Number(Random.nextDouble(), true),
        "gamma" -> Number(Random.nextDouble(), true),
        "outbreak-size" -> Number(Random.nextInt(10000), false),
        "culls" -> Series(Seq(2008,2009,2010), Seq(Random.nextInt(1000),Random.nextInt(1000),Random.nextInt(1000)), false)
      )
    )
    Json.prettyPrint(Json.toJson(run))
  }

  println(makeRunJson())
}
