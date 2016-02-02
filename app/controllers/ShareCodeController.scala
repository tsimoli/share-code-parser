package controllers

import util.MatchShareCodeParser
import models.ShareCode
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import services.ShareCodeDao
import scala.concurrent.Future._
import play.api.Play.current

class ShareCodeController extends Controller {
  implicit val shareCodeRead = Json.format[ShareCodePayload]

  /**
    * Checks if share code already exists(someone has posted it) and if not, parses it and saves to DB
    */
  def parseShareCode = Action.async(parse.json) { implicit request =>
    request.body.validate[ShareCodePayload].map {
      case (shareCodePayload) =>
        lazy val shareCodeDao = current.injector.instanceOf[ShareCodeDao]
        val subStringShareCode = if(shareCodePayload.shareCode.contains("%20")) shareCodePayload.shareCode.split("%20")(1) else shareCodePayload.shareCode
        shareCodeDao.findShareCode(subStringShareCode).map { shareCodeOption =>
          shareCodeOption.map { shareCode =>
            BadRequest("Share code already inserted")
          }.getOrElse {
            MatchShareCodeParser.parseCode(subStringShareCode).map { parsedOption =>
              val shareCode = ShareCode(-1, subStringShareCode, parsedOption._1, parsedOption._2, parsedOption._3, "new", new DateTime())
              shareCodeDao.insertShareCode(shareCode)
              Ok("Share code inserted")
            }.getOrElse(BadRequest("Invalid sharecode format"))
          }
        }
    }.recoverTotal {
      e => successful(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(e))))
    }
  }
}

case class ShareCodePayload(shareCode: String)


