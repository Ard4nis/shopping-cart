import filters.{BasicAuthFilter, TimingFilter}
import javax.inject.Inject
import play.api.http.HttpFilters
import play.api.mvc.Filter

class Filters @Inject()(timingFilter: TimingFilter, basicAuthFilter: BasicAuthFilter) extends HttpFilters {

  def filters: Seq[Filter] = Seq(timingFilter, basicAuthFilter)

}
