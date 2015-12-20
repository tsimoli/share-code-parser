package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.libs.ws.{WSAuthScheme, WSClient}
import play.api.mvc.{Action, Controller}
import scala.concurrent.Future
import scala.concurrent.Future._

class ParserProxyController @Inject() (ws: WSClient) extends Controller {
  implicit val shareCodeWrite= Json.format[ParseData]
  /**
    * Forward to parser
    */
  def forwardToParser = Action.async(parse.json) { implicit request =>
    request.body.validate[ParseData].map {
      case (parseData) =>
        val webservicePost = ws.url("http://parception-service.default.svc.cluster.local:9000/api/parse").withRequestTimeout(10000)
          .withAuth("parserTest", "parserTestPass", WSAuthScheme.BASIC)
          .withHeaders("Content-Type" -> "application/json")
        webservicePost.post(Json.toJson(parseData))
        Future(Ok("Parse command sent"))
    }.recoverTotal {
      e => successful(BadRequest("Error"))
    }
  }
}

case class ParseData(matchId: String, shareCode: String, url: String, matchDuration: Long, matchDate: Long)
