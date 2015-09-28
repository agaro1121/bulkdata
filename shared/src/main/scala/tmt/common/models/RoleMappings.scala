package tmt.common.models

import boopickle.Default._

case class RoleMappings(mappings: Map[String, Seq[String]]) {
  def getServers(role: String) = mappings.getOrElse(role, Seq.empty)
  def roles = mappings.keySet.toSeq.sorted
}

object RoleMappings {
  implicit val pickler = generatePickler[RoleMappings]
}
