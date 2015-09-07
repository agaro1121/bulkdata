package tmt.images

import monifu.concurrent.Implicits.globalScheduler
import org.scalajs.dom._
import tmt.common.{UiControls, Stream, SharedConfigs}

import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.typedarray.ArrayBuffer

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = UiControls.button.onclick = { e: Event =>
    val socket = new WebSocket(s"ws://${SharedConfigs.interface}:${SharedConfigs.port}/images/bytes")
    socket.binaryType = "arraybuffer"
    drain(socket)
  }

  def drain(socket: WebSocket) = Stream.socket(socket)
    .map(makeUrl)
    .map(new Rendering(_))
    .flatMap(_.loaded)
    .map(_.render())
    .buffer(1.second).map(_.size).foreach(println)

  def makeUrl(messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val properties = js.Dynamic.literal("type" -> "image/jpeg").asInstanceOf[BlobPropertyBag]
    val blob = new Blob(js.Array(arrayBuffer), properties)
    UiControls.URL.createObjectURL(blob)
  }
}