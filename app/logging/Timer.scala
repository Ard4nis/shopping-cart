package logging

import io.prometheus.client.Summary

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

object Timer {

  def timeExternal[T](service: String, endpoint: String, future: Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val summary = Summaries.getExternalRequestSummary.labels(service, endpoint)
    monitor(summary, future) andThen {
      case Failure(_) => Summaries.getExternalErrorCounter.inc()
    }
    future
  }

  private def monitor[T](summary: Summary.Child, future: Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val timer = summary.startTimer()
    future onComplete { _ =>
      timer.observeDuration()
    }
    future
  }

}