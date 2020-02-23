package filters

import java.util.Base64

import akka.stream.Materializer
import com.google.inject.Inject
import play.api.Logging
import play.api.mvc.{Results, _}
import repositories.BasicAuthRepo

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class BasicAuthFilter @Inject()(implicit val basicAuthRepo: BasicAuthRepo,
                                val ex: ExecutionContext,
                                val mat: Materializer) extends Filter with Logging {

  private val authHeaderPrefix = "basic"

  private val blacklistedUris = Seq("/logging", "/health", "/")
  private val realm = "shopping-cart"
  private lazy val unauthorized = Results.Unauthorized("{\"message\": \"unauthorized\"}")
    .withHeaders(("WWW-Authenticate", s"Basic realm=$realm")).as("application/json")

  private lazy val decoder = Base64.getDecoder


  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {

    if (isBlacklistedUri(requestHeader)) {
      nextFilter(requestHeader)
    } else {
      getUserAndPassword(requestHeader) match {
        case Some((user, password)) => basicAuthRepo.isAuthenticated(user, password).flatMap { authenticated =>
            if (!authenticated) {
              fail(requestHeader, "Invalid credentials", unauthorized)
            } else {
              succeed(requestHeader, user, nextFilter(requestHeader))
            }
        }.recover {
          case t: Throwable =>
          Await.result(fail(requestHeader, t.getMessage, Results.InternalServerError), 5 seconds)
        }
        case None =>
          fail(requestHeader, "Missing header", unauthorized)
      }
    }
  }

  private def isBlacklistedUri(requestHeader: RequestHeader): Boolean = {
    val requestUri = requestHeader.uri
    if (requestUri.length > 0) {
      blacklistedUris.contains(requestUri)
    } else {
      false
    }
  }

  private def getUserAndPassword(requestHeader: RequestHeader): Option[(String, String)] = {
    (for {
      basicAuthHeader <- requestHeader.headers.get("authorization")
      userAndPassFromHeader <- decodeBasicAuthHeader(basicAuthHeader)
    } yield userAndPassFromHeader) match {
      case auth: Some[(String, String)] => auth
      case None => None
    }
  }

  private def decodeBasicAuthHeader(auth: String): Option[(String, String)] = {
    val authParts = auth.split(" ")
    if (authParts.size < 2 || authParts(0).toLowerCase() != authHeaderPrefix) {
      None
    } else {
      decodeBasicAuth(authParts(1))
    }
  }

  private def decodeBasicAuth(auth: String): Option[(String, String)] = {
    val decodedAuthStr = new String(decoder.decode(auth), "UTF-8")
    val usernamePassword = decodedAuthStr.split(":")

    if (usernamePassword.size == 2) {
      Some(usernamePassword(0), usernamePassword(1))
    } else {
      None
    }
  }

  /**
   * Resolve the user's IP address in case (s)he is behind a proxy or load balancer.
   */
  private def getUserIPAddress(requestHeader: RequestHeader): String = {
    requestHeader.headers.get("x-forwarded-for").getOrElse(requestHeader.remoteAddress.toString)
  }

  private def fail(requestHeader: RequestHeader, reason: String, result: Result): Future[Result] = {
    val ip = getUserIPAddress(requestHeader)
    logger.warn(s"$ip failed to log in: $reason. Requested uri: ${requestHeader.uri}")
    Future.successful(result)
  }

  private def succeed(requestHeader: RequestHeader, user: String, eventualResult: Future[Result]): Future[Result] = {
    val ip = getUserIPAddress(requestHeader)
    logger.info(s"$ip successfully logged in as user $user. Requested uri: ${requestHeader.uri}")
    eventualResult
  }

}
