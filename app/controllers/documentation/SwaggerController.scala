package controllers.documentation

import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.Future

class SwaggerController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def redirectToSwagger: Action[AnyContent] = Action.async {
    Future.successful(Redirect(url = "/assets/lib/swagger-ui/index.html", queryString = Map("url" -> Seq("/assets/swagger.json"))))
  }

}
