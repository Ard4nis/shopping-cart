package filters

import akka.stream.Materializer
import io.prometheus.client.SimpleTimer
import javax.inject.Inject
import logging.Summaries
import play.api.Logging
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.routing.{HandlerDef, Router}

import scala.concurrent.{ExecutionContext, Future}

class TimingFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter with Logging {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {
    val handlerDefOpt: Option[HandlerDef]  = requestHeader.attrs.get[HandlerDef](Router.Attrs.HandlerDef)
    handlerDefOpt match {
      case Some(handlerDef) => doFilter(nextFilter, handlerDef)(requestHeader)
      case None => nextFilter(requestHeader)
    }
  }

  private def doFilter(nextFilter: RequestHeader => Future[Result], handlerDef: HandlerDef)
                      (implicit requestHeader: RequestHeader): Future[Result] = {
    val requestTimer = new SimpleTimer()

    val action                  = s"${handlerDef.controller}.${handlerDef.method}"
    val comment                 = handlerDef.comments

    logger.info(s"Received: ${requestHeader.method} on ${requestHeader.uri} ($action)")

    nextFilter(requestHeader).map { result =>

      val requestTime = requestTimer.elapsedSeconds()
      val status = result.header.status

      if ((action.startsWith("controllers.") && !action.startsWith("controllers.Assets")) || comment == "[timing:on]") {
        Summaries.getInternalRequestSummary.labels(action.toLowerCase, status.toString).observe(requestTime)
      }

      logger.info(s"Response: ${requestHeader.method} on ${requestHeader.uri} ($action), returned $status, and took ${(requestTime * 1000).intValue()} ms")

      result
    }
  }
}