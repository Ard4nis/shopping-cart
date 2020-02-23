package controllers.info

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.Future

class HealthController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def health: Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(Map("status" -> "OK"))))
  }

}
