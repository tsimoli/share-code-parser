package controllers

import _root_.util.MatchShareCodeParser
import models.ShareCode
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._
import services.ShareCodeDao
import scala.concurrent.Future._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class ShareCodeController extends Controller {
  implicit val shareCodeRead = Json.reads[ShareCodePayload]
  val shareCodeDao = new ShareCodeDao()

  /**
    * Checks if share code already exists(someone has posted it) and if not, parses it and saves to DB
    */
  def parseShareCode = Action.async(parse.json) { implicit request =>
    request.body.validate[ShareCodePayload].map {
      case (shareCodePayload) =>
        shareCodeDao.findShareCode(shareCodePayload.shareCode).map { shareCodeOption =>
          shareCodeOption.map { shareCode =>
            BadRequest("Share code already inserted")
          }.getOrElse {
            val subStringShareCode = shareCodePayload.shareCode.split("%20")
            MatchShareCodeParser.parseCode(subStringShareCode(1)).map { parsedOption =>
              val shareCode = ShareCode(-1, shareCodePayload.shareCode, parsedOption._1, parsedOption._2, parsedOption._3, "new", new DateTime())
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


