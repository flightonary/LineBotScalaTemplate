package controllers

import javax.inject._

import scala.concurrent.ExecutionContext
import scala.util._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.{Configuration, Logger}
import models.linebot.api.SendObjects._
import models.linebot.app.LineBotApplication
import models.linebot.webhook.Objects._


@Singleton
class LineBotController @Inject() (conf: Configuration, val ws: WSClient, implicit val ec: ExecutionContext)
  extends Controller with LineBotApplication {

  val logger = Logger("linebot")

  override val channel_secret: String = conf.getString("linebot.channel.secret").get
  override val channel_access_token: String = conf.getString("linebot.channel.access_token").get

  def webhook = Action { request =>
    process_webhook(request)
    Ok
  }

  val encourageReplies = List("そんなことないよ", "大丈夫だよ", "心配しすぎ", "気にしすぎ")
  override def onTextMessage = { event: MessageEvent[TextMessage] =>
    import scala.util.Random
    if (event.msg.text.matches(""".*かな$""")) {
      val reply = TextSendObject(Random.shuffle(encourageReplies).head)
      ReplyAPI.send(event.replyToken, List(reply)).onComplete {
        case Success(resp) => logger.debug(resp.toString)
        case Failure(failure) => logger.debug(failure.toString)
      }
    }
  }

}