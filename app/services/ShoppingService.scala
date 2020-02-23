package services

import java.util.UUID

import com.google.inject.Inject
import models.Basket
import play.api.Logging
import repositories.ShoppingRepo

import scala.concurrent.Future

trait ShoppingService {
  def openBasket(): Future[UUID]

  def addToBasket(basketId: UUID, productId: UUID, amount: Int): Future[Boolean]

  def removeFromBasket(basketId: UUID, productId: UUID): Future[Boolean]

  def getBasket(basketId: UUID): Future[Basket]

  def increaseAmount(basketId: UUID, productId: UUID, amount: Int): Future[Boolean]

}

class ShoppingServiceImpl @Inject() (shoppingRepo: ShoppingRepo) extends ShoppingService with Logging {
  override def openBasket(): Future[UUID] = shoppingRepo.openBasket()

  override def addToBasket(basketId: UUID, productId: UUID, amount: Int): Future[Boolean] = shoppingRepo.addToBasket(basketId, productId, amount)

  override def removeFromBasket(basketId: UUID, productId: UUID): Future[Boolean] = shoppingRepo.removeFromBasket(basketId, productId)

  override def getBasket(basketId: UUID): Future[Basket] = shoppingRepo.getBasket(basketId)

  override def increaseAmount(basketId: UUID, productId: UUID, amount: Int): Future[Boolean] = shoppingRepo.increaseAmount(basketId, productId, amount)
}
