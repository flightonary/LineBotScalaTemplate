package models.linebot.api

import play.api.libs.json._

package object SendObjects {
  object SOType extends Enumeration {
    val text, image, video, audio, location, sticker, imagemap, template = Value
  }

  sealed abstract class SendObject {
    val stype: SOType.Value

    def specificJson: JsObject
    def toJson: JsObject = Json.obj(
      "type" -> stype.toString
    ) ++ specificJson
  }

  case class TextSendObject(text: String) extends SendObject {
    override val stype = SOType.text
    override def specificJson: JsObject = Json.obj(
      "text" -> text
    )
  }

  case class ImageSendObject(originalContentUrl: String, previewImageUrl: String) extends SendObject {
    override val stype = SOType.image
    override def specificJson: JsObject = Json.obj(
      "originalContentUrl" -> originalContentUrl,
      "previewImageUrl" -> previewImageUrl
    )
  }

  case class VideoSendObject(originalContentUrl: String, previewImageUrl: String) extends SendObject {
    override val stype = SOType.video
    override def specificJson: JsObject = Json.obj(
      "originalContentUrl" -> originalContentUrl,
      "previewImageUrl" -> previewImageUrl
    )
  }

  case class AudioSendObject(originalContentUrl: String, duration: Int) extends SendObject {
    override val stype = SOType.audio
    override def specificJson: JsObject = Json.obj(
      "originalContentUrl" -> originalContentUrl,
      "duration" -> duration
    )
  }

  case class LocationSendObject(title: String, address: String, latitude: Double, longitude: Double) extends SendObject {
    override val stype = SOType.location
    override def specificJson: JsObject = Json.obj(
      "title" -> title,
      "address" -> address,
      "latitude" -> latitude,
      "longitude" -> longitude
    )
  }

  case class StickerSendObject(packageId: String, stickerId: String) extends SendObject {
    override val stype = SOType.sticker
    override def specificJson: JsObject = Json.obj(
      "packageId" -> packageId,
      "stickerId" -> stickerId
    )
  }

  case class ImagemapSendObject(baseUrl: String, altText: String,
                                height: Int, actions: Seq[ImagemapAction]) extends SendObject {
    override val stype = SOType.imagemap
    override def specificJson: JsObject = Json.obj(
      "baseUrl" -> baseUrl,
      "altText" -> altText,
      "baseSize" -> Json.obj(
        "width" -> 1040,
        "height" -> height
      ),
      "actions" -> actions.map(_.toJson)
    )
  }

  case class TemplateSendObject(altText: String, template: Template) extends SendObject {
    override val stype = SOType.template
    override def specificJson: JsObject = Json.obj(
      "altText" -> altText,
      "template" -> template.toJson
    )
  }

  /*
    ImagemapAction
   */
  object IAType extends Enumeration {
    val uri, message = Value
  }

  sealed abstract class ImagemapAction {
    val iatype: IAType.Value

    def specificJson: JsObject

    def toJson: JsObject = Json.obj(
      "type" -> iatype.toString
    ) ++ specificJson
  }

  case class URIImagemapAction(linkUri: String, area: ImagemapArea) extends ImagemapAction {
    override val iatype = IAType.uri
    override def specificJson: JsObject = Json.obj(
      "linkUri" -> linkUri,
      "area" -> area.toJson
    )
  }

  case class MessageImagemapAction(text: String, area: ImagemapArea) extends ImagemapAction {
    override val iatype = IAType.message
    override def specificJson: JsObject = Json.obj(
      "text" -> text,
      "area" -> area.toJson
    )
  }

  /*
    Imagemap Area
   */
  case class ImagemapArea(x: Int, y: Int, width: Int, height: Int) {
    def toJson: JsObject = Json.obj(
      "x" -> x,
      "y" -> y,
      "width" -> width,
      "height" -> height
    )
  }

  /*
    Template
   */
  object TType extends Enumeration {
    val buttons, confirm, carousel = Value
  }

  sealed abstract class Template {
    val ttype: TType.Value

    def specificJson: JsObject

    def toJson: JsObject = Json.obj(
      "type" -> ttype.toString
    ) ++ specificJson
  }

  case class ButtonsTemplate(thumbnailImageUrl: Option[String], title: Option[String],
                             text: String, actions: Seq[TemplateAction]) extends Template {
    override val ttype = TType.buttons
    override def specificJson: JsObject = Json.obj(
      "thumbnailImageUrl" -> thumbnailImageUrl,
      "title" -> title,
      "text" -> text,
      "actions" -> actions.take(4).map(_.toJson)
    )
  }

  case class ConfirmTemplate(text: String, actions: Seq[TemplateAction]) extends Template {
    override val ttype = TType.confirm
    override def specificJson: JsObject = Json.obj(
      "text" -> text,
      "actions" -> actions.take(2).map(_.toJson)
    )
  }

  case class CarouselTemplate(columns: Seq[TemplateAction]) extends Template {
    override val ttype = TType.carousel
    override def specificJson: JsObject = Json.obj(
      "columns" -> columns.take(5).map(_.toJson)
    )
  }

  case class Column(thumbnailImageUrl: Option[String], title: Option[String],
                             text: String, actions: Seq[TemplateAction]) {
    def toJson: JsObject = Json.obj(
      "thumbnailImageUrl" -> thumbnailImageUrl,
      "title" -> title,
      "text" -> text,
      "actions" -> actions.take(3).map(_.toJson)
    )
  }

  /*
    Template Action
   */
  object TAType extends Enumeration {
    val postback, message, uri = Value
  }

  sealed abstract class TemplateAction {
    val tatype: TAType.Value

    def specificJson: JsObject

    def toJson: JsObject = Json.obj(
      "type" -> tatype.toString
    ) ++ specificJson
  }

  case class PostbackTemplateAction(label: String, data: String, text: String) extends TemplateAction {
    override val tatype = TAType.postback
    override def specificJson: JsObject = Json.obj(
      "label" -> label,
      "data" -> data,
      "text" -> text
    )
  }

  case class MessageTemplateAction(label: String, text: String) extends TemplateAction {
    override val tatype = TAType.message
    override def specificJson: JsObject = Json.obj(
      "label" -> label,
      "text" -> text
    )
  }

  case class URITemplateAction(label: String, uri: String) extends TemplateAction {
    override val tatype = TAType.uri
    override def specificJson: JsObject = Json.obj(
      "label" -> label,
      "uri" -> uri
    )
  }

}
