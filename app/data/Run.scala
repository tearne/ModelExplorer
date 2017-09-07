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

case class Meta(inputNames: Seq[String], outputNames: Seq[String])
object Meta{
  implicit val metaFormats = Json.format[Meta]
}

case class Run(meta: Meta, data: Map[String, Blob])
object Run {
  implicit val runFormats = Json.format[Run]
}

object Test extends App {
  println(PretendModel.loadOfRuns.map(run => Json.prettyPrint(Json.toJson(run))))
}
