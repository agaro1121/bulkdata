package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.shared.models._

import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

class SubscriptionView(dataStore: ViewData)(implicit ec: ExecutionContext) extends View {

  val topicName = Var("")
  val serverName = Var("")
  val consumers = dataStore.consumersOf(topicName)

  val connection = Rx(Connection(serverName(), topicName()))

  val connectionSet = Var(ConnectionSet.empty)

  Obs(dataStore.connectionSet) {
    connectionSet() = dataStore.connectionSet()
  }

  def frag = formGroup(
    label("Make Connection"),
    div(cls := "form-inline")(
      label("Select output server"),
      makeSelection(dataStore.producers, topicName)
    ),
    div(cls := "form-inline")(
      label("Select input server"),
      makeSelection(consumers, serverName)
    ),
    formControl(button)(onclick := {() => addConnection()})("Connect"),
    Rx {
      ul(id := "connections")(
        connectionSet().connections.toSeq.map { c  =>
          li(cls := "form-inline")(
            s"${c.topic} ===> ${c.server}",
            formControl(button)(onclick := {() => removeConnection(c)})("unsubscribe")
          )
        }
      )
    }
  )

  def addConnection() = {
    subscribe(connection()).onSuccess {
      case x if x.status == 202 =>  connectionSet() = connectionSet().add(connection())
    }
  }

  def removeConnection(connection: Connection) = {
    unsubscribe(connection)
    connectionSet() = connectionSet().remove(connection)
  }

  def subscribe(connection: Connection) = Ajax.post(s"${connection.server}/subscribe/${connection.topic}")

  def unsubscribe(connection: Connection) = Ajax.post(s"${connection.server}/unsubscribe/${connection.topic}")
}
