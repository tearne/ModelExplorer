package controllers

import javax.inject._

import data._
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

//  type Point = (Double, Double)
//  implicit def toPoint(v: (Int, Int)) = (v._1.toDouble, v._2.toDouble)
//
//  val testData: PlotData = PlotData(
//    inputs = Map(
//      "beta" -> Distribution(Seq(0.0 -> 1.0, 1.0 -> 3.0, 2.0 -> 0.0)),
//      "gamma" -> Distribution(Seq(0.0 -> 1.0, 1.0 -> 3.0, 2.0 -> 0.0)),
//      "delta" -> Distribution(Seq(6.0 -> 1.0, 7.0 -> 3.0, 8.0 -> 0.0))
//    ),
//    outputsRaw = Map(
//      "Outbreak Size" -> SeriesBand(
//        Seq(2008 -> 10000, 2009 -> 20000, 2010 -> 30000),
//        Seq(2008 -> 10000, 2009 -> 30000, 2010 -> 50000)),
//      "Outbreak Duration" -> SeriesBand(
//        Seq(2008 -> 18, 2009 -> 20, 2010 -> 26),
//        Seq(2008 -> 19, 2009 -> 22, 2010 -> 30)),
//      "Num Culls" -> SeriesBand(
//        Seq(2008 -> 500, 2009 -> 300, 2010 -> 200),
//        Seq(2008 -> 600, 2009 -> 700, 2010 -> 400)),
//    )
//  )
  val runs = PretendModel.loadOfRuns

  def index() = Action { implicit request: Request[AnyContent] =>
    val result = Json.toJson(
      PlotData.buildFrom(runs, request.queryString)
//      testData.copy(queryMap = request.queryString)
    )
    Ok(result)
  }
}
