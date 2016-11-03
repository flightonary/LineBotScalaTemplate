package models.linebot.webhook

import org.scalatest._
import models.linebot.webhook.Objects._
import play.api.libs.json.Json

class JsonValidatorTest extends FunSuite with Matchers {

  test("testParse") {
    val events = JsonValidator.validate(Json.parse("""{
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
                       |    },
                       |    {
                       |      "replyToken": "nHuyWiB7yP5Zw52FIkcQobQuGDXCTA",
                       |      "type": "follow",
                       |      "timestamp": 1462629479859,
                       |      "source": {
                       |        "type": "user",
                       |        "userId": "U206d25c2ea6bd87c17655609a1c37cb8"
                       |      }
                       |    }
                       |  ]
                       |}""".stripMargin))

    events should not be None
    events.get.length shouldBe 2
    events.get.head shouldBe a [MessageEvent[_]]
  }

}
