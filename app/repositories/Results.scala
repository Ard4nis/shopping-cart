package repositories

import java.sql.ResultSet

object Results {

  implicit class ResultSetMap(resultSet: ResultSet) {
    def map[T](f: ResultSet => T): Seq[T] = {
      new Iterator[T] {
        def hasNext: Boolean = resultSet.next()
        def next(): T = f(resultSet)
      }.toList //To list, because toSeq can treat it like a stream, and ResultSet gets closed before all rows are fetched
    }
  }
}
