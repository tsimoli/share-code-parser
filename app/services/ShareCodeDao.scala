package services

import models.ShareCode
import org.joda.time.DateTime
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile
import slick.lifted.Tag
import util.MyPostgresDriver.api._
import com.github.tototoshi.slick.PostgresJodaSupport._

trait ShareCodeComponent {

  class ShareCodes(tag: Tag) extends Table[ShareCode](tag, "sharecode") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def shareCode = column[String]("share_code")

    def matchId = column[String]("match_id")

    def outcomeId = column[String]("outcome_id")

    def token = column[String]("token")

    def status = column[String]("status")

    def created = column[DateTime]("created")

    def * = (
      id,
      shareCode,
      matchId,
      outcomeId,
      token,
      status,
      created) <>(ShareCode.tupled, ShareCode.unapply)
  }
}

class ShareCodeDao extends ShareCodeComponent {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val shareCodes = TableQuery[ShareCodes]

  def findShareCode(shareCode: String) = {
    dbConfig.db.run(shareCodes.filter(_.shareCode === shareCode).result.headOption)
  }

  def insertShareCode(shareCode: ShareCode) = {
    dbConfig.db.run(shareCodes += shareCode).map(_ => ())
  }
}