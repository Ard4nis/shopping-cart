package models

import java.sql.ResultSet
import java.util.UUID

import play.api.libs.json.Writes
import play.api.libs.json.Json._
import play.api.libs.json._

case class Product(id: UUID, name: String, description: String, price: Int, amount: Int)

object Product {
  implicit val productWrites: Writes[Product] = (product: Product) => {
    Json.obj("id" -> product.id,
      "name" -> product.name,
    "description" -> product.description,
    "price" -> product.price,
    "amount" -> product.amount)
  }

  def apply(rs: ResultSet): Product = {
    Product(UUID.fromString(rs.getString("product_id")), rs.getString("name"), rs.getString("description"), rs.getInt("value"), rs.getInt("amount"))
  }
}
