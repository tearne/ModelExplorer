package controllers

import javax.inject._

import data._
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val runs = PretendModel.loadOfRuns

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def data() = Action { implicit request: Request[AnyContent] =>
    val result = Json.toJson(
      PlotData.buildFrom(runs, request.queryString)
    )
    Ok(result)
  }
}
