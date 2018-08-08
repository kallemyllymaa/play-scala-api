package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.concurrent._
import play.api.libs.json._

import play.api.libs.functional.syntax._

import scala.concurrent.ExecutionContext

import services.MMSIMessenger

case class Coordinates(lat: Float, lon: Float)

object Coordinates {
  
  implicit val coordinatesReads: Reads[Coordinates] = (
    (JsPath)(0).read[Float] and
    (JsPath)(1).read[Float]
  )(Coordinates.apply _)

  implicit val coordinatesWrite: Writes[Coordinates] = (
    (JsPath \ "lat").write[Float] and
    (JsPath \ "lon").write[Float]
  )(unlift(Coordinates.unapply))

}

case class Feature(mmsi: Int, typer: String, coordinates: Coordinates)

object Feature {
  
  implicit val featureReads: Reads[Feature] = (
    (JsPath \ "mmsi").read[Int] and
    (JsPath \ "type").read[String] and
    (JsPath \ "geometry" \ "coordinates").read[Coordinates]
  )(Feature.apply _)

  implicit val featureWrites: Writes[Feature] = (
    (JsPath \ "mmsi").write[Int] and
    (JsPath \ "type").write[String] and
    (JsPath \ "coordinates").write[Coordinates]
  )(unlift(Feature.unapply))

}

case class MarineResponse(typez: String, features: Seq[Feature])

object MarineResponse {
  
  implicit val marineResponseReads: Reads[MarineResponse] = (
    (JsPath \ "type").read[String] and
    (JsPath \ "features").read[Seq[Feature]]
  )(MarineResponse.apply _)
  
  implicit val marineResponseWrites: Writes[MarineResponse] = (
    (JsPath \ "type").write[String] and
    (JsPath \ "features").write[Seq[Feature]]
  )(unlift(MarineResponse.unapply))
}

@Singleton
class MMSIController @Inject() (
  cc: ControllerComponents,
  mmsiMessenger: MMSIMessenger) (implicit val ec: ExecutionContext) extends AbstractController(cc) {

  def index(lat: Float, lon: Float, rad: Float) = Action.async {
    mmsiMessenger.getVessels(lat,lon,rad).map {
      response => {
        Ok(Json.toJson(response.json.validate[MarineResponse].get))
      }
    }
  }
}
