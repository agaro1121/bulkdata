package tmt.transformations

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.concurrent.Executors
import javax.imageio.ImageIO
import javax.inject.Singleton

import akka.http.scaladsl.model.DateTime
import org.imgscalr.Scalr
import org.imgscalr.Scalr.Rotation
import tmt.app.AppSettings
import tmt.shared.models.Image

import scala.concurrent.ExecutionContext
import async.Async._

@Singleton
class ImageProcessor(appSettings: AppSettings) {

  private val imageProcessingEc = ExecutionContext.fromExecutorService(
    Executors.newFixedThreadPool(appSettings.imageProcessingThreadPoolSize)
  )

  def rotate(image: Image) = async {
    val bufferedImage = ImageIO.read(new ByteArrayInputStream(image.bytes))
    val rotatedBufferedImage = Scalr.rotate(bufferedImage, Rotation.FLIP_VERT)
    makeNewImage(image.name, rotatedBufferedImage)
  }(imageProcessingEc)

  private def makeNewImage(name: String, rotatedBufferedImage: BufferedImage): Image = {
    val baos = new ByteArrayOutputStream()
    ImageIO.write(rotatedBufferedImage, "jpeg", baos)
    baos.flush()
    val rotatedImage = Image(name, baos.toByteArray, DateTime.now.clicks)
    baos.close()
    rotatedImage
  }
}
