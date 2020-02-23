package controllers.info

import java.io.StringWriter

import akka.util.ByteString
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import javax.inject.Inject
import play.api.http.HttpEntity
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, ResponseHeader, Result}

class MetricsController @Inject() (registry: CollectorRegistry, cc: ControllerComponents) extends AbstractController(cc) {

  def metrics: Action[AnyContent] = Action {
    val out = new StringWriter()
    TextFormat.write004(out, registry.metricFamilySamples())

    Result(
      header = ResponseHeader(OK, Map.empty),
      body = HttpEntity.Strict(ByteString(out.toString), Some(TextFormat.CONTENT_TYPE_004))
    )
  }

}
