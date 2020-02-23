package repositories

import com.google.inject.Inject
import executionContexts.DatabaseExecutionContext
import play.api.Logging
import play.api.db.Database

import scala.concurrent.Future

trait BasicAuthRepo {
  def isAuthenticated(username: String, password: String): Future[Boolean]
}

class PostgresBasicAuthRepo @Inject()(db: Database)(implicit ec: DatabaseExecutionContext) extends BasicAuthRepo with Logging {

  override def isAuthenticated(username: String, password: String): Future[Boolean] = Future {
    db.withConnection { conn =>
      val stmt = conn.prepareStatement(s"SELECT EXISTS (SELECT 1 FROM auth WHERE username = ? AND password = ?) AS exists")

      stmt.setString(1, username)
      stmt.setString(2, password)

      val rs = stmt.executeQuery()
      rs.next()

      rs.getBoolean("exists")
    }
  }.recover {
    case t: Throwable =>
      logger.error("Unexpected database error occured", t)
      false
  }
}
