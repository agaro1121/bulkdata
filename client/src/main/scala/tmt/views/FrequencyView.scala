package tmt.views

import monifu.concurrent.Scheduler
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.metrics.MetricsRendering

import scalatags.JsDom.all._

class FrequencyView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {
  val metricsRendering = new MetricsRendering(viewData)

  def frag = div(
    makeForm("Select frequency computing node", viewData.frequencyServers, metricsRendering),
    span(metricsRendering.frequency)
  )
}
