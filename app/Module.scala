import com.google.inject.AbstractModule
import io.prometheus.client.CollectorRegistry
import repositories.{BasicAuthRepo, PostgresBasicAuthRepo, PostgresShoppingRepo, ShoppingRepo}
import services.{ShoppingService, ShoppingServiceImpl}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[CollectorRegistry]).toInstance(CollectorRegistry.defaultRegistry)

    /** Services */
    bind(classOf[ShoppingService]).to(classOf[ShoppingServiceImpl])

    /** Repositories */
    bind(classOf[BasicAuthRepo]).to(classOf[PostgresBasicAuthRepo])
    bind(classOf[ShoppingRepo]).to(classOf[PostgresShoppingRepo])
  }

}
