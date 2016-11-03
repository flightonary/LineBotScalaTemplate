package models.linebot.app

import play.api.mvc.{AnyContent, Request}
import play.api.libs.ws.WSClient
import models.linebot.webhook.Objects._
import models.linebot.api._
import models.linebot.webhook.{JsonValidator, SignatureVerifier}

trait LineBotApplication extends EventDispatcher[Unit] with MessagingAPI {
  val channel_secret: String
  val channel_access_token: String
  val ws: WSClient

  lazy val _cli: Client = new Client(channel_access_token, ws)
  lazy val ReplyAPI: Reply = new Reply(_cli)
  lazy val PushAPI: Push = new Push(_cli)

  def process_webhook(request: Request[AnyContent]): Unit = {
    for {
      signature <- request.headers.get("X-Line-Signature")
      json <- request.body.asJson
      if SignatureVerifier.verify(channel_secret, json.toString, signature)
      events <- JsonValidator.validate(json)
    } yield {
      events.foreach(dispatch)
    }
  }

  override def defaultBehavior = {e: Event => Unit}
}

trait MessagingAPI {
  val _cli: Client
  val ReplyAPI: Reply
  val PushAPI: Push
}

trait EventDispatcher[T] {
  def dispatch(event: Event): T = {
    event match {
      case msgEve: MessageEvent[_] =>
        msgEve.msg match {
          case _: TextMessage => onTextMessage(event.asInstanceOf[MessageEvent[TextMessage]])
          case _: ImageMessage => onImageMessage(event.asInstanceOf[MessageEvent[ImageMessage]])
          case _: VideoMessage => onVideoMessage(event.asInstanceOf[MessageEvent[VideoMessage]])
          case _: AudioMessage => onAudioMessage(event.asInstanceOf[MessageEvent[AudioMessage]])
          case _: LocationMessage => onLocationMessage(event.asInstanceOf[MessageEvent[LocationMessage]])
          case _: StickerMessage => onStickerMessage(event.asInstanceOf[MessageEvent[StickerMessage]])
        }
      case _: FollowEvent => onFollowEvent(event.asInstanceOf[FollowEvent])
      case _: UnfollowEvent => onUnfollowEvent(event.asInstanceOf[UnfollowEvent])
      case _: JoinEvent => onJoinEvent(event.asInstanceOf[JoinEvent])
      case _: LeaveEvent => onLeaveEvent(event.asInstanceOf[LeaveEvent])
      case _: PostbackEvent => onPostbackEvent(event.asInstanceOf[PostbackEvent])
      case bcnEve: BeaconEvent[_] =>
        bcnEve.beacon match {
          case _: EnterBeacon => onEnterBeaconEvent(event.asInstanceOf[BeaconEvent[EnterBeacon]])
        }
    }
  }

  def onTextMessage: (MessageEvent[TextMessage] => T) = defaultBehavior
  def onImageMessage: (MessageEvent[ImageMessage] => T) = defaultBehavior
  def onVideoMessage: (MessageEvent[VideoMessage] => T) = defaultBehavior
  def onAudioMessage: (MessageEvent[AudioMessage] => T) = defaultBehavior
  def onLocationMessage: (MessageEvent[LocationMessage] => T) = defaultBehavior
  def onStickerMessage: (MessageEvent[StickerMessage] => T) = defaultBehavior
  def onFollowEvent: (FollowEvent => T) = defaultBehavior
  def onUnfollowEvent: (UnfollowEvent => T) = defaultBehavior
  def onJoinEvent: (JoinEvent => T) = defaultBehavior
  def onLeaveEvent: (LeaveEvent => T) = defaultBehavior
  def onPostbackEvent: (PostbackEvent => T) = defaultBehavior
  def onEnterBeaconEvent: (BeaconEvent[EnterBeacon] => T) = defaultBehavior

  def defaultBehavior: (Event => T)
}