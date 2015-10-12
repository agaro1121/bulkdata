package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import play.api.mvc.{Action, Controller}
import services.{ConnectionSetService, ClusterClientService, RoleMappingsService}
import templates.PageFactory
import tmt.shared.models.HostMappings
import upickle.default._

import scala.async.Async._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationLong

@Singleton
class StreamController @Inject()(
  appSettings: AppSettings,
  clusterClientService: ClusterClientService,
  roleMappingsService: RoleMappingsService,
  pageFactory: PageFactory,
  connectionSetService: ConnectionSetService
)(implicit ec: ExecutionContext) extends Controller {

  def streams() = Action {
    Ok(pageFactory.showcase(roleMappingsService.onlineRoleMappings).render)
  }

  def mappings() = Action {
    Ok(write(roleMappingsService.onlineRoleMappings))
  }

  def hosts() = Action {
    Ok(write(appSettings.hosts))
  }

  def connections() = Action.async {
    async {
      val connectionDataSet = await(connectionSetService.connectionSet)
      Ok(write(connectionDataSet))
    }
  }

  def throttle(serverName: String, delay: Long) = Action {
    clusterClientService.throttle(serverName, delay.millis)
    Accepted("ok")
  }

  def subscribe(serverName: String, topic: String) = Action {
    clusterClientService.subscribe(serverName, topic)
    Accepted("ok")
  }

  def unsubscribe(serverName: String, topic: String) = Action {
    clusterClientService.unsubscribe(serverName, topic)
    Accepted("ok")
  }
}
