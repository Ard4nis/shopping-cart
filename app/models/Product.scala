package models

import java.sql.ResultSet

import play.api.libs.json.Writes
import play.api.libs.json.Json._
import play.api.libs.json._

case class Product(name: String, description: String, price: Int, amount: Int)

object Product {
  implicit val productWrites: Writes[Product] = (product: Product) => {
    Json.obj("name" -> product.name,
    "description" -> product.description,
    "price" -> product.price,
    "amount" -> product.amount)
  }

  def apply(rs: ResultSet): Product = {
    Product(rs.getString("name"), rs.getString("description"), rs.getInt("value"), rs.getInt("amount"))
  }
}
