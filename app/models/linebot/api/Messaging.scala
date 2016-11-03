package models.linebot.api

import play.api.libs.json._
import play.api.libs.ws._
import scala.concurrent.Future
import SendObjects._

sealed trait Messaging {
  val cli: Client
  protected val resource: String
  protected val tokenKey: String

  protected def _send(token: String, objs: Seq[SendObject]): Future[WSResponse] = {
    val req = cli.createRequest(resource)
    val data = Json.obj(
      tokenKey -> token,
      "messages" -> objs.take(5).map(_.toJson)
    )
    req.post(data)
  }
}

final class Reply(val cli: Client) extends Messaging {
  override protected val resource = "message/reply"
  override protected val tokenKey = "replyToken"

  def send(replyToken: String, obj: Seq[SendObject]): Future[WSResponse] =
    this._send(replyToken, obj)
}

final class Push(val cli: Client) extends Messaging {
  override protected val resource = "message/push"
  override protected val tokenKey = "to"

  def send(to: String, obj: Seq[SendObject]): Future[WSResponse] =
    this._send(to, obj)
}