import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "WatchDogController" should {

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include ("I'm line dev bot")
    }

  }

  "LineBotController" should {
    "text message" in {
      val request = FakeRequest(POST, "/webhook").withJsonBody(Json.parse(
        """
          |{
          |  "events": [
          |    {
          |      "replyToken": "nHuyWiB7yP5Zw52FIkcQobQuGDXCTA",
          |      "type": "message",
          |      "timestamp": 1462629479859,
          |      "source": {
          |        "type": "user",
          |        "userId": "U206d25c2ea6bd87c17655609a1c37cb8"
          |      },
          |      "message": {
          |        "id": "325708",
          |        "type": "text",
          |        "text": "Hello, world"
          |      }
          |    }
          |  ]
          |}
        """.stripMargin))

      val webhook = route(app, request).get

      status(webhook) mustBe OK
    }
  }

}
