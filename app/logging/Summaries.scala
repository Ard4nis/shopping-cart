package logging

import io.prometheus.client.{CollectorRegistry, Counter, Summary}

object Summaries {

  private val registry = CollectorRegistry.defaultRegistry
  registry.clear()

  /*
    Metrics for internal request timing. It is, and should only be used to measure complete time from incoming request to outgoing response.
    Is used primarily in the TimingFilter class, to time all requests without having to wrap each controller method with a timing method.
   */
  private val internalRequestSummary = createSummary("internal_req", "summaries for timing complete request times internally", "action", "status")
  def getInternalRequestSummary: Summary = internalRequestSummary

  /*
    Metrics for external services
   */
  private val externalErrorCounter = createCounter("external_errors", "counter for external services errors, picked up if a future has failed, not if response status is a non-200")
  def getExternalErrorCounter: Counter = externalErrorCounter

  private val externalRequestSummary = createSummary("external_req", "summaries for timing external service requests", "service", "action")
  def getExternalRequestSummary: Summary = externalRequestSummary

  /*
    Private methods for creating various metrics
   */
  private def createSummary(name: String, help: String, labelNames: String*): Summary = {
    val summaryName = s"shopping_cart_$name"
    internalCreateSummary(summaryName, help, labelNames: _*).register(registry)
  }

  private def createCounter(name: String, help: String): Counter = {
    val counterName = s"shopping_cart_$name"
    Counter.build(counterName, help).register(registry)
  }

  private def internalCreateSummary(name: String, help: String, labelNames: String*) = {
    Summary.build(name, help)
      .quantile(0.5, 0.05).quantile(0.9, 0.01).quantile(0.99, 0.001)
      .labelNames(labelNames: _*)
  }
}
