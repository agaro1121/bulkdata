package tmt


import org.scalajs.dom.Event
import org.scalajs.dom.html.Image

import scala.scalajs.js

trait OnLoad extends js.Object {
  var onload: js.Function1[Event, _] = js.native
}

class CustomImage extends Image with OnLoad