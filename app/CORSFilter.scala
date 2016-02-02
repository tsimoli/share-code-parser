import controllers.Default
import play.api.Logger
import play.api.mvc.{Result, RequestHeader, Filter}

case class CORSFilter() extends Filter{
  import scala.concurrent._
  import ExecutionContext.Implicits.global
  lazy val allowedDomain = play.api.Play.current.configuration.getString("cors.allowed.domain")
  def isPreFlight(r: RequestHeader) =(
    r.method.toLowerCase.equals("options")
      &&
      r.headers.get("Access-Control-Request-Method").nonEmpty
    )

  // In production define allowed origins
  def apply(f: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    Logger.trace("[cors] filtering request to add cors")
    if (isPreFlight(request)) {
      Logger.trace("[cors] request is preflight")
      Logger.trace(s"[cors] default allowed domain is $allowedDomain")
      Future.successful(Default.Ok.withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Methods" -> "POST",
        "Access-Control-Allow-Headers" -> "Origin, Accept, Authorization, STATS_TOKEN, X-REQUESTED-WITH, X-CSRF-TOKEN, Content-Type",
        "Access-Control-Allow-Credentials" -> "true"
      ))
    } else {
      Logger.trace("[cors] request is normal")
      Logger.trace(s"[cors] default allowed domain is $allowedDomain")
      f(request).map{_.withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Methods" -> "POST",
        "Access-Control-Allow-Headers" -> "Origin, Accept, Authorization, STATS_TOKEN, X-REQUESTED-WITH, X-CSRF-TOKEN, Content-Type",
        "Access-Control-Allow-Credentials" -> "true"
      )}
    }
  }
}
