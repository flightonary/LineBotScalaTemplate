package models.linebot.api

import play.api.libs.ws._
import scala.concurrent.duration._

class Client(access_token: String, ws: WSClient) {
  val base_url: String = "https://api.line.me/v2/bot/"

  def createRequest(resrc: String) = {
    ws.url(base_url + "/" + resrc)
      .withHeaders(
        "Content-Type" -> "application/json",
        "Authorization" -> s"Bearer ${access_token}"
      )
      .withRequestTimeout(10000 millis)
  }
}