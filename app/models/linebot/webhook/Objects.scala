package models.linebot.webhook

package object Objects {

  object EType extends Enumeration {
    val message, follow, unfollow, join, leave, postback, beacon = Value
  }

  object SType extends Enumeration {
    val user, group, room = Value
  }

  object MType extends Enumeration {
    val text, image, video, audio, location, sticker = Value
  }

  object BType extends Enumeration {
    val enter = Value
  }

  /*
    Event
   */
  sealed abstract class Event {
    val etype: EType.Value
    val timestamp: Long
    val src: Source
  }
  case class MessageEvent[+T <: Message](timestamp: Long, src: Source, replyToken: String, msg: T) extends Event {
    override val etype = EType.message
  }
  case class FollowEvent(timestamp: Long, src: Source, replyToken: String) extends Event {
    override val etype = EType.follow
  }
  case class UnfollowEvent(timestamp: Long, src: Source) extends Event {
    override val etype = EType.unfollow
  }
  case class JoinEvent(timestamp: Long, src: Source, replyToken: String) extends Event {
    override val etype = EType.join
  }
  case class LeaveEvent(timestamp: Long, src: Source) extends Event {
    override val etype = EType.leave
  }
  case class PostbackEvent(timestamp: Long, src: Source, postback: Postback) extends Event {
    override val etype = EType.postback
  }
  case class BeaconEvent[+T <: Beacon](timestamp: Long, src: Source, beacon: T) extends Event {
    override val etype = EType.beacon
  }

  /*
    Source
   */
  sealed abstract class Source {
    val stype: SType.Value
    def id: String
  }
  case class SourceUser(userId: String) extends Source {
    override val stype = SType.user
    override def id: String = userId
  }
  case class SourceGroup(groupId: String) extends Source {
    override val stype = SType.group
    override def id: String = groupId
  }
  case class SourceRoom(roomId: String) extends Source {
    override val stype = SType.room
    override def id: String = roomId
  }

  /*
    Message
   */
  sealed abstract class Message {
    val id: String
    val mtype: MType.Value
  }
  case class TextMessage(id: String, text: String) extends Message {
    override val mtype = MType.text
  }
  case class ImageMessage(id: String) extends Message {
    override val mtype = MType.image
  }
  case class VideoMessage(id: String) extends Message {
    override val mtype = MType.video
  }
  case class AudioMessage(id: String) extends Message {
    override val mtype = MType.audio
  }
  case class LocationMessage(id: String, title: String, address: String,
                             latitude: Double, longitude: Double) extends Message {
    override val mtype = MType.location
  }
  case class StickerMessage(id: String, packageId: String, stickerId: String) extends Message {
    override val mtype = MType.sticker
  }

  /*
    Postback
   */
  case class Postback(data: String)

  /*
    Beacon
   */
  sealed abstract class Beacon {
    val btype: BType.Value
    val hwid: String
  }
  case class EnterBeacon(hwid: String) extends Beacon {
    override val btype = BType.enter
  }
}
