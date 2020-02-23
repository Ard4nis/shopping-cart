package repositories

import java.util.UUID

import com.google.inject.Inject
import executionContexts.DatabaseExecutionContext
import io.prometheus.client.CollectorRegistry
import logging.Timer.timeExternal
import models.{Basket, BasketToProduct, Product}
import org.postgresql.util.PSQLException
import play.api.Logging
import play.api.db.Database
import repositories.Results._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait ShoppingRepo {
  def openBasket(): Future[UUID]

  def addToBasket(basketId: UUID, productId: UUID, amount: Int): Future[Boolean]

  def removeFromBasket(basketId: UUID, productId: UUID): Future[Boolean]

  def getBasket(basketId: UUID): Future[Basket]

  def increaseAmount(basketId: UUID, productId: UUID, amount: Int): Future[Boolean]

}

class PostgresShoppingRepo @Inject()(db: Database, registry: CollectorRegistry)(implicit ec: DatabaseExecutionContext) extends ShoppingRepo with Logging {


  override def openBasket(): Future[UUID] = timeExternal("db", "open_basket", Future {
    db.withTransaction { conn =>
      val id = UUID.randomUUID()
      val stmt = conn.prepareStatement(s"INSERT INTO cart (id) VALUES (?) RETURNING *")

      stmt.setObject(1, id)

      val resultSet = stmt.executeQuery()
      resultSet.next()
      val returnedId = UUID.fromString(resultSet.getString("id"))

      returnedId
    }
  }.recover {
    case t: Throwable =>
      logger.error(s"Unexpected database error occurred, message: ${t.getMessage}")
      throw t
  })

  override def addToBasket(basketId: UUID, productId: UUID, amount: Int): Future[Boolean] = timeExternal("db", "add_to_basket", Future {
    db.withTransaction { conn =>
      val stmt = conn.prepareStatement(s"INSERT INTO cart_to_product (cart_id, product_id, amount) VALUES (?, ?, ?) RETURNING *")

      stmt.setObject(1, basketId)
      stmt.setObject(2, productId)
      stmt.setInt(3, amount)

      val resultSet = stmt.executeQuery()
      val addedToBasket = resultSet.map { rs =>
        BasketToProduct(rs)
      }

      addedToBasket.headOption match {
        case Some(_) => true
        case None =>
          logger.error(s"Unexpected exception. No rows returned from ResultSet.")
          false
      }
    }
  }.recover {
    case sql: PSQLException => sql.getServerErrorMessage.getSQLState match {
      case "23505" =>
        logger.error(s"Product already exists in basket")
        throw sql
      case "23503" =>
        logger.error(s"Basket doesn't exist")
        throw sql
      case _ =>
        logger.error("Unexpected SQL error occurred")
        throw sql
    }
    case t: Throwable =>
      logger.error(s"Unexpected database error occurred, message: ${t.getMessage}")
      throw t
  })

  override def removeFromBasket(basketId: UUID, productId: UUID): Future[Boolean] = timeExternal("db", "remove_from_basket", Future {
    db.withTransaction { conn =>
      val stmt = conn.prepareStatement(s"DELETE FROM cart_to_product WHERE cart_id = ? AND product_id = ? RETURNING *")

      stmt.setObject(1, basketId)
      stmt.setObject(2, productId)

      val resultSet = stmt.executeQuery()
      val basketToProduct = resultSet.map { rs =>
        BasketToProduct(rs)
      }

      basketToProduct.headOption match {
        case Some(_) => true
        case None =>
          logger.error(s"No product $productId in basket $basketId found")
          false
      }
    }
  }.recover {
    case t: Throwable =>
      logger.error(s"Unexpected database error occurred, message: ${t.getMessage}")
      throw t
  })

  override def getBasket(basketId: UUID): Future[Basket] = timeExternal("db", "get_basket", Future {
    db.withConnection { conn =>
      val stmt = conn.prepareStatement(s"SELECT product_id, name, description, value, amount FROM product, cart_to_product WHERE product_id = id AND cart_id = ?")

      stmt.setObject(1, basketId)

      val resultSet = stmt.executeQuery()

      val products = resultSet.map { rs =>
        Product(rs)
      }

      products.headOption match {
        case Some(_) =>
          Basket(products)
        case None =>
          logger.info(s"No products found for basket $basketId")
          Basket(Seq())
      }
    }
  }.recover {
    case t: Throwable =>
      logger.error(s"Unexpected database error occurred, message: ${t.getMessage}")
      throw t
  })

  override def increaseAmount(basketId: UUID, productId: UUID, amount: Int): Future[Boolean] = timeExternal("db", "increase_amount", Future {
    db.withConnection { conn =>
      val stmt = conn.prepareStatement(s"UPDATE cart_to_product SET amount = amount + ? WHERE cart_id = ? AND product_id = ? RETURNING *")

      stmt.setInt(1, amount)
      stmt.setObject(2, basketId)
      stmt.setObject(3, productId)

      val resultSet = stmt. executeQuery()

      val increasedAmount = resultSet.map { rs =>
        BasketToProduct(rs)
      }

      increasedAmount.headOption match {
        case Some(_) => true
        case None => false
      }
    }
  }.recover {
    case t: Throwable =>
      logger.error(s"Unexpected database error occurred, message: ${t.getMessage}")
      throw t
  })
}
