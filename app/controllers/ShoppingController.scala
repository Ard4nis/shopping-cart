package controllers

import java.util.UUID

import com.google.inject.Inject
import io.prometheus.client.CollectorRegistry
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ShoppingService

import scala.concurrent.{ExecutionContext, Future}

class ShoppingController @Inject()(shoppingService: ShoppingService, registry: CollectorRegistry, cc: ControllerComponents)(implicit ex: ExecutionContext) extends AbstractController(cc) with Logging {

  def openBasket = Action.async {
    shoppingService.openBasket().map { id =>
      logger.info(s"Basket opened with identifier: $id")
      Ok(Json.toJson(Map("basket_id" -> id)))
    }.recover {
      case t: Throwable =>
        val errorMsg = s"Unexpected error occurred, message: ${t.getMessage}"
        logger.error(errorMsg)
        InternalServerError(errorMsg)
    }
  }

  def getBasket(basketId: UUID) = Action.async {
    shoppingService.getBasket(basketId).map { basket =>
      logger.info(s"Basket found with the following products: ${basket.products}")
      Ok(Json.toJson(basket))
    }.recover {
      case t: Throwable =>
        val errorMsg = s"Unexpected error occurred, message: ${t.getMessage}"
        logger.error(errorMsg)
        InternalServerError(errorMsg)
    }
  }

  def addToBasket(basketId: UUID, productId: UUID, amount: Int = 1) = Action.async {
    shoppingService.addToBasket(basketId, productId, amount).map { completed =>
      if (completed) {
        logger.info(s"$amount $productId added to basket $basketId")
        NoContent
      } else {
        BadRequest
      }
    }.recover {
      case t: Throwable =>
        val errorMsg = s"Unexpected error occurred, message: ${t.getMessage}"
        logger.error(errorMsg)
        InternalServerError(errorMsg)
    }
  }

  def removeFromBasket(basketId: UUID, productId: UUID) = Action.async {
    shoppingService.removeFromBasket(basketId, productId).map { completed =>
      if (completed) {
        logger.info(s"$productId removed from $basketId")
        NoContent
      } else {
        BadRequest
      }
    }.recover {
      case t: Throwable =>
        val errorMsg = s"Unexpected error occurred, message: ${t.getMessage}"
        logger.error(errorMsg)
        InternalServerError(errorMsg)
    }
  }

  def increaseAmount(basketId: UUID, productId: UUID, amount: Int) = Action.async {
    shoppingService.increaseAmount(basketId, productId, amount).map { completed =>
      if (completed) {
        logger.info(s"$productId amount increased with $amount in $basketId")
        NoContent
      } else {
        BadRequest
      }
    }.recover {
      case t: Throwable =>
        val errorMsg = s"Unexpected error occurred, message: ${t.getMessage}"
        logger.error(errorMsg)
        InternalServerError(errorMsg)
    }
  }
}
