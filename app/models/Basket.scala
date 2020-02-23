package models

import java.sql.ResultSet
import java.util.UUID

import play.api.libs.json.Writes
import play.api.libs.json._

case class Basket(products: Seq[Product]) {
  def total: Int = {
    var totalPrice = 0

    products.foreach { product =>
      totalPrice += (product.price*product.amount)
    }

    totalPrice
  }
}

object Basket {
  implicit val basketWrites: Writes[Basket] = Json.writes[Basket]
}

case class BasketToProduct(id: UUID, productId: UUID, amount: Int)

object BasketToProduct {

  def apply(rs: ResultSet): Unit = {
    BasketToProduct(UUID.fromString(rs.getString("cart_id")), UUID.fromString(rs.getString("product_id")), rs.getInt("amount"))
  }
}

