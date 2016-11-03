package controllers

import javax.inject._

import play.api.mvc._

@Singleton
class WatchDogController @Inject() (webJarAssets: WebJarAssets) extends Controller {

  def index = Action {
    Ok("I'm line dev bot")
  }

}
