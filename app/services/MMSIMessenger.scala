package services

import javax.inject._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

import play.api.libs.ws._
import play.api.libs.concurrent._

import scala.concurrent.Future

/**
 * This trait demonstrates how to create a component that is injected
 * into a controller. The trait represents a counter that returns a
 * incremented number each time it is called.
 */
trait MMSIMessenger {
  def getVessels(lat: Float, lon: Float, rad: Float): Future[WSResponse]
}

/**
 * This class is a concrete implementation of the [[Counter]] trait.
 * It is configured for Guice dependency injection in the [[Module]]
 * class.
 *
 * This class has a `Singleton` annotation because we need to make
 * sure we only use one counter per application. Without this
 * annotation we would get a new instance every time a [[Counter]] is
 * injected.
 */
@Singleton
class RealMMSIMessenger @Inject()
  (ws: WSClient) extends MMSIMessenger {
    override def getVessels(lat: Float, lon: Float, rad: Float): Future[WSResponse] = {
      val time = LocalDateTime.now(ZoneId.of("UTC"))
    
      val plop = time.minusMinutes(5).toString();

      val naks = s"https://meri.digitraffic.fi/api/v1/locations/latitude/${lat}/longitude/${lon}/radius/${rad}/from/${plop}Z"

      ws.url(naks).get()
    }
}