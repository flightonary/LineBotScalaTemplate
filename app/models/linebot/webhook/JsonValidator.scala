package models.linebot.webhook

import models.linebot.webhook.Objects._
import play.api.libs.json._

object JsonValidator {
  def validate(json: JsValue): Option[Seq[Event]] = {
    (json \ "events").asOpt[Array[JsValue]].map(_.flatMap(getEvent))
  }

  private def getEvent(jsEvent: JsValue): Option[Event] = {
    for {
      etype <- (jsEvent \ "type").asOpt[String]
      timestamp <- (jsEvent \ "timestamp").asOpt[Long]
      src <- (jsEvent \ "source").asOpt[JsValue].flatMap(getSource)
      event: Event <- EType withName etype match {
        case EType.message =>
          for {
            replyToken <- (jsEvent \ "replyToken").asOpt[String]
            msg: Message <- (jsEvent \ "message").asOpt[JsValue].flatMap(getMessage)
          } yield msg match {
            case txt: TextMessage => MessageEvent(timestamp, src, replyToken, txt)
            case img: ImageMessage => MessageEvent(timestamp, src, replyToken, img)
            case vid: VideoMessage => MessageEvent(timestamp, src, replyToken, vid)
            case aud: AudioMessage => MessageEvent(timestamp, src, replyToken, aud)
            case loc: LocationMessage => MessageEvent(timestamp, src, replyToken, loc)
            case stk: StickerMessage => MessageEvent(timestamp, src, replyToken, stk)
          }
        case EType.follow => (jsEvent \ "replyToken").asOpt[String].map(FollowEvent(timestamp, src, _))
        case EType.unfollow => Some(UnfollowEvent(timestamp, src))
        case EType.join => (jsEvent \ "replyToken").asOpt[String].map(JoinEvent(timestamp, src, _))
        case EType.leave => Some(LeaveEvent(timestamp, src))
        case EType.postback =>
          (jsEvent \ "postback" \ "data").asOpt[String].map(d => PostbackEvent(timestamp, src, Postback(d)))
        case EType.beacon =>
          for {
            hwid <- (jsEvent \ "beacon" \ "hwid").asOpt[String]
            btype <- (jsEvent \ "beacon" \ "type").asOpt[String]
            beacon: Beacon <- BType withName btype match {
              case BType.enter => Some(EnterBeacon(hwid))
            }
          } yield beacon match {
            case ebcn: EnterBeacon => BeaconEvent(timestamp, src, ebcn)
          }
        case _ => None //Unknown Event
      }
    } yield event
  }

  private def getSource(jsSrc: JsValue): Option[Source] = {
    for {
      stype <- (jsSrc \ "type").asOpt[String]
      src: Source <- SType withName stype match {
        case SType.user => (jsSrc \ "userId").asOpt[String].map(SourceUser)
        case SType.group => (jsSrc \ "groupId").asOpt[String].map(SourceGroup)
        case SType.room => (jsSrc \ "roomId").asOpt[String].map(SourceRoom)
        case _ => None //Unknown Source
      }
    } yield src
  }

  private def getMessage(jsMsg: JsValue): Option[Message] = {
    for {
      id <- (jsMsg \ "id").asOpt[String]
      mtype <- (jsMsg \ "type").asOpt[String]
      msg: Message <- MType withName mtype match {
        case MType.text => (jsMsg \ "text").asOpt[String].map(TextMessage(id, _))
        case MType.image => Some(ImageMessage(id))
        case MType.video => Some(VideoMessage(id))
        case MType.audio => Some(AudioMessage(id))
        case MType.location =>
          for {
            title <- (jsMsg \ "title").asOpt[String]
            address <- (jsMsg \ "address").asOpt[String]
            latitude <- (jsMsg \ "latitude").asOpt[Double]
            longitude <- (jsMsg \ "longitude").asOpt[Double]
          } yield LocationMessage(id, title, address, latitude, longitude)
        case MType.sticker =>
          for {
            packageId <- (jsMsg \ "packageId").asOpt[String]
            stickerId <- (jsMsg \ "stickerId").asOpt[String]
          } yield StickerMessage(id, packageId, stickerId)
        case _ => None //Unknown Message
      }
    } yield msg
  }
}
