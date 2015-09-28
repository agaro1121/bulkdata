package models

import com.google.inject.Singleton
import com.typesafe.config.ConfigObject
import library.ConfigValueExtensions.RichConfigValue
import play.api.libs.json.JsObject
import tmt.common.models.RoleMappings

@Singleton
class RoleMappingsFactory {
  def fromConfig(bindings: ConfigObject) = RoleMappings {
    val entries = bindings.as[JsObject].value.toSeq
    entries
      .collect {
       case (k, v: JsObject) => Mapping((v \ "role").as[String], k)
      }
      .groupBy(_.role)
      .mapValues(_.map(_.serverName))
  }

  private case class Mapping(role: String, serverName: String)
}
